package com.adenki.smpp.net;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * Exception indicating that a timeout has occurred while reading from the
 * SMSC link.
 * @version $Id$
 */
public class ReadTimeoutException extends SMPPRuntimeException {
    private static final long serialVersionUID = 2L;

    public ReadTimeoutException() {
        super();
    }

    public ReadTimeoutException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ReadTimeoutException(String message) {
        super(message);
    }

    public ReadTimeoutException(Throwable rootCause) {
        super(rootCause);
    }
}
