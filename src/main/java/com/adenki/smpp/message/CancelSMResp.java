package com.adenki.smpp.message;

/**
 * SMSC response to a cancel message request.
 * @version $Id$
 */
public class CancelSMResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    /**
     * Construct a new CancelSMResp.
     */
    public CancelSMResp() {
        super(CommandId.CANCEL_SM_RESP);
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
    
    @Override
    public int hashCode() {
        return super.hashCode() * 37;
    }
}
