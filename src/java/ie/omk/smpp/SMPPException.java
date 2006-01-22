package ie.omk.smpp;

/**
 * SMPPException.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class SMPPException extends java.lang.Exception {
    public SMPPException() {
        super();
    }

    public SMPPException(String s) {
        super(s);
    }

    public SMPPException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

