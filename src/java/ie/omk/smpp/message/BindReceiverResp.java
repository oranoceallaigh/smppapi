package ie.omk.smpp.message;

/**
 * SMSC response to a BindReceiver request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindReceiverResp extends ie.omk.smpp.message.BindResp {
    /**
     * Construct a new BindReceiverResp.
     */
    public BindReceiverResp() {
        super(BIND_RECEIVER_RESP);
    }

    public BindReceiverResp(SMPPPacket request) {
        super(request);
    }
}

