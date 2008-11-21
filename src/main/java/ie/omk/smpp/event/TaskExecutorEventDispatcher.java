package ie.omk.smpp.event;

import ie.omk.smpp.Session;
import ie.omk.smpp.message.SMPPPacket;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link EventDispatcher} that uses Java's
 * {@link Executor} framework to dispatch events. If no other executor
 * is supplied, then a {@link ThreadPoolExecutor} will be created
 * at {@link #init} time.
 */
public class TaskExecutorEventDispatcher extends AbstractEventDispatcher {
    private static final Logger LOG =
        LoggerFactory.getLogger(TaskExecutorEventDispatcher.class);

    private Executor executor;
    private int threadCount = 3;
    
    public void destroy() {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdownNow();
        }
    }

    public void init() {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(threadCount);
        }
    }

    public void notifyObservers(final Session conn, final SMPPEvent event) {
        final SessionObserver[] observers = getObserverList();
        executor.execute(new Runnable() {
            public void run() {
                doUpdate(observers, conn, event);
            }
        });
    }

    public void notifyObservers(final Session conn, final SMPPPacket packet) {
        final SessionObserver[] observers = getObserverList();
        executor.execute(new Runnable() {
            public void run() {
                doPacketReceived(observers, conn, packet);
            }
        });
    }

    /**
     * Set the number of threads to create in a <tt>ThreadPoolExecutor</tt>
     * for event dispatching. This property is ignored if an
     * <tt>executor</tt> is directly set on this object.
     * @param threadCount The number of threads to create in the thread pool.
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    private void doUpdate(SessionObserver[] observers, Session session, SMPPEvent event) {
        for (SessionObserver observer : observers) {
            try {
                observer.update(session, event);
            } catch (Throwable t) {
                LOG.error("Observer " + observer + " threw an exception", t);
            }
        }
    }
    
    private void doPacketReceived(
            SessionObserver[] observers, Session session, SMPPPacket packet) {
        for (SessionObserver observer : observers) {
            try {
                observer.packetReceived(session, packet);
            } catch (Throwable t) {
                LOG.error("Observer " + observer + " threw an exception", t);
            }
        }
    }
}
