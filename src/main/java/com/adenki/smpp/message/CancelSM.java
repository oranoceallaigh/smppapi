package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Cancal message. This SMPP message is used to cancel a previously submitted
 * but yet undelivered short message at the SMSC. Relevant inherited fields from
 * SMPPPacket: <br>
 * <ul>
 * serviceType <br>
 * messageId <br>
 * source <br>
 * destination <br>
 * </ul>
 * 
 * @version $Id$
 */
public class CancelSM extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    private String serviceType;
    private String messageId;
    private Address source;
    private Address destination;
    
    /**
     * Construct a new CancelSM.
     */
    public CancelSM() {
        super(CommandId.CANCEL_SM);
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
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
            CancelSM other = (CancelSM) obj;
            equals |= safeCompare(serviceType, other.serviceType);
            equals |= safeCompare(messageId, other.messageId);
            equals |= safeCompare(source, other.source);
            equals |= safeCompare(destination, other.destination);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (serviceType != null) ? serviceType.hashCode() : 0;
        hc += (messageId != null) ? messageId.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        hc += (destination != null) ? destination.hashCode() : 0;
        return hc;
    }
    
    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("serviceType=").append(serviceType)
        .append(",messageId=").append(messageId)
        .append(",source=").append(source)
        .append(",destination=").append(destination);
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateServiceType(serviceType);
        smppVersion.validateMessageId(messageId);
        smppVersion.validateAddress(source);
        smppVersion.validateAddress(destination);
    }
    
    @Override
    protected void readMandatory(PacketDecoder decoder) {
        serviceType = decoder.readCString();
        messageId = decoder.readCString();
        source = decoder.readAddress();
        destination = decoder.readAddress();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(serviceType);
        encoder.writeCString(messageId);
        encoder.writeAddress(source);
        encoder.writeAddress(destination);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 2;
        length += sizeOf(serviceType);
        length += sizeOf(messageId);
        length += sizeOf(source);
        length += sizeOf(destination);
        return length;
    }
}
