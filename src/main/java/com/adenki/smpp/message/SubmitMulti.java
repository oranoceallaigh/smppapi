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
 * Submit a message to multiple destinations.
 * 
 * @version $Id$
 */
public class SubmitMulti extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String serviceType;
    private Address source;
    private DestinationTable destinationTable = new DestinationTable();
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

    /**
     * Construct a new SubmitMulti.
     */
    public SubmitMulti() {
        super(CommandId.SUBMIT_MULTI);
    }

    /**
     * Get a handle to the error destination table. Applications may add
     * destination addresses or distribution list names to the destination
     * table.
     */
    public DestinationTable getDestinationTable() {
        return destinationTable;
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

    /**
     * Add an address to the destination table.
     * 
     * @param d
     *            The SME destination address
     * @return The current number of destination addresses (including the new
     *         one).
     * @see Address
     */
    public int addDestination(Address d) {
        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Add a distribution list to the destination table.
     * 
     * @param d
     *            the distribution list name.
     * @return The current number of destination addresses (including the new
     */
    public int addDestination(String d) {
        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Get the number of destinations in the destination table.
     */
    public int getNumDests() {
        return destinationTable.size();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            SubmitMulti other = (SubmitMulti) obj;
            equals |= safeCompare(serviceType, other.serviceType);
            equals |= safeCompare(source, other.source);
            equals |= safeCompare(destinationTable, other.destinationTable);
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
        hc += (destinationTable != null) ? destinationTable.hashCode() : 0;
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
    protected void toString(StringBuilder buffer) {
        int length = 0;
        if (message != null) {
            length = message.length;
        }
        buffer.append("serviceType=").append(serviceType)
        .append(",source=").append(source)
        .append(",numberOfDests=").append(destinationTable.size())
        .append(",destinations=").append(destinationTable)
        .append(",esmClass=").append(esmClass)
        .append(",protocolID=").append(protocolID)
        .append(",priority=").append(priority)
        .append(",deliveryTime=").append(deliveryTime)
        .append(",expiryTime=").append(expiryTime)
        .append(",registered=").append(registered)
        .append(",replaceIfPresent=").append(replaceIfPresent)
        .append(",dataCoding=").append(dataCoding)
        .append(",defaultMsg=").append(defaultMsg)
        .append(",smLength=").append(length)
        .append(",message=").append(message);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        super.validateMandatory(smppVersion);
        smppVersion.validateNumberOfDests(destinationTable.size());
        for (Address address : destinationTable.getAddresses()) {
            smppVersion.validateAddress(address);
        }
        for (String distributionList : destinationTable.getDistributionLists()) {
            smppVersion.validateDistListName(distributionList);
        }
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        serviceType = decoder.readCString();
        source = decoder.readAddress();
        int numDests = decoder.readUInt1();
        destinationTable = new DestinationTable();
        destinationTable.readFrom(decoder, numDests);
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
        int numDests = destinationTable.size();
        encoder.writeUInt1(numDests);
        destinationTable.writeTo(encoder);
        encoder.writeUInt1(esmClass);
        encoder.writeUInt1(protocolID);
        encoder.writeUInt1(priority);
        encoder.writeDate(deliveryTime);
        encoder.writeDate(expiryTime);
        encoder.writeUInt1(registered);
        encoder.writeUInt1(replaceIfPresent);
        encoder.writeUInt1(dataCoding);
        encoder.writeUInt1(defaultMsg);
        int len = (message != null) ? message.length : 0;
        encoder.writeUInt1(len);
        encoder.writeBytes(message, 0, len);
    }
    
    @Override
    protected int getMandatorySize() {
        int l = 10;
        l += sizeOf(serviceType);
        l += sizeOf(source);
        l += destinationTable.getLength();
        l += sizeOf(deliveryTime);
        l += sizeOf(expiryTime);
        l += sizeOf(message);
        return l;
    }
}
