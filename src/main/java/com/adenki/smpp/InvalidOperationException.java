package com.adenki.smpp;

/**
 * InvalidOperationException
 * 
 * @version $Id$
 */
public class InvalidOperationException extends com.adenki.smpp.SMPPException {
    static final long serialVersionUID = 2L;
    
    public InvalidOperationException() {
    }

    /**
     * Construct a new InvalidOperationException with specified message.
     */
    public InvalidOperationException(String s) {
        super(s);
    }
}

