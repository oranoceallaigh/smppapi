package com.adenki.smpp;

/**
 * Parent class of all runtime exceptions from the SMPP API.
 * @version $Id$
 */
public class SMPPRuntimeException extends RuntimeException {
    static final long serialVersionUID = 2L;

    public SMPPRuntimeException() {
        super();
    }

    public SMPPRuntimeException(String msg) {
        super(msg);
    }

    public SMPPRuntimeException(Throwable cause) {
        super(cause);
    }

    public SMPPRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

