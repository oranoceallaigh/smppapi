package ie.omk.smpp.message;

/**
 * SMSC response to a cancel message request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class CancelSMResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new CancelSMResp.
     */
    public CancelSMResp() {
        super(CANCEL_SM_RESP);
    }

    /**
     * Construct a new CancelSMResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public CancelSMResp(int seqNum) {
        super(CANCEL_SM_RESP, seqNum);
    }

    /**
     * Create a new CancelSMResp packet in response to a CancelSM. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public CancelSMResp(CancelSM r) {
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
        return new String("cancel_sm_resp");
    }
}

