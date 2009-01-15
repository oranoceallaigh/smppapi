package com.adenki.smpp.message;

public class SMPPProtocolException extends com.adenki.smpp.SMPPRuntimeException {
    static final long serialVersionUID = 2L;

    public SMPPProtocolException(String msg) {
        super(msg);
    }

    public SMPPProtocolException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

