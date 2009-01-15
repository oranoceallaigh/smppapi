package com.adenki.smpp;

/**
 * $Id$
 */
public class UnsupportedOperationException extends SMPPRuntimeException {
    static final long serialVersionUID = 2L;
    
    public UnsupportedOperationException() {
    }

    public UnsupportedOperationException(String msg) {
        super(msg);
    }
}
