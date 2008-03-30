package ie.omk.smpp.message;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;

import java.io.IOException;

/**
 * BroadcastSM response packet.
 * @version $Id:$
 * @since 0.4.0
 */
public class BroadcastSMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    
    public BroadcastSMResp() {
        super(CommandId.BROADCAST_SM_RESP);
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getMessageId() {
        return messageId;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            BroadcastSMResp other = (BroadcastSMResp) obj;
            equals |= safeCompare(messageId, other.messageId);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return (messageId != null) ? messageId.hashCode() : "".hashCode();
    }
    
    @Override
    protected void readMandatory(PacketDecoder decoder) {
        messageId = decoder.readCString();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(messageId);
    }
    
    @Override
    protected int getMandatorySize() {
        return 1 + sizeOf(messageId);
    }
}
