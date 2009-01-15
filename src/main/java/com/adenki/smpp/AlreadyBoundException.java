package com.adenki.smpp;

/**
 * AlreadyBoundException
 * 
 * @version $Id$
 */
public class AlreadyBoundException extends com.adenki.smpp.SMPPRuntimeException {
    static final long serialVersionUID = 2L;
    
    public AlreadyBoundException() {
    }

    /**
     * Construct a new AlreadyBoundException with specified message.
     */
    public AlreadyBoundException(String s) {
        super(s);
    }
}

