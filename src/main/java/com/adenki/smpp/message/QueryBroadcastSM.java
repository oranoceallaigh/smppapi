package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Query the status of a previously submitted broadcast message.
 * @version $Id$
 * @since 0.4.0
 */
public class QueryBroadcastSM extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    private String messageId;
    private Address source;
    
    public QueryBroadcastSM() {
        super (CommandId.QUERY_BROADCAST_SM);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            QueryBroadcastSM other = (QueryBroadcastSM) obj;
            equals |= safeCompare(messageId, other.messageId);
            equals |= safeCompare(source, other.source);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (messageId != null) ? messageId.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        return hc;
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        messageId = decoder.readCString();
        source = decoder.readAddress();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(messageId);
        encoder.writeAddress(source);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 1;
        length += sizeOf(messageId);
        length += sizeOf(source);
        return length;
    }
}
