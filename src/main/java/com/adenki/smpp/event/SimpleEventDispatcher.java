package com.adenki.smpp.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.Session;
import com.adenki.smpp.message.SMPPPacket;

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
 * @version $Id$
 * @see com.adenki.smpp.event.EventDispatcher
 */
public class SimpleEventDispatcher extends AbstractEventDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleEventDispatcher.class);
    
    /**
     * Create a new SimpleEventDispatcher.
     */
    public SimpleEventDispatcher() {
    }

    /**
     * Create a new SimpleEventDispatcher and register one observer on it.
     */
    public SimpleEventDispatcher(SessionObserver observer) {
        addObserver(observer);
    }

    public void init() {
        // nothing to do.
    }

    public void destroy() {
        // nothing to do.
    }

    /**
     * Notify registered observers of an SMPP event.
     * @param conn the Connection with which the event is associated.
     * @param event the SMPP event to notify observers of.
     */
    public void notifyObservers(Session conn, SMPPEvent event) {
        SessionObserver[] observerList = getObserverList();
        for (SessionObserver observer : observerList) {
            try {
                observer.update(conn, event);
            } catch (Exception x) {
                LOG.error("An observer threw an exception during event processing", x);
            }
        }
    }

    /**
     * Notify registered observers of an incoming SMPP packet.
     * @param conn  the Connection which the packet was received on.
     * @param packet the received packet to notify observers of.
     */
    public void notifyObservers(Session conn, SMPPPacket packet) {
        SessionObserver[] observerList = getObserverList();
        for (SessionObserver observer : observerList) {
            try {
                observer.packetReceived(conn, packet);
            } catch (Exception x) {
                LOG.error("An observer threw an exception during packet processing", x);
            }
        }
    }
}
