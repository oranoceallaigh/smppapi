package com.adenki.smpp.message;

/**
 * SMSC response to a BindTransmitter request.
 * 
 * @version $Id$
 */
public class BindTransmitterResp extends BindResp {
    private static final long serialVersionUID = 2L;
    /**
     * Construct a new BindTransmitterResp.
     */
    public BindTransmitterResp() {
        super(CommandId.BIND_TRANSMITTER_RESP);
    }

    /**
     * Create a new BindTransmitterResp packet in response to a BindTransmitter.
     * This constructor will set the sequence number to that of the packet it is
     * in response to.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public BindTransmitterResp(SMPPPacket request) {
        super(request);
    }
}
