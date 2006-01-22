package ie.omk.smpp.message;

/**
 * Bind to the SMSC as a transmitter.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindTransmitter extends ie.omk.smpp.message.Bind {
    /**
     * Construct a new BindTransmitter.
     */
    public BindTransmitter() {
        super(BIND_TRANSMITTER);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_transmitter");
    }
}

