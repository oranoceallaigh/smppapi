package ie.omk.smpp.message;

/**
 * SMSC response to a ReplaceSM request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class ReplaceSMResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new ReplaceSMResp.
     */
    public ReplaceSMResp() {
        super(REPLACE_SM_RESP);
    }

    /**
     * Construct a new ReplaceSMResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public ReplaceSMResp(int seqNum) {
        super(REPLACE_SM_RESP, seqNum);
    }

    /**
     * Create a new ReplaceSMResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public ReplaceSMResp(ReplaceSM r) {
        super(r);
    }

    public int getBodyLength() {
        return 0;
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("replace_sm_resp");
    }
}

