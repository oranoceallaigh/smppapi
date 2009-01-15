package com.adenki.smpp.message;

/**
 * Message state enumeration.
 * @version $Id$
 * @since 0.4.0
 */
public class MessageState {
    public static final MessageState SCHEDULED = new MessageState(0, false);
    public static final MessageState EN_ROUTE = new MessageState(1, false);
    public static final MessageState DELIVERED = new MessageState(2, true);
    public static final MessageState EXPIRED = new MessageState(3, true);
    public static final MessageState DELETED = new MessageState(4, true);
    public static final MessageState UNDELIVERABLE = new MessageState(5, true);
    public static final MessageState ACCEPTED = new MessageState(6, true);
    public static final MessageState UNKNOWN = new MessageState(7, false);
    public static final MessageState REJECTED = new MessageState(8, true);
    public static final MessageState SKIPPED = new MessageState(9, true);
    
    private static final MessageState[] LOOKUP_TABLE = new MessageState[] {
        SCHEDULED,
        EN_ROUTE,
        DELIVERED,
        EXPIRED,
        DELETED,
        UNDELIVERABLE,
        ACCEPTED,
        UNKNOWN,
        REJECTED,
        SKIPPED,
    };
    
    private final int value;
    private final boolean isFinal;
    
    protected MessageState(int value, boolean isFinal) {
        this.value = value;
        this.isFinal = isFinal;
    }

    public int getValue() {
        return value;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public static MessageState getMessageState(int value) {
        try {
            return LOOKUP_TABLE[value];
        } catch (ArrayIndexOutOfBoundsException x) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
