package ie.omk.smpp;

/**
 * BadCommandIDException
 * 
 * @version $Id$
 */
public class BadCommandIDException extends SMPPRuntimeException {
    private static final long serialVersionUID = 2;
    private int badId;
    
    public BadCommandIDException() {
        super();
    }

    public BadCommandIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadCommandIDException(String message) {
        super(message);
    }

    public BadCommandIDException(Throwable cause) {
        super(cause);
    }

    public BadCommandIDException(String message, int badId) {
        super(message);
        this.badId = badId;
    }

    public BadCommandIDException(String message, Throwable cause, int badId) {
        super(message, cause);
        this.badId = badId;
    }

    public int getBadId() {
        return badId;
    }
}
