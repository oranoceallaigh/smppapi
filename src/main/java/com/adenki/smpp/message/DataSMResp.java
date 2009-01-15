package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;


/**
 * Response to a data_sm.
 * @version $Id$
 */
public class DataSMResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    private String messageId;
    
    /**
     * Construct a new DataSMResp.
     */
    public DataSMResp() {
        super(CommandId.DATA_SM_RESP);
    }

    /**
     * Create a new DataSMResp packet in response to a DataSM. This constructor
     * will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public DataSMResp(SMPPPacket request) {
        super(request);
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
            DataSMResp other = (DataSMResp) obj;
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
    protected void toString(StringBuilder buffer) {
        buffer.append("messageId=").append(messageId);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
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
