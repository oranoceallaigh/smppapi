package ie.omk.smpp.message;

/**
 * Bind to the SMSC as a transceiver.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindTransceiver extends Bind {
    /**
     * Construct a new BindTransceiver.
     */
    public BindTransceiver() {
        super(BIND_TRANSCEIVER);
    }
}

