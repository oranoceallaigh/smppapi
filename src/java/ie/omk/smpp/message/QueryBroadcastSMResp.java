package ie.omk.smpp.message;

import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * QueryBroadcastSM response packet.
 * @version $Id:$
 */
public class QueryBroadcastSMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    
    public QueryBroadcastSMResp() {
        super (SMPPPacket.QUERY_BROADCAST_SM_RESP);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
    
    @Override
    protected boolean validateTLVTable(SMPPVersion smppVersion) {
        boolean valid = true;
        TLVTable tlvTable = getTLVTable();
        valid &= tlvTable.containsKey(Tag.MESSAGE_STATE);
        valid &= tlvTable.containsKey(Tag.BROADCAST_AREA_IDENTIFIER);
        valid &= tlvTable.containsKey(Tag.BROADCAST_AREA_SUCCESS);
        return valid;
    }
}
