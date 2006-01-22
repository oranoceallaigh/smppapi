package ie.omk.smpp.message;

/**
 * SMSC response to a BindTransmitter request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindTransmitterResp extends ie.omk.smpp.message.BindResp {
    /**
     * Construct a new BindTransmitterResp.
     */
    public BindTransmitterResp() {
        super(BIND_TRANSMITTER_RESP);
    }

    /**
     * Create a new BindTransmitterResp packet in response to a BindTransmitter.
     * This constructor will set the sequence number to that of the packet it is
     * in response to.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public BindTransmitterResp(BindTransmitter r) {
        super(r);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_transmitter_resp");
    }
}

