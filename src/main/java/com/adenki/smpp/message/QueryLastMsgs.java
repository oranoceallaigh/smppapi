package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Query the last number of messages sent from a certain ESME. Relevant
 * inherited fields from SMPPPacket: <br>
 * <ul>
 * source <br>
 * </ul>
 * 
 * @version $Id$
 */
public class QueryLastMsgs extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    /**
     * The source address for which to query messages. The last <code>
     * msgCount</code> messages originating from this source address will
     * be retrieved.
     */
    private Address source;

    /**
     * Number of messages to look up.
     */
    private int msgCount;

    /**
     * Construct a new QueryLastMsgs.
     */
    public QueryLastMsgs() {
        super(CommandId.QUERY_LAST_MSGS);
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    public int getMsgCount() {
        return msgCount;
    }

    /**
     * Set the number of messages to query. <code>msgCount</code> must be
     * between 1 and 100 inclusive. Attempts to set a value less than 1 will
     * force the value to 1. Attempts to set a value greater than 100 will
     * force the value to 100.
     * @param msgCount The number of messages to query from the SMSC.
     */
    public void setMsgCount(int msgCount) {
        if (msgCount < 1) {
            this.msgCount = 1;
        } else if (msgCount > 100) {
            this.msgCount = 100;
        } else {
            this.msgCount = msgCount;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            QueryLastMsgs other = (QueryLastMsgs) obj;
            equals |= safeCompare(source, other.source);
            equals |= msgCount == other.msgCount;
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (source != null) ? source.hashCode() : 0;
        hc += Integer.valueOf(msgCount).hashCode();
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("source=").append(source)
        .append("msgCount=").append(msgCount);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateAddress(source);
    }
    
    @Override
    protected void readMandatory(PacketDecoder decoder) {
        source = decoder.readAddress();
        msgCount = decoder.readUInt1();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeAddress(source);
        encoder.writeUInt1(msgCount);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 1;
        length += sizeOf(source);
        return length;
    }
}
