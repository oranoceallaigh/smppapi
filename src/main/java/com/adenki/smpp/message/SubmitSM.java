package com.adenki.smpp.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.SMPPDate;
import com.adenki.smpp.util.StringUtil;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Submit a message to the SMSC for delivery to a single destination.
 * 
 * @version $Id$
 */
public class SubmitSM extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String serviceType;
    private Address source;
    private Address destination;
    private int esmClass;
    private int protocolID;
    private int priority;
    private SMPPDate deliveryTime;
    private SMPPDate expiryTime;
    private int registered;
    private int replaceIfPresent;
    private int dataCoding;
    private int defaultMsg;
    private byte[] message;

    public SubmitSM() {
        super(CommandId.SUBMIT_SM);
    }
    
    SubmitSM(int commandId) {
        // Convenience constructor provided for deliver_sm.
        super(commandId);
    }

    public int getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(int dataCoding) {
        this.dataCoding = dataCoding;
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

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public int getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(int esmClass) {
        this.esmClass = esmClass;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getProtocolID() {
        return protocolID;
    }

    public void setProtocolID(int protocolID) {
        this.protocolID = protocolID;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public int getReplaceIfPresent() {
        return replaceIfPresent;
    }

    public void setReplaceIfPresent(int replaceIfPresent) {
        this.replaceIfPresent = replaceIfPresent;
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
            SubmitSM other = (SubmitSM) obj;
            equals |= safeCompare(serviceType, other.serviceType);
            equals |= safeCompare(source, other.source);
            equals |= safeCompare(destination, other.destination);
            equals |= esmClass == other.esmClass;
            equals |= protocolID == other.protocolID;
            equals |= priority == other.priority;
            equals |= safeCompare(deliveryTime, other.deliveryTime);
            equals |= safeCompare(expiryTime, other.expiryTime);
            equals |= registered == other.registered;
            equals |= replaceIfPresent == other.replaceIfPresent;
            equals |= dataCoding == other.dataCoding;
            equals |= defaultMsg == other.defaultMsg;
            equals |= Arrays.equals(message, other.message);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (serviceType != null) ? serviceType.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        hc += (destination != null) ? destination.hashCode() : 0;
        hc += Integer.valueOf(esmClass).hashCode();
        hc += Integer.valueOf(protocolID).hashCode();
        hc += Integer.valueOf(priority).hashCode();
        hc += (deliveryTime != null) ? deliveryTime.hashCode() : 0;
        hc += (expiryTime != null) ? expiryTime.hashCode() : 0;
        hc += Integer.valueOf(registered).hashCode();
        hc += Integer.valueOf(replaceIfPresent).hashCode();
        hc += Integer.valueOf(dataCoding).hashCode();
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
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateServiceType(this.serviceType);
        smppVersion.validateAddress(this.source);
        smppVersion.validateAddress(this.destination);
        smppVersion.validateEsmClass(this.esmClass);
        smppVersion.validateProtocolID(this.protocolID);
        smppVersion.validatePriorityFlag(this.priority);
        smppVersion.validateRegisteredDelivery(this.registered);
        smppVersion.validateReplaceIfPresent(this.replaceIfPresent);
        smppVersion.validateDataCoding(this.dataCoding);
        smppVersion.validateDefaultMsg(this.defaultMsg);
        smppVersion.validateMessage(this.message, 0, sizeOf(this.message));
    }
  
    @Override
    protected void readMandatory(PacketDecoder decoder) {
        serviceType = decoder.readCString();
        source = decoder.readAddress();
        destination = decoder.readAddress();
        esmClass = decoder.readUInt1();
        protocolID = decoder.readUInt1();
        priority = decoder.readUInt1();
        deliveryTime = decoder.readDate();
        expiryTime = decoder.readDate();
        registered = decoder.readUInt1();
        replaceIfPresent = decoder.readUInt1();
        dataCoding = decoder.readUInt1();
        defaultMsg = decoder.readUInt1();
        int len = decoder.readUInt1();
        message = decoder.readBytes(len);
    }
  
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(serviceType);
        encoder.writeAddress(source);
        encoder.writeAddress(destination);
        encoder.writeUInt1(esmClass);
        encoder.writeUInt1(protocolID);
        encoder.writeUInt1(priority);
        encoder.writeDate(deliveryTime);
        encoder.writeDate(expiryTime);
        encoder.writeUInt1(registered);
        encoder.writeUInt1(replaceIfPresent);
        encoder.writeUInt1(dataCoding);
        encoder.writeUInt1(defaultMsg);
        if (message != null) {
            encoder.writeUInt1(message.length);
            encoder.writeBytes(message, 0, message.length);
        } else {
            encoder.writeUInt1(0);
        }
    }

    @Override
    protected int getMandatorySize() {
        int length = 9;
        length += sizeOf(this.serviceType);
        length += sizeOf(this.source);
        length += sizeOf(this.destination);
        length += sizeOf(this.deliveryTime);
        length += sizeOf(this.expiryTime);
        length += sizeOf(this.message);
        return length;
    }
  
    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("source=").append(source)
        .append(",destination=").append(destination)
        .append(",registered=").append(registered)
        .append(",esmClass=").append(esmClass)
        .append(",dataCoding=").append(dataCoding)
        .append(",message=");
        if (message != null) {
            buffer.append('"')
            .append(StringUtil.escapeJava(message))
            .append('"');
        } else {
            buffer.append("null");
        }
    }
}
