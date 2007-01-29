package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.BytesParamDescriptor;
import ie.omk.smpp.message.param.DestinationTableParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Response to Query message details. Gives all details of a specified message
 * at the SMSC.
 * 
 * @author Oran Kelly
 * @version $Id: $
 */
public class QueryMsgDetailsResp extends SMPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    /**
     * Table of destinations the message was routed to.
     */
    private DestinationTable destinationTable = new DestinationTable();

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.INTEGER1)
        .add(new DestinationTableParamDescriptor(2))
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(new BytesParamDescriptor(10))
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1);
    }

    /**
     * Construct a new QueryMsgDetailsResp.
     */
    public QueryMsgDetailsResp() {
        super(QUERY_MSG_DETAILS_RESP);
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

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
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
    protected void toString(StringBuffer buffer) {
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
        .append(",smLength=").append(getMessageLen())
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
        smppVersion.validateMessage(message, 0, getMessageLen());
        smppVersion.validateMessageId(messageId);
        smppVersion.validateMessageState(messageStatus);
        smppVersion.validateErrorCode(errorCode);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        int length = 0;
        if (message != null) {
            length = message.length;
        }
        return new Object[] {
                serviceType,
                source,
                Integer.valueOf(destinationTable.size()),
                destinationTable,
                Integer.valueOf(protocolID),
                Integer.valueOf(priority),
                deliveryTime,
                expiryTime,
                Integer.valueOf(registered),
                Integer.valueOf(dataCoding),
                Integer.valueOf(length),
                message,
                messageId,
                finalDate,
                Integer.valueOf(messageStatus),
                Integer.valueOf(errorCode),
        };
    }

    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        source = (Address) params.get(1);
        // index 2 intentionally skipped
        destinationTable = (DestinationTable) params.get(3);
        protocolID = ((Number) params.get(4)).intValue();
        priority = ((Number) params.get(5)).intValue();
        deliveryTime = (SMPPDate) params.get(6);
        expiryTime = (SMPPDate) params.get(7);
        registered = ((Number) params.get(8)).intValue();
        dataCoding = ((Number) params.get(9)).intValue();
        // index 10 intentionally skipped
        message = (byte[]) params.get(11);
        messageId = (String) params.get(12);
        finalDate = (SMPPDate) params.get(13);
        messageStatus = ((Number) params.get(14)).intValue();
        errorCode = ((Number) params.get(15)).intValue();
    }
}
