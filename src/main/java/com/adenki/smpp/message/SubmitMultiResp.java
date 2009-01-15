package com.adenki.smpp.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Submit to multiple destinations response.
 * 
 * @version $Id$
 */
public class SubmitMultiResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String messageId;
    
    /** Table of unsuccessful destinations */
    private List<ErrorAddress> unsuccessfulTable = new ArrayList<ErrorAddress>();

    /**
     * Construct a new Unbind.
     */
    public SubmitMultiResp() {
        super(CommandId.SUBMIT_MULTI_RESP);
    }

    /**
     * Create a new SubmitMultiResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public SubmitMultiResp(SMPPPacket request) {
        super(request);
    }

    
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /** Get the number of destinations the message was not delivered to. */
    public int getUnsuccessfulCount() {
        return unsuccessfulTable.size();
    }

    /**
     * Add a destination address to the table of unsuccessful destinations.
     * 
     * @param ea
     *            ErrorAddress object representing the failed destination
     * @return The current count of unsuccessful destinations (including the new
     *         one)
     */
    public int add(ErrorAddress ea) {
        unsuccessfulTable.add(ea);
        return unsuccessfulTable.size();
    }

    /**
     * Remove an address from the table of unsuccessful destinations.
     * 
     * @param a
     *            the address to remove.
     * @return the size of the table after removal.
     */
    public int remove(Address a) {
        synchronized (unsuccessfulTable) {
            int i = unsuccessfulTable.indexOf(a);
            if (i > -1) {
                unsuccessfulTable.remove(i);
            }

            return unsuccessfulTable.size();
        }
    }

    /**
     * Get an iterator to iterate over the set of addresses in the unsuccessful
     * destination table.
     */
    public ListIterator<ErrorAddress> tableIterator() {
        return unsuccessfulTable.listIterator();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            SubmitMultiResp other = (SubmitMultiResp) obj;
            equals |= safeCompare(messageId, other.messageId);
            equals |= safeCompare(unsuccessfulTable, other.unsuccessfulTable);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (messageId != null) ? messageId.hashCode() : 0;
        hc += (unsuccessfulTable != null) ? unsuccessfulTable.hashCode() : 0;
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",unsuccessfulCount=").append(unsuccessfulTable.size())
        .append(",unsuccessful=").append(unsuccessfulTable);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
        smppVersion.validateNumUnsuccessful(unsuccessfulTable.size());
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        messageId = decoder.readCString();
        int count = decoder.readUInt1();
        unsuccessfulTable = new ArrayList<ErrorAddress>();
        for (int i = 0; i < count; i++) {
            unsuccessfulTable.add(decoder.readErrorAddress());
        }
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(messageId);
        encoder.writeUInt1(unsuccessfulTable.size());
        for (ErrorAddress errorAddress : unsuccessfulTable) {
            encoder.writeErrorAddress(errorAddress);
        }
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 2;
        length += sizeOf(messageId);
        for (ErrorAddress ea : unsuccessfulTable) {
            length += ea.getLength();
        }
        return length;
    }
}
