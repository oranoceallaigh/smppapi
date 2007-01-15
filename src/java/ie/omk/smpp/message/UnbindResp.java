package ie.omk.smpp.message;

/**
 * SMSC response to an Unbind request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class UnbindResp extends SMPPPacket {
    /**
     * Construct a new UnbindResp.
     */
    public UnbindResp() {
        super(UNBIND_RESP);
    }

    /**
     * Create a new UnbindResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public UnbindResp(SMPPPacket request) {
        super(request);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("unbind_resp");
    }
}
