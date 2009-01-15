package com.adenki.smpp.event;

import com.adenki.smpp.Session;

/**
 * Abstract super class of SMPP control events.
 * 
 * @version $Id$
 */
public abstract class SMPPEvent {
    /** ReceiverStartEvent enumeration type. */
    public static final int RECEIVER_START = 2;

    /** ReceiverExitEvent enumeration type. */
    public static final int RECEIVER_EXIT = 3;

    /** ReceiverExceptionEvent enumeration type. */
    public static final int RECEIVER_EXCEPTION = 4;

    /** The source Connection of this event. */
    private Session source;

    /** The type of this event. */
    private int type;

    /**
     * Construct a new event. The <code>type</code> parameter should match one
     * of the enumeration constants defined in this class.
     */
    protected SMPPEvent(int type, Session source) {
        this.source = source;
        this.type = type;
    }

    /**
     * Get the source connection of this event.
     */
    public Session getSource() {
        return source;
    }

    /**
     * Get the enumeration type of this event.
     * 
     * @see #RECEIVER_EXIT
     * @see #RECEIVER_EXCEPTION
     */
    public int getType() {
        return type;
    }
}

