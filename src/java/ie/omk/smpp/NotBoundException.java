package ie.omk.smpp;

/**
 * NotBoundException
 * 
 * @version $Id$
 */
public class NotBoundException extends ie.omk.smpp.SMPPRuntimeException {
    static final long serialVersionUID = -3977230990049048741L;
    
    public NotBoundException() {
    }

    /**
     * Construct a new NotBoundException with specified message.
     */
    public NotBoundException(String s) {
        super(s);
    }
}

