package com.adenki.smpp.util;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * @version $Id$
 */
public class PropertyNotFoundException extends SMPPRuntimeException {
    static final long serialVersionUID = 2L;

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

