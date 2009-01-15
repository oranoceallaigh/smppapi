package com.adenki.smpp.event;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.Session;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;

/**
 * Implementation of the {@link EventDispatcher} that uses Java's
 * {@link Executor} framework to dispatch events. If no other executor
 * is supplied, then a {@link ThreadPoolExecutor} will be created
 * at {@link #init} time.
 * <p>
 * The number of threads created in the <tt>ThreadPoolExecutor</tt>
 * is determined from the {@link #setThreadCount(int) threadCount}
 * property, which by default is set to <tt>0</tt>. If the application
 * does not override this value, then the {@link APIConfig} will
 * be consulted for the {@link APIConfig#EVENT_THREAD_POOL_SIZE} property. If
 * no value is set there, then a default value of <tt>3</tt> will be used.
 * </p>
 */
public class TaskExecutorEventDispatcher extends AbstractEventDispatcher {
    private static final Logger LOG =
        LoggerFactory.getLogger(TaskExecutorEventDispatcher.class);

    private Executor executor;
    private int threadCount = 0;
    
    public void destroy() {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdownNow();
        }
    }

    public void init() {
        if (executor == null) {
            int numThreads = threadCount;
            if (numThreads < 1) {
                numThreads = getNumThreadsFromConfig();
            }
            executor = Executors.newFixedThreadPool(numThreads);
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
    
    private int getNumThreadsFromConfig() {
        APIConfig config = APIConfigFactory.getConfig();
        return config.getInt(APIConfig.EVENT_THREAD_POOL_SIZE, 3);
    }
}
