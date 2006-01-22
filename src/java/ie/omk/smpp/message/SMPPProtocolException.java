package ie.omk.smpp.message;

public class SMPPProtocolException extends ie.omk.smpp.SMPPRuntimeException {

    public SMPPProtocolException(String msg) {
        super(msg);
    }

    public SMPPProtocolException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

