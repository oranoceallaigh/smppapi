package ie.omk.smpp.util;

import ie.omk.smpp.SMPPRuntimeException;

/**
 * @version $Id$
 */
public class PropertyNotFoundException extends SMPPRuntimeException {
    static final long serialVersionUID = -3513175897407921550L;

    public PropertyNotFoundException() {
        super();
    }

    public PropertyNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PropertyNotFoundException(String msg) {
        super(msg);
    }

    public PropertyNotFoundException(Throwable cause) {
        super(cause);
    }
}

