package com.adenki.smpp.message;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.SMPPDate;


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
}

