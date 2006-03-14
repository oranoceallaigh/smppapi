package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple implementation of the event dispatcher interface. This
 * implementation simply iterates over the set of registered observers and
 * notifies each of the event in turn. This means that whatever thread the
 * <code>Connection</code> object uses to call into this object will be
 * blocked, from the <code>Connection</code>'s point of view, until every
 * observer has successfully processed the event.
 * 
 * <p>
 * Adding and removing observers from an instance of this class is protected
 * against multi-threaded access. However, event and packet notification
 * is not. If an event notification is currently in progress and another
 * thread modifies the set of registered observers, then it is possible for
 * the new observer to receive events before the call to <code>addObserver
 * </code> is complete.
 * </p>
 * 
 * @author Oran Kelly
 * @version $Id$
 * @see ie.omk.smpp.event.EventDispatcher
 */
public class SimpleEventDispatcher implements EventDispatcher {

    /**
     * Size of array increments.
     */
    public static final int INCREMENT = 3;

    private static final Log LOGGER = LogFactory.getLog(SimpleEventDispatcher.class);
    
    /**
     * List of observers registered on this event dispatcher.
     */
    private ConnectionObserver[] observers;

    /**
     * Create a new SimpleEventDispatcher.
     */
    public SimpleEventDispatcher() {
        observers = new ConnectionObserver[INCREMENT];
    }

    /**
     * Create a new SimpleEventDispatcher and register one observer on it.
     */
    public SimpleEventDispatcher(ConnectionObserver ob) {
        observers = new ConnectionObserver[1];
        addObserver(ob);
    }

    public void init() {
        // nothing to do.
    }

    public void destroy() {
        // nothing to do.
    }

    /**
     * Add a connection observer to receive SMPP events. An observer cannot be
     * added twice. Attempting to do so has no effect.
     * 
     * @param ob
     *            the ConnectionObserver object to add.
     */
    public void addObserver(ConnectionObserver ob) {
        synchronized (observers) {
            if (indexOf(ob) < 0) {
                int index = findSlot();
                if (index < 0) {
                    growArray(ob);
                } else {
                    observers[index] = ob;
                }
            } else {
                LOGGER.info("Not adding observer because it's already registered");
            }
        }
    }

    /**
     * Remove a connection observer. If the observer was not previously added,
     * the method has no effect.
     * 
     * @param ob
     *            The ConnectionObserver object to remove.
     */
    public void removeObserver(ConnectionObserver ob) {
        synchronized (observers) {
            int index = indexOf(ob);
            if (index > -1) {
                observers[index] = null;
            } else {
                LOGGER.info("Cannot remove an observer that was not added");
            }
        }
    }

    /**
     * Get an iterator to iterate over the set of observers registered with this
     * event dispatcher.
     * 
     * @return an iterator which iterates the observers registered with this
     *         event dispatcher.
     */
    public Iterator observerIterator() {
        List list;
        synchronized (observers) {
            list = new ArrayList(observers.length);
            for (int i = 0; i < observers.length; i++) {
                if (observers[i] != null) {
                    list.add(observers[i]);
                }
            }
        }
        return list.iterator();
    }

    /**
     * Determine if this dispatcher has a particular observer registered for
     * events.
     * 
     * @param ob
     *            The ConnectionObserver to check the presence of.
     * @return true if the observer is registered with this dispatcher, false
     *         otherwise.
     */
    public boolean contains(ConnectionObserver ob) {
        return indexOf(ob) > -1;
    }

    /**
     * Notify registered observers of an SMPP event.
     * 
     * @param conn 
     *            the Connection with which the event is associated.
     * @param event
     *            the SMPP event to notify observers of.
     */
    public void notifyObservers(Connection conn, SMPPEvent event) {
        for (int i = 0; i < observers.length; i++) {
            ConnectionObserver obs = observers[i];
            if (obs != null) {
                try {
                    obs.update(conn, event);
                } catch (Throwable t) {
                    LOGGER.warn("An observer threw an exception during event processing", t);
                }
            }
        }
    }

    /**
     * Notify registered observers of an incoming SMPP packet.
     * 
     * @param conn 
     *            the Connection which the packet was received on.
     * @param packet
     *            the received packet to notify observers of.
     */
    public void notifyObservers(Connection conn, SMPPPacket packet) {
        for (int i = 0; i < observers.length; i++) {
            ConnectionObserver obs = observers[i];
            if (obs != null) {
                try {
                    obs.packetReceived(conn, packet);
                } catch (Throwable t) {
                    LOGGER.warn("An observer threw an exception during packet processing", t);
                }
            }
        }
    }
    
    public int capacity() {
        return observers.length;
    }
    
    public int size() {
        int size = 0;
        synchronized (observers) {
            for (int i = 0; i < observers.length; i++) {
                if (observers[i] != null) {
                    size++;
                }
            }
        }
        return size;
    }
    
    void growArray(ConnectionObserver ob) {
        assert Thread.holdsLock(observers);
        ConnectionObserver[] newArray =
            new ConnectionObserver[observers.length + INCREMENT];
        System.arraycopy(observers, 0, newArray, 0, observers.length);
        newArray[observers.length] = ob;
        observers = newArray;
    }
    
    private int findSlot() {
        assert Thread.holdsLock(observers);
        int i;
        for (i = 0; i < observers.length; i++) {
            if (observers[i] == null) {
                break;
            }
        }
        if (i == observers.length) {
            i = -1;
        }
        return i;
    }
    
    private int indexOf(ConnectionObserver obs) {
        for (int i = observers.length - 1; i >= 0; i--) {
            if (observers[i] == obs) {
                return i;
            }
        }
        return -1;
    }
}
