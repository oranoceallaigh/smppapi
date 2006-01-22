package ie.omk.smpp.event;

import ie.omk.smpp.Connection;

/**
 * Abstract super class of SMPP control events.
 * 
 * @author Oran Kelly
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
    private Connection source = null;

    /** The type of this event. */
    private int type = 0;

    /**
     * Construct a new event. The <code>type</code> parameter should match one
     * of the enumeration constants defined in this class.
     */
    protected SMPPEvent(int type, Connection source) {
        this.source = source;
        this.type = type;
    }

    /**
     * Get the source connection of this event.
     */
    public Connection getSource() {
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

