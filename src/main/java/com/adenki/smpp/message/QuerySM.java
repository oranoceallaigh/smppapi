package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Query the state of a message.
 * 
 * @version $Id$
 */
public class QuerySM extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String messageId;
    private Address source;
    
    /**
     * Construct a new QuerySM.
     */
    public QuerySM() {
        super(CommandId.QUERY_SM);
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
            QuerySM other = (QuerySM) obj;
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
    protected void toString(StringBuilder buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",source=").append(source);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
        smppVersion.validateAddress(source);
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
        return 1 + sizeOf(messageId) + sizeOf(source);
    }
}
