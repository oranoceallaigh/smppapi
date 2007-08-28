package ie.omk.smpp.event;

import ie.omk.smpp.Connection;

/**
 * Event generated when the receiver thread starts. Usually applications can
 * ignore this message as they don't need to do anything when the receiver
 * thread starts.
 * 
 * @version $Id$
 */
public class ReceiverStartEvent extends SMPPEvent {
    /**
     * Create a new ReceiverStartEvent.
     */
    public ReceiverStartEvent(Connection source) {
        super(RECEIVER_START, source);
    }
}

