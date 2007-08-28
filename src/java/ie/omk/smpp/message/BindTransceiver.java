package ie.omk.smpp.message;

/**
 * Bind to the SMSC as a transceiver.
 * 
 * @version $Id$
 */
public class BindTransceiver extends Bind {
    private static final long serialVersionUID = 1L;
    /**
     * Construct a new BindTransceiver.
     */
    public BindTransceiver() {
        super(BIND_TRANSCEIVER);
    }
}

