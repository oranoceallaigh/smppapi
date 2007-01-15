package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.PropertyNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @version $Id$
 */
public class ThreadedEventDispatcher extends AbstractEventDispatcher implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadedEventDispatcher.class);

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
        LOG.debug("Shutting down dispatch threads.");

        // This could happen if an application attempts to set a new event
        // dispatcher during event processing. There are probably many other
        // ways this call-back could happen but it shouldn't!
        if (Thread.currentThread().getThreadGroup() == threadPool) {
            LOG.error("Cannot shut down the thread pool with one of it's own threads.");
            throw new RuntimeException();
        }

        running = false;
        synchronized (queue) {
            queue.notifyAll();
        }

        LOG.info("Waiting for threads in pool to die.");
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
                    LOG.debug("There's still some threads running. Doing another loop..");
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
                threadPool.interrupt();
                Thread.yield();
            }
        }
        if (threadPool.activeCount() > 0) {
            LOG.error("{} dispatcher threads refused to die.",
                    threadPool.activeCount());
            if (LOG.isDebugEnabled()) {
                Thread[] threads = new Thread[threadPool.activeCount()];
                threadPool.enumerate(threads, false);
                for (int i = 0; i < pool.length; i++) {
                    LOG.debug(pool[i].getName());
               }
            }
        }
    }

    // notifyObservers is always single-threaded access as there's only 1
    // receiver thread!
    public void notifyObservers(Connection conn, SMPPEvent e) {
        LOG.debug("Notifying observers of a new SMPP event {}", e.getType());
        queue.put(conn, e);
        if (threadsWaiting > 0) {
            synchronized (queue) {
                queue.notify();
            }
        }
    }

    public void notifyObservers(Connection conn, SMPPPacket pak) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Notifying observers of a new SMPP packet ({}, {})",
                    Integer.toHexString(pak.getCommandId()),
                    Integer.toString(pak.getSequenceNum()));
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
        try {
            LOG.debug("Thread start: {}", Thread.currentThread().getName());
            while (running) {
                nd = null;
                try {
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            threadsWaiting++;
                            queue.wait();
                            threadsWaiting--;
                       }
    
                        nd = queue.get();
                   }
                } catch (InterruptedException x) {
                    continue;
                }

                if (nd == null) {
                    continue;
                }
                ConnectionObserver[] observers = getObserverList();
                for (ConnectionObserver observer : observers) {
                    if (nd.hasEvent()) {
                        observer.packetReceived(nd.getConnection(), nd.getPacket());
                    } else {
                        observer.update(nd.getConnection(), nd.getEvent());
                    }
                }
            } // end while

            LOG.debug("Thread exit: {}", Thread.currentThread().getName());
        } catch (Exception x) {
            LOG.warn("Exception in dispatcher thread", x);
        }
    }
}
