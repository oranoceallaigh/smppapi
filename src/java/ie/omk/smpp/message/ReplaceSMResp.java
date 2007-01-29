package ie.omk.smpp.message;

/**
 * SMSC response to a ReplaceSM request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class ReplaceSMResp extends SMPPPacket {
    /**
     * Construct a new ReplaceSMResp.
     */
    public ReplaceSMResp() {
        super(REPLACE_SM_RESP);
    }

    /**
     * Create a new ReplaceSMResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public ReplaceSMResp(SMPPPacket request) {
        super(request);
    }
}

