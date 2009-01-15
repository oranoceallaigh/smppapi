package com.adenki.smpp;

/**
 * Thrown when an attempt is made to use an unimplemented command ID.
 * @version $Id$
 */
public class BadCommandIDException extends SMPPRuntimeException {
    private static final long serialVersionUID = 3L;
    private int badId;
    
    public BadCommandIDException() {
        super();
    }

    public BadCommandIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadCommandIDException(String message) {
        super(message);
    }

    public BadCommandIDException(Throwable cause) {
        super(cause);
    }

    public BadCommandIDException(String message, int badId) {
        super(message);
        this.badId = badId;
    }

    public BadCommandIDException(String message, Throwable cause, int badId) {
        super(message, cause);
        this.badId = badId;
    }

    public int getBadId() {
        return badId;
    }
}
