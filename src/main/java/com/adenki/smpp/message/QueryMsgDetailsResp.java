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
 * Response to Query message details. Gives all details of a specified message
 * at the SMSC.
 * 
 * @version $Id$
 */
public class QueryMsgDetailsResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    
    private String serviceType;
    private Address source;
    private DestinationTable destinationTable = new DestinationTable();
    private int protocolID;
    private int priority;
    private SMPPDate deliveryTime;
    private SMPPDate expiryTime;
    private int registered;
    private int dataCoding;
    private byte[] message;
    private String messageId;
    private SMPPDate finalDate;
    private MessageState messageStatus = MessageState.UNKNOWN;
    private int errorCode;

    /**
     * Construct a new QueryMsgDetailsResp.
     */
    public QueryMsgDetailsResp() {
        super(CommandId.QUERY_MSG_DETAILS_RESP);
    }

    /**
     * Create a new QueryMsgDetailsResp packet in response to a BindReceiver.
     * This constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public QueryMsgDetailsResp(SMPPPacket request) {
        super(request);
    }
    
    public int getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(int dataCoding) {
        this.dataCoding = dataCoding;
    }

    public SMPPDate getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(SMPPDate deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public SMPPDate getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(SMPPDate expiryTime) {
        this.expiryTime = expiryTime;
    }

    public SMPPDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(SMPPDate finalDate) {
        this.finalDate = finalDate;
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

    public MessageState getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageState messageStatus) {
        this.messageStatus = messageStatus;
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
     * @param address
     *            The SME destination address
     * @return The current number of destination addresses (including the new
     *         one).
     * @see Address
     */
    public int addDestination(Address address) {
        synchronized (destinationTable) {
            destinationTable.add(address);
            return destinationTable.size();
        }
    }

    /**
     * Add a distribution list to the destination table.
     * 
     * @param distributionList
     *            the distribution list name.
     * @return The current number of destination addresses (including the new
     */
    public int addDestination(String distributionList) {
        synchronized (destinationTable) {
            destinationTable.add(distributionList);
            return destinationTable.size();
        }
    }

    /**
     * Get the current number of destination addresses.
     */
    public int getNumDests() {
        return destinationTable.size();
    }

    /**
     * Get a handle to the destination table.
     */
    public DestinationTable getDestinationTable() {
        return destinationTable;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            QueryMsgDetailsResp other = (QueryMsgDetailsResp) obj;
            equals |= safeCompare(serviceType, other.serviceType);
            equals |= safeCompare(source, other.source);
            equals |= safeCompare(destinationTable, other.destinationTable);
            equals |= protocolID == other.protocolID;
            equals |= priority == other.priority;
            equals |= safeCompare(deliveryTime, other.deliveryTime);
            equals |= safeCompare(expiryTime, other.expiryTime);
            equals |= registered == other.registered;
            equals |= dataCoding == other.dataCoding;
            equals |= Arrays.equals(message, other.message);
            equals |= messageId == other.messageId;
            equals |= safeCompare(finalDate, other.finalDate);
            equals |= messageStatus == other.messageStatus;
            equals |= errorCode == other.errorCode;
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (serviceType != null) ? serviceType.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        hc += (destinationTable != null) ? destinationTable.hashCode() : 13;
        hc += Integer.valueOf(protocolID).hashCode();
        hc += Integer.valueOf(priority).hashCode();
        hc += (deliveryTime != null) ? deliveryTime.hashCode() : 0;
        hc += (expiryTime != null) ? expiryTime.hashCode() : 0;
        hc += Integer.valueOf(registered).hashCode();
        hc += Integer.valueOf(dataCoding).hashCode();
        if (message != null) {
            try {
                hc += new String(message, "US-ASCII").hashCode();
            } catch (UnsupportedEncodingException x) {
                throw new RuntimeException(x);
            }
        }
        hc += Integer.valueOf(messageId).hashCode();
        hc += (finalDate != null) ? finalDate.hashCode() : 0;
        hc += Integer.valueOf(messageStatus.getValue()).hashCode();
        hc += Integer.valueOf(errorCode).hashCode();
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("serviceType=").append(serviceType)
        .append(",source=").append(source)
        .append(",numberOfDests=").append(destinationTable.size())
        .append(",destinations=").append(destinationTable)
        .append(",protocolID=").append(protocolID)
        .append(",priority=").append(priority)
        .append(",deliveryTime=").append(deliveryTime)
        .append(",expiryTime=").append(expiryTime)
        .append(",registered=").append(registered)
        .append(",dataCoding=").append(dataCoding)
        .append(",smLength=").append(sizeOf(message))
        .append(",message=").append(message)
        .append(",messageId=").append(messageId)
        .append(",finalDate=").append(finalDate)
        .append(",messageStatus=").append(messageStatus)
        .append(",errorCode=").append(errorCode);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateServiceType(serviceType);
        smppVersion.validateAddress(source);
        smppVersion.validateNumberOfDests(destinationTable.size());
        for (Address address : destinationTable.getAddresses()) {
            smppVersion.validateAddress(address);
        }
        for (String distList : destinationTable.getDistributionLists()) {
            smppVersion.validateDistListName(distList);
        }
        smppVersion.validateProtocolID(protocolID);
        smppVersion.validatePriorityFlag(priority);
        smppVersion.validateRegisteredDelivery(registered);
        smppVersion.validateDataCoding(dataCoding);
        smppVersion.validateMessage(message, 0, sizeOf(message));
        smppVersion.validateMessageId(messageId);
        smppVersion.validateErrorCode(errorCode);
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        serviceType = decoder.readCString();
        source = decoder.readAddress();
        int tableSize = decoder.readUInt1();
        destinationTable = new DestinationTable();
        destinationTable.readFrom(decoder, tableSize);
        protocolID = decoder.readUInt1();
        priority = decoder.readUInt1();
        deliveryTime = decoder.readDate();
        expiryTime = decoder.readDate();
        registered = decoder.readUInt1();
        dataCoding = decoder.readUInt1();
        int messageLen = decoder.readUInt1();
        message = decoder.readBytes(messageLen);
        messageId = decoder.readCString();
        finalDate = decoder.readDate();
        messageStatus = MessageState.getMessageState(decoder.readUInt1());
        errorCode = decoder.readUInt1();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(serviceType);
        encoder.writeAddress(source);
        encoder.writeUInt1(destinationTable.size());
        destinationTable.writeTo(encoder);
        encoder.writeUInt1(protocolID);
        encoder.writeUInt1(priority);
        encoder.writeDate(deliveryTime);
        encoder.writeDate(expiryTime);
        encoder.writeUInt1(registered);
        encoder.writeUInt1(dataCoding);
        int messageLen = (message != null) ? message.length : 0;
        encoder.writeUInt1(messageLen);
        encoder.writeBytes(message, 0, messageLen);
        encoder.writeCString(messageId);
        encoder.writeDate(finalDate);
        encoder.writeUInt1(messageStatus.getValue());
        encoder.writeUInt1(errorCode);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 10;
        length += sizeOf(serviceType);
        length += sizeOf(source);
        length += destinationTable.getLength();
        length += sizeOf(deliveryTime);
        length += sizeOf(expiryTime);
        length += sizeOf(message);
        length += sizeOf(messageId);
        length += sizeOf(finalDate);
        return length;
    }
}
