package com.adenki.smpp.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.SMPPDate;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Replace a message. This message submits a short message to the SMSC replacing
 * a previously submitted message.
 * 
 * @version $Id$
 */
public class ReplaceSM extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String messageId;
    private Address source;
    private SMPPDate deliveryTime;
    private SMPPDate expiryTime;
    private int registered;
    private int defaultMsg;
    private byte[] message;

    /**
     * Construct a new ReplaceSM.
     */
    public ReplaceSM() {
        super(CommandId.REPLACE_SM);
    }
    
    public int getDefaultMsg() {
        return defaultMsg;
    }

    public void setDefaultMsg(int defaultMsg) {
        this.defaultMsg = defaultMsg;
    }

    public SMPPDate getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(SMPPDate deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public SMPPDate getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(SMPPDate expiryTime) {
        this.expiryTime = expiryTime;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    /**
     * Get the number of octets in the message payload.
     * 
     * @return The number of octets (bytes) in the message payload.
     */
    public int getMessageLen() {
        return sizeOf(message);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            ReplaceSM other = (ReplaceSM) obj;
            equals |= safeCompare(messageId, other.messageId);
            equals |= safeCompare(source, other.source);
            equals |= safeCompare(deliveryTime, other.deliveryTime);
            equals |= safeCompare(expiryTime, other.expiryTime);
            equals |= registered == other.registered;
            equals |= defaultMsg == other.defaultMsg;
            equals |= Arrays.equals(message, other.message);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (messageId != null) ? messageId.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        hc += (deliveryTime != null) ? deliveryTime.hashCode() : 0;
        hc += (expiryTime != null) ? expiryTime.hashCode() : 0;
        hc += Integer.valueOf(registered).hashCode();
        hc += Integer.valueOf(defaultMsg).hashCode();
        if (message != null) {
            try {
                hc += new String(message, "US-ASCII").hashCode();
            } catch (UnsupportedEncodingException x) {
                throw new RuntimeException(x);
            }
        }
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",source=").append(source)
        .append(",deliveryTime=").append(deliveryTime)
        .append(",expiryTime=").append(expiryTime)
        .append(",registered=").append(registered)
        .append(",defaultMsg=").append(defaultMsg)
        .append(",length=").append(getMessageLen())
        .append(",message=").append(message);
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
        smppVersion.validateAddress(source);
        smppVersion.validateRegisteredDelivery(registered);
        smppVersion.validateDefaultMsg(defaultMsg);
        smppVersion.validateMessage(message, 0, getMessageLen());
    }
    
    @Override
    protected void readMandatory(PacketDecoder decoder) {
        messageId = decoder.readCString();
        source = decoder.readAddress();
        deliveryTime = decoder.readDate();
        expiryTime = decoder.readDate();
        registered = decoder.readUInt1();
        defaultMsg = decoder.readUInt1();
        int len = decoder.readUInt1();
        message = decoder.readBytes(len);
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(messageId);
        encoder.writeAddress(source);
        encoder.writeDate(deliveryTime);
        encoder.writeDate(expiryTime);
        encoder.writeUInt1(registered);
        encoder.writeUInt1(defaultMsg);
        int len = (message != null) ? message.length : 0;
        encoder.writeUInt1(len);
        encoder.writeBytes(message, 0, len);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 4;
        length += sizeOf(messageId);
        length += sizeOf(source);
        length += sizeOf(deliveryTime);
        length += sizeOf(expiryTime);
        length += sizeOf(message);
        return length;
    }
}
