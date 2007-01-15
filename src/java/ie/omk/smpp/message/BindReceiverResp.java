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
    
    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_receiver_resp");
    }
}

