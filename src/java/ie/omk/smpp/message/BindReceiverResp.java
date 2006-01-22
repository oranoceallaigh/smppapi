package ie.omk.smpp.message;

/**
 * SMSC response to a BindReceiver request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindReceiverResp extends ie.omk.smpp.message.BindResp {
    /**
     * Construct a new BindReceiverResp.
     */
    public BindReceiverResp() {
        super(BIND_RECEIVER_RESP);
    }

    /**
     * Create a new BindReceiverResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to that of the BindReceiver
     * message.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public BindReceiverResp(BindReceiver r) {
        super(r);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_receiver_resp");
    }
}

