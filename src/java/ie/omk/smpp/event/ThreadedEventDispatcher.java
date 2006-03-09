package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.PropertyNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An event dispatcher that does not block the receiver daemon thread.
 * <p>
 * <font size="+2"> <b>This class is highly experimental </b> </font>.
 * </p>
 * This dispatcher class has a pool of dispatcher threads. When an event is
 * delivered to this dispatcher by the receiver thread, it is added to a queue
 * and the method returns immediately. One dispatcher thread is then selected
 * and that thread is responsible for delivering the event to all registered
 * observers. The receiver daemon is then free to continue adding new events to
 * the event queue, which will be processed by a thread in the thread pool.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class ThreadedEventDispatcher implements EventDispatcher, Runnable {

    private static final Log LOGGER = LogFactory.getLog(ThreadedEventDispatcher.class);

    /**
     * Runner flag. If set to false, all dispatcher threads will exit on next
     * iteration (some may be blocked on the queue).
     */
    private boolean running = true;

    /**
     * Size of the thread pool.
     */
    private int poolSize;

    /**
     * Pool of event dispatcher threads.
     */
    private ThreadGroup threadPool = new ThreadGroup("DispatcherPool");

    /**
     * FIFO queue of packets and SMPP events.
     */
    private FIFOQueue queue;

    /**
     * Number of threads currently blocked on the queue.
     */
    private int threadsWaiting;

    /**
     * List of observers registered for event delivery.
     */
    private List observers = new ArrayList();

    /**
     * Create a new threaded event dispatcher object.
     */
    public ThreadedEventDispatcher() {
    }

    /**
     * Initialise this event dispatcher. This method will retrieve the size of
     * the thread pool and FIFO queue from the API configuration and initialise
     * both. See {@link ie.omk.smpp.util.APIConfig}class documentation for the
     * appropriate configuration properties to use. If the properties are not
     * found in the configuration, the current defaults are a thread pool size
     * of <code>3</code> and a FIFO queue size of <code>100</code>.
     */
    public void init() {
        int queueSize;
        try {
            APIConfig cfg = APIConfig.getInstance();
            poolSize = cfg.getInt(APIConfig.EVENT_THREAD_POOL_SIZE);
            queueSize = cfg.getInt(APIConfig.EVENT_THREAD_FIFO_QUEUE_SIZE);
        } catch (PropertyNotFoundException x) {
            poolSize = 3;
            queueSize = 100;
        }

        // The queue must be created before the thread pool is initialised!
        queue = new FIFOQueue(queueSize);
        initialiseThreadPool();
    }

    private void initialiseThreadPool() {
        Thread t;
        for (int i = 0; i < poolSize; i++) {
            t = new Thread(threadPool, this, "EventDispatch" + i);
            t.start();
        }
    }

    /**
     * Shut down all threads in the thread pool. This method will block until
     * all threads have terminated properly. Applications should be careful not
     * to use one of the thread pool's own threads to call this method as this
     * will cause a runtime exception. How can this method wait for all the
     * pool's threads to die if one of the pool's threads is executing this
     * method?
     */
    public void destroy() {
        LOGGER.debug("Shutting down dispatch threads.");

        // This could happen if an application attempts to set a new event
        // dispatcher during event processing. There are probably many other
        // ways this call-back could happen but it shouldn't!
        if (Thread.currentThread().getThreadGroup() == threadPool) {
            LOGGER.error("Cannot shut down the thread pool with one of it's own threads.");
            throw new RuntimeException();
        }

        running = false;
        synchronized (queue) {
            queue.notifyAll();
        }

        LOGGER.info("Waiting for threads in pool to die.");
        final int waitTime = 50;
        // Allow a full second of waiting!
        final int times = 1000 / waitTime;
        int time = 0;
        Thread[] pool = new Thread[poolSize];
        while (true) {
            try {
                pool[0] = null;
                threadPool.enumerate(pool, false);

                if (pool[0] == null) {
                    break;
                } else {
                    LOGGER.debug("There's still some threads running. Doing another loop..");
                }

                // Break out if it looks like we're stuck in an infinite loop
                if (time >= times) {
                    break;
                }

                // What's a good time to wait for more threads to terminate?
                Thread.sleep(waitTime);
                synchronized (queue) {
                    queue.notifyAll();
               }
            } catch (InterruptedException x) {
                // TODO: check if i need to do anything here.
            }
        }

        if (pool[0] != null) {
            forceThreadExit();
        }
    }

    private void forceThreadExit() {
        LOGGER.debug("Interrupting all remaining dispatcher threads.");

        // this should wake any threads blocked on an object or sleeping..
        threadPool.interrupt();

        try {
            // 500 milliseconds - an eternity
            Thread.sleep(500);
        } catch (InterruptedException x) {
        }

        synchronized (queue) {
            queue.notifyAll();
        }

        if (threadPool.activeCount() > 0) {
            LOGGER
                    .error("Some dispatcher threads are refusing to die. I give up.");
            if (LOGGER.isDebugEnabled()) {
                Thread[] pool = new Thread[threadPool.activeCount()];
                threadPool.enumerate(pool, false);
                LOGGER.debug("Still-active threads:");
                for (int i = 0; i < pool.length; i++) {
                    LOGGER.debug("  " + pool[i].getName());
               }
            }
        }
    }

    public void addObserver(ConnectionObserver observer) {
        synchronized (observers) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }
    }

    public void removeObserver(ConnectionObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public Iterator observerIterator() {
        return Collections.unmodifiableList(observers).iterator();
    }

    public boolean contains(ConnectionObserver observer) {
        synchronized (observers) {
            return observers.contains(observer);
        }
    }

    // notifyObservers is always single-threaded access as there's only 1
    // receiver thread!
    public void notifyObservers(Connection conn, SMPPEvent e) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Notifying observers of a new SMPP event "
                    + e.getType());
        }

        queue.put(conn, e);
        if (threadsWaiting > 0) {
            synchronized (queue) {
                queue.notify();
            }
        }
    }

    public void notifyObservers(Connection conn, SMPPPacket pak) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Notifying observers of a new SMPP packet ("
                    + Integer.toHexString(pak.getCommandId()) + ","
                    + Integer.toString(pak.getSequenceNum()) + ")");
        }
        queue.put(conn, pak);
        if (threadsWaiting > 0) {
            synchronized (queue) {
                queue.notify();
            }
        }
    }

    public void run() {
        NotificationDetails nd;
        ConnectionObserver observer;

        try {
            LOGGER.debug("Thread " + Thread.currentThread().getName()
                    + " started");

            while (running) {
                nd = null;
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        threadsWaiting++;
                        queue.wait();
                        threadsWaiting--;
                   }

                    nd = queue.get();
               }

                if (nd == null) {
                    continue;
                }

                for (int i = observers.size() - 1; i >= 0; i--) {
                    observer = (ConnectionObserver) observers.get(i);
                    if (nd.hasEvent()) {
                        observer.packetReceived(
                                nd.getConnection(), nd.getPacket());
                    } else {
                        observer.update(
                                nd.getConnection(), nd.getEvent());
                    }
                }
            } // end while

            LOGGER.debug("Thread " + Thread.currentThread().getName()
                    + " exiting");
        } catch (Exception x) {
            LOGGER.warn("Exception in dispatcher thread", x);
        }
    }
}

