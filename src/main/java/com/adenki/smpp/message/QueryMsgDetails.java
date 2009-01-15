package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Query Message details. Get all information about an existing message at the
 * SMSC.
 * 
 * @version $Id$
 */
public class QueryMsgDetails extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    /**
     * Original message ID of the required message.
     */
    private String messageId;
    /**
     * Source address of the message.
     */
    private Address source;
    /**
     * Length of the message text required.
     */
    private int smLength;

    /**
     * Construct a new QueryMsgDetails.
     */
    public QueryMsgDetails() {
        super(CommandId.QUERY_MSG_DETAILS);
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

    /**
     * Set the number of bytes of the original message required. Minimum request
     * length is 0, maximum is 160. If the length is outside these bounds, it
     * will be set to the min or max.
     * 
     * @param len
     *            The number of bytes required.
     */
    public void setSmLength(int len) {
        if (len < 0) {
            smLength = 0;
        } else if (len > 160) {
            smLength = 160;
        } else {
            smLength = len;
        }
    }

    /** Get the number of bytes of the original message being requested. */
    public int getSmLength() {
        return smLength;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            QueryMsgDetails other = (QueryMsgDetails) obj;
            equals |= safeCompare(messageId, other.messageId);
            equals |= safeCompare(source, other.source);
            equals |= smLength == other.smLength;
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (messageId != null) ? messageId.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        hc += Integer.valueOf(smLength).hashCode();
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",source=").append(source)
        .append(",smLength=").append(smLength);
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
        smLength = decoder.readUInt1();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(messageId);
        encoder.writeAddress(source);
        encoder.writeUInt1(smLength);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 2;
        length += sizeOf(messageId);
        length += sizeOf(source);
        return length;
    }
}
