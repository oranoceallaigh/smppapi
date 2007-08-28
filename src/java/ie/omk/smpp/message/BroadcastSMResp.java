package ie.omk.smpp.message;

import java.util.List;

/**
 * BroadcastSM response packet.
 * @version $Id:$
 *
 */
public class BroadcastSMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    
    public BroadcastSMResp() {
        super(SMPPPacket.BROADCAST_SM_RESP);
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getMessageId() {
        return messageId;
    }

    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BodyDescriptor.ONE_CSTRING;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {messageId};
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
    }
}
