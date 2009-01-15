package com.adenki.smpp.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * SMSC response to a QueryLastMsgs request.
 * @version $Id$
 */
public class QueryLastMsgsResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    private static final int MAX_SIZE = 255;
    
    /** The table of messages returned */
    private List<String> messageTable = new ArrayList<String>();

    /**
     * Construct a new QueryLastMsgsResp.
     */
    public QueryLastMsgsResp() {
        super(CommandId.QUERY_LAST_MSGS_RESP);
    }

    /**
     * Create a new QueryLastMsgsResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public QueryLastMsgsResp(SMPPPacket request) {
        super(request);
    }

    /**
     * Add a message Id to the response packet. A maximum of 255 message IDs
     * can be added, since the size specifier for the encoded packet is only
     * one byte. If an attempt is made to add more than 255 message IDs, this
     * method will fail silently.
     * @param id
     *            The message Id to add to the packet.
     * @return The current number of message Ids (including the new one).
     */
    public int addMessageId(String id) {
        if (messageTable.size() < MAX_SIZE) {
            messageTable.add(id);
        }
        return messageTable.size();
    }

    /** Get the number of message Ids. */
    public int getMsgCount() {
        return messageTable.size();
    }

    /**
     * Get a String array of the message Ids.
     * @return A String array of all the message Ids. Will never return
     * <code>null</code>, if the table is empty a zero-length array will be
     * returned.
     */
    public String[] getMessageIds() {
        return (String[]) messageTable.toArray(new String[0]);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            QueryLastMsgsResp other = (QueryLastMsgsResp) obj;
            equals |= safeCompare(messageTable, other.messageTable);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (messageTable != null) ? messageTable.hashCode() : 71;
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("msgCount=").append(messageTable.size())
        .append(",messageIds=").append(messageTable);
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        messageTable = new ArrayList<String>();
        int count = decoder.readUInt1();
        for (int i = 0; i < count; i++) {
            messageTable.add(decoder.readCString());
        }
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        int count = messageTable.size();
        encoder.writeUInt1(count);
        for (String s : messageTable) {
            encoder.writeCString(s);
        }
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 1;
        for (String s : messageTable) {
            length += (1 + sizeOf(s));
        }
        return length;
    }
}
