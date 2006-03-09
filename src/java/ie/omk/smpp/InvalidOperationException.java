package ie.omk.smpp;

/**
 * InvalidOperationException
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class InvalidOperationException extends ie.omk.smpp.SMPPException {
    static final long serialVersionUID = 7624381507606078212L;
    
    public InvalidOperationException() {
    }

    /**
     * Construct a new InvalidOperationException with specified message.
     */
    public InvalidOperationException(String s) {
        super(s);
    }
}

