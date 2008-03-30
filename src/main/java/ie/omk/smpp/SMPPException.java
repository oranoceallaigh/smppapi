package ie.omk.smpp;

/**
 * SMPPException.
 * 
 * @version $Id$
 */
public class SMPPException extends java.lang.Exception {
    static final long serialVersionUID = -5382146274442716891L;

    public SMPPException() {
        super();
    }

    public SMPPException(String message, Throwable cause) {
        super(message, cause);
    }

    public SMPPException(String message) {
        super(message);
    }

    public SMPPException(Throwable cause) {
        super(cause);
    }
}
