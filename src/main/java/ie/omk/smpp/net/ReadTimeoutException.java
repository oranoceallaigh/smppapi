package ie.omk.smpp.net;

import ie.omk.smpp.SMPPRuntimeException;

/**
 * Exception indicating that a timeout has occurred while reading from the
 * SMSC link.
 * @version $Id:$
 */
public class ReadTimeoutException extends SMPPRuntimeException {
    private static final long serialVersionUID = 1;

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
