package ie.omk.smpp.message;

/**
 * SMSC response to a cancel message request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class CancelSMResp extends SMPPPacket {
    /**
     * Construct a new CancelSMResp.
     */
    public CancelSMResp() {
        super(CANCEL_SM_RESP);
    }

    /**
     * Create a new CancelSMResp packet in response to a CancelSM. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public CancelSMResp(SMPPPacket request) {
        super(request);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("cancel_sm_resp");
    }
}
