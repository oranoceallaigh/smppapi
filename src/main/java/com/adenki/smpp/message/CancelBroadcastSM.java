package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Cancel a previously submitted broadcast message.
 * @version $Id$
 * @since 0.4.0
 */
public class CancelBroadcastSM extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String serviceType;
    private String messageId;
    private Address source;
    
    public CancelBroadcastSM() {
        super (CommandId.CANCEL_BROADCAST_SM);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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
            CancelBroadcastSM other = (CancelBroadcastSM) obj;
            equals |= safeCompare(serviceType, other.serviceType);
            equals |= safeCompare(messageId, other.messageId);
            equals |= safeCompare(source, other.source);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (serviceType != null) ? serviceType.hashCode() : 0;
        hc += (messageId != null) ? messageId.hashCode() : 31;
        hc += (source != null) ? source.hashCode() : 97;
        return hc;
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        serviceType = decoder.readCString();
        messageId = decoder.readCString();
        source = decoder.readAddress();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(serviceType);
        encoder.writeCString(messageId);
        encoder.writeAddress(source);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 2;
        length += sizeOf(serviceType);
        length += sizeOf(messageId);
        length += sizeOf(source);
        return length;
    }
}
