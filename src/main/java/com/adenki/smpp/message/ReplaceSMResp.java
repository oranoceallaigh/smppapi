package com.adenki.smpp.message;

/**
 * SMSC response to a ReplaceSM request.
 * 
 * @version $Id$
 */
public class ReplaceSMResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    /**
     * Construct a new ReplaceSMResp.
     */
    public ReplaceSMResp() {
        super(CommandId.REPLACE_SM_RESP);
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
    
    @Override
    public int hashCode() {
        return super.hashCode() * 19;
    }
}

