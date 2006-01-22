package ie.omk.smpp.message;

/**
 * SMSC response to an Unbind request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class UnbindResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new UnbindResp.
     */
    public UnbindResp() {
        super(UNBIND_RESP);
    }

    /**
     * Construct a new UnbindResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public UnbindResp(int seqNum) {
        super(UNBIND_RESP, seqNum);
    }

    /**
     * Create a new UnbindResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public UnbindResp(Unbind r) {
        super(r);
    }

    public int getBodyLength() {
        return 0;
    }

    public void readBodyFrom(byte[] b, int offset) throws SMPPProtocolException {
        return;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("unbind_resp");
    }
}

