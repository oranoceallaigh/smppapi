
package ie.omk.smpp.event;

import ie.omk.smpp.Connection;

import ie.omk.smpp.message.SMPPPacket;

import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.PropertyNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/** An event dispatcher that does not block the receiver daemon thread.
 * <p>
 * <font size="+2"><b>This class is highly experimental</b></font>.
 * </p>
 * This dispatcher class has a pool of dispatcher threads. When an event is
 * delivered to this dispatcher by the receiver thread, it is added to a queue
 * and the method returns immediately. One dispatcher thread is then selected
 * and that thread is responsible for delivering the event to all registered
 * observers.  The receiver daemon is then free to continue adding new events to
 * the event queue, which will be processed by a thread in the thread pool.
 * @author Oran Kelly
 */
public class ThreadedEventDispatcher implements EventDispatcher, Runnable {

    private Logger logger = Logger.getLogger(DEFAULT_LOGGER_NAME);

    /** Runner flag. If set to false, all dispatcher threads will exit on next
     * iteration (some may be blocked on the queue).
     */
    private boolean running = true;

    /** Size of the thread pool.
     */
    private int poolSize = 0;

    /** Pool of event dispatcher threads.
     */
    private ThreadGroup threadPool = new ThreadGroup("DispatcherPool");

    /** FIFO queue of packets and SMPP events.
     */
    private FIFOQueue queue = null;

    /** Number of threads currently blocked on the queue.
     */
    private int threadsWaiting = 0;

    /** List of observers registered for event delivery.
     */
    private ArrayList observers = new ArrayList();


    /** Create a new threaded event dispatcher object.
     */
    public ThreadedEventDispatcher() {
    }

    /** Initialise this event dispatcher. This method will retrieve the size of
     * the thread pool and FIFO queue from the API configuration and initialise
     * both. See {@link ie.omk.smpp.util.APIConfig} class documentation for the
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

    /** Shut down all threads in the thread pool. This method will block until
     * all threads have terminated properly. Applications should be careful not
     * to use one of the thread pool's own threads to call this method as this
     * will cause a runtime exception. How can this method wait for all the
     * pool's threads to die if one of the pool's threads is executing this
     * method?
     */
    public void destroy() {
	logger.debug("Shutting down dispatch threads.");

	// This could happen if an application attempts to set a new event
	// dispatcher during event processing. There are probably many other
	// ways this call-back could happen but it shouldn't!
	if (Thread.currentThread().getThreadGroup() == threadPool) {
	    logger.error("Cannot shut down the thread pool with one of it's own threads.");
	    throw new RuntimeException();
	}

	running = false;
	synchronized (queue) {
	    queue.notifyAll();
	}

	logger.info("Waiting for threads in pool to die.");
	final int waitTime = 50;
	final int times = 1000 / waitTime; // allow a full second of waiting!
	int time = 0;
	Thread[] pool = new Thread[poolSize];
	while (true) {
	    try {
		pool[0] = null;
		threadPool.enumerate(pool, false);

		if (pool[0] == null)
		    break;
		else
		    logger.debug("There's still some threads running. Doing another loop..");

		// Break out if it looks like we're stuck in an infinite loop
		if (time >= times)
		    break;

		// What's a good time to wait for more threads to terminate?
		Thread.sleep(waitTime);
		synchronized (queue) {
		    queue.notifyAll();
		}
	    } catch (InterruptedException x) {
	    }
	}

	if (pool[0] != null)
	    forceThreadExit();
    }


    private void forceThreadExit() {
	logger.debug("Interrupting all remaining dispatcher threads.");

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
	    logger.error("Some dispatcher threads are refusing to die. I give up.");
	    if (logger.isDebugEnabled()) {
		Thread[] pool = new Thread[threadPool.activeCount()];
		threadPool.enumerate(pool, false);
		logger.debug("Still-active threads:");
		for (int i = 0; i < pool.length; i++) {
		    logger.debug("  " + pool[i].getName());
		}
	    }
	}
    }

    public void addObserver(ConnectionObserver observer) {
	synchronized (observers) {
	    if (!observers.contains(observer))
		observers.add(observer);
	}
    }

    public void removeObserver(ConnectionObserver observer) {
	synchronized (observers) {
	    observers.remove(observer);
	}
    }

    public Iterator observerIterator() {
	return (((ArrayList)observers.clone()).iterator());
    }

    public boolean contains(ConnectionObserver observer) {
	synchronized (observers) {
	    return (observers.contains(observer));
	}
    }


    // notifyObservers is always single-threaded access as there's only 1
    // receiver thread!
    public void notifyObservers(Connection conn, SMPPEvent e) {
	if (logger.isDebugEnabled())
	    logger.debug("Notifying observers of a new SMPP event " + e.getType());

	queue.put(conn, e);
	if (threadsWaiting > 0) {
	    synchronized (queue) {
		queue.notify();
	    }
	}
    }

    public void notifyObservers(Connection conn, SMPPPacket pak) {
	if (logger.isDebugEnabled()) {
	    logger.debug("Notifying observers of a new SMPP packet ("
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
	    logger.debug("Thread " + Thread.currentThread().getName() + " started");

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

		if (nd == null)
		    continue;

		// Get a shallow copy of the registered observers and iterate
		// over it
		Iterator i = ((ArrayList)observers.clone()).iterator();
		while (i.hasNext()) {
		    observer = (ConnectionObserver)i.next();
		    if (nd.event == null)
			observer.packetReceived(nd.conn, nd.pak);
		    else
			observer.update(nd.conn, nd.event);
		} // end while
	    } // end while

	    logger.debug("Thread " + Thread.currentThread().getName() + " exiting");
	} catch (Exception x) {
	    logger.warn("Exception in dispatcher thread", x);
	}
    }

    private class NotificationDetails {
	public Connection conn = null;

	public SMPPEvent event = null;

	public SMPPPacket pak = null;

	public NotificationDetails() {
	}

	public void setDetails(Connection c, SMPPEvent e, SMPPPacket p) {
	    conn = c;
	    event = e;
	    pak = p;
	}
    }

    /** A simple implementation of a FIFO queue. Need this to be as minimal as
     * possible so it's zippidy quick. No synchronization is done here, it's
     * handled by the relevant ThreadedEventDispatcher methods.
     */
    private class FIFOQueue {

	private int head = 0, tail = 0;

	private NotificationDetails[] queue = null;

	public FIFOQueue(int queueSize) {
	    if (queueSize < 1)
		queueSize = 100;

	    queue = new NotificationDetails[queueSize];
	    for (int i = 0; i < queueSize; i++)
		queue[i] = new NotificationDetails();
	}

	public void put(Connection c, SMPPPacket p) throws QueueFullException {
	    if (isFull()) {
		throw new QueueFullException();
	    }

	    queue[tail++].setDetails(c, null, p);
	    if (tail >= queue.length)
		tail = 0;
	}

	public void put(Connection c, SMPPEvent e) throws QueueFullException {
	    if (isFull()) {
		throw new QueueFullException();
	    }

	    queue[tail++].setDetails(c, e, null);
	    if (tail >= queue.length)
		tail = 0;
	}


	public NotificationDetails get() {
	    NotificationDetails nd = null;

	    if (!isEmpty()) {
		nd = queue[head++];
		if (head >= queue.length)
		    head = 0;
	    }

	    return (nd);
	}

	public boolean isEmpty() {
	    return (tail == head);
	}

	public boolean isFull() {
	    if (tail > head)
		return ((tail == queue.length - 1) && head == 0);
	    else
		return (tail == (head - 1));
	}
    }

    private class QueueFullException extends RuntimeException {
	public QueueFullException() {
	}
    }
}
