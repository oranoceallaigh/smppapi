package com.adenki.smpp.message;

/**
 * SMSC response to a BindTransceiver request.
 * 
 * @version $Id$
 */
public class BindTransceiverResp extends BindResp {
    private static final long serialVersionUID = 2L;
    /**
     * Construct a new BindTransceiverResp.
     */
    public BindTransceiverResp() {
        super(CommandId.BIND_TRANSCEIVER_RESP);
    }

    /**
     * Create a new BindTransceiverResp packet in response to a BindTransceiver.
     * This constructor will set the sequence number to that of the packet it is
     * in response to.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public BindTransceiverResp(SMPPPacket request) {
        super(request);
    }
}

