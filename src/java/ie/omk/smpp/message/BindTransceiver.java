package ie.omk.smpp.message;

/**
 * Bind to the SMSC as a transceiver.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindTransceiver extends ie.omk.smpp.message.Bind {
    /**
     * Construct a new BindTransceiver.
     */
    public BindTransceiver() {
        super(BIND_TRANSCEIVER);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_transceiver");
    }
}

