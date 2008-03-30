package ie.omk.smpp;

/**
 * Parent class of all runtime exceptions from the SMPP API.
 * @version $Id$
 */
public class SMPPRuntimeException extends RuntimeException {
    static final long serialVersionUID = 5392381000287167880L;

    public SMPPRuntimeException() {
        super();
    }

    public SMPPRuntimeException(String msg) {
        super(msg);
    }

    public SMPPRuntimeException(Throwable cause) {
        super(cause);
    }

    public SMPPRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

