package com.adenki.smpp.message;

/**
 * SMSC response to a BindReceiver request.
 * 
 * @version $Id$
 */
public class BindReceiverResp extends BindResp {
    private static final long serialVersionUID = 2L;
    /**
     * Construct a new BindReceiverResp.
     */
    public BindReceiverResp() {
        super(CommandId.BIND_RECEIVER_RESP);
    }

    public BindReceiverResp(SMPPPacket request) {
        super(request);
    }
}

