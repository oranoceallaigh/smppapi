package ie.omk.smpp.message;

/**
 * Cancel broadcast message response.
 * @version $Id:$
 */
public class CancelBroadcastSMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;

    public CancelBroadcastSMResp() {
        super (SMPPPacket.CANCEL_BROADCAST_SM_RESP);
    }
}
