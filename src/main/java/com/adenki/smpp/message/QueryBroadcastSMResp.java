package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.message.tlv.TLVTable;
import com.adenki.smpp.message.tlv.Tag;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * QueryBroadcastSM response packet.
 * @version $Id$
 * @since 0.4.0
 */
public class QueryBroadcastSMResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String messageId;
    
    public QueryBroadcastSMResp() {
        super (CommandId.QUERY_BROADCAST_SM_RESP);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            QueryBroadcastSMResp other = (QueryBroadcastSMResp) obj;
            equals |= safeCompare(messageId, other.messageId);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (messageId != null) ? messageId.hashCode() : 0;
        return hc;
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
    protected boolean validateTLVTable(SMPPVersion smppVersion) {
        boolean valid = true;
        TLVTable tlvTable = getTLVTable();
        valid &= tlvTable.containsKey(Tag.MESSAGE_STATE);
        valid &= tlvTable.containsKey(Tag.BROADCAST_AREA_IDENTIFIER);
        valid &= tlvTable.containsKey(Tag.BROADCAST_AREA_SUCCESS);
        return valid;
    }
    
    @Override
    protected int getMandatorySize() {
        return 1 + sizeOf(messageId);
    }
}
