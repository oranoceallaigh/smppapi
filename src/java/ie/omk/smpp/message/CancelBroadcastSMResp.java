package ie.omk.smpp.message;

/**
 * Cancel broadcast message response.
 * @version $Id:$
 * @since 0.4.0
 */
public class CancelBroadcastSMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;

    public CancelBroadcastSMResp() {
        super (CommandId.CANCEL_BROADCAST_SM_RESP);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 13;
    }
}
