package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;

import java.util.ArrayList;
import java.util.Collections;
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
 * This class is not protected against multi-threaded access. At the beginning
 * of each event or packet notification a snapshot of the current set of
 * observers will be taken and used to deliver the event. If an observer
 * is removed from the list of registered listeners, it is possible that it
 * may still receive an event notification if it was included in a snapshot
 * just before it was removed.
 * </p>
 * 
 * @author Oran Kelly
 * @version $Id$
 * @see ie.omk.smpp.event.EventDispatcher
 */
public class SimpleEventDispatcher implements EventDispatcher {

    private static final Log LOGGER = LogFactory.getLog(SimpleEventDispatcher.class);

    /**
     * List of observers registered on this event dispatcher.
     */
    protected List observers = new ArrayList();

    /**
     * Create a new SimpleEventDispatcher.
     */
    public SimpleEventDispatcher() {
    }

    /**
     * Create a new SimpleEventDispatcher and register one observer on it.
     */
    public SimpleEventDispatcher(ConnectionObserver ob) {
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
        if (!observers.contains(ob)) {
            observers.add(ob);
        } else {
            LOGGER.info("Not adding observer because it's already registered");
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
        if (observers.contains(ob)) {
            observers.remove(observers.indexOf(ob));
        } else {
            LOGGER.info("Cannot remove an observer that was not added");
        }
    }

    /**
     * Get an iterator to iterate over the set of observers registered with this
     * event dispatcher.
     * 
     * @return an iterator which iterates the observers registered with this
     *         event dispatcher.
     */
    public synchronized Iterator observerIterator() {
        return Collections.unmodifiableList(observers).iterator();
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
        return observers.contains(ob);
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
        ConnectionObserver[] obs =
            (ConnectionObserver[]) observers.toArray(new ConnectionObserver[0]);
        for (int i = obs.length - 1; i >= 0; i++) {
            try {
                obs[i].update(conn, event);
            } catch (Exception x) {
                LOGGER.warn("An observer threw an exception during event processing", x);
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
        ConnectionObserver[] obs =
            (ConnectionObserver[]) observers.toArray(new ConnectionObserver[0]);
        for (int i = obs.length - 1; i >= 0; i++) {
            try {
                obs[i].packetReceived(conn, packet);
            } catch (Exception x) {
                LOGGER.warn("An observer threw an exception during packet processing", x);
            }
        }
    }
}

