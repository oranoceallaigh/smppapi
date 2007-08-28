package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.BytesParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Super-class containing all of the fields that can relate to a short message.
 * This class is a convenience for packets such as SubmitSM, DeliverSM and
 * QueryMsgDetailsResp to extend.
 */
public class SMPacket extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    protected static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();

    protected String serviceType;
    protected Address source;
    protected Address destination;
    protected int esmClass;
    protected int protocolID;
    protected int priority;
    protected SMPPDate deliveryTime;
    protected SMPPDate expiryTime;
    protected int registered;
    protected int replaceIfPresent;
    protected int dataCoding;
    protected int defaultMsg;
    protected byte[] message;
    protected String messageId;
    protected SMPPDate finalDate;
    protected int messageStatus;
    protected int errorCode;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(new BytesParamDescriptor(12));
    }
    
    SMPacket(int commandId) {
        super(commandId);
    }
    
    SMPacket(SMPPPacket request) {
        super(request);
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
     * Set the message data. The data will be copied from the supplied byte
     * array into an internal one.
     * 
     * @param message
     *            The byte array to take message data from.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if start or len is less than zero or if the byte array length
     *             is less than <code>start + len</code>.
     */
    public void setMessage(byte[] message) {
        this.setMessage(message, 0, message.length);
    }

    /**
     * Set the message data. The data will be copied from the supplied byte
     * array into an internal one. If <i>encoding </i> is not null, the
     * data_coding field will be set using the value returned by
     * MessageEncoding.getDataCoding.
     * 
     * @param message
     *            The byte array to take message data from.
     * @param start
     *            The index the message data begins at.
     * @param len
     *            The length of the message data.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if start or len is less than zero or if the byte array length
     *             is less than <code>start + len</code>.
     */
    public void setMessage(byte[] message, int start, int len) {
        if (message != null) {
            this.message = new byte[len];
            System.arraycopy(message, start, this.message, 0, len);
        } else {
            this.message = null;
        }
    }

    /**
     * Get the message data.
     * @return The message data byte array. May be null.
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * Get the number of octets in the message payload.
     * 
     * @return The number of octets (bytes) in the message payload.
     */
    public int getMessageLen() {
        return (message == null) ? 0 : message.length;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("serviceType=").append(serviceType)
        .append(",source=").append(source)
        .append(",destination=").append(destination)
        .append(",esmClass=").append(esmClass)
        .append(",protocolID=").append(protocolID)
        .append(",priority=").append(priority)
        .append(",deliveryTime=").append(deliveryTime)
        .append(",expiryTime=").append(expiryTime)
        .append(",registered=").append(registered)
        .append(",replaceIfPresent=").append(replaceIfPresent)
        .append(",dataCoding=").append(dataCoding)
        .append(",defaultMsg=").append(defaultMsg)
        .append(",smLength=").append(getMessageLen())
        .append(",message=").append(message);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateServiceType(serviceType);
        smppVersion.validateAddress(source);
        smppVersion.validateAddress(destination);
        smppVersion.validateEsmClass(esmClass);
        smppVersion.validateProtocolID(protocolID);
        smppVersion.validatePriorityFlag(priority);
        smppVersion.validateRegisteredDelivery(registered);
        smppVersion.validateReplaceIfPresent(replaceIfPresent);
        smppVersion.validateDataCoding(dataCoding);
        smppVersion.validateDefaultMsg(defaultMsg);
        smppVersion.validateMessage(message, 0, getMessageLen());
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                serviceType,
                source,
                destination,
                Integer.valueOf(esmClass),
                Integer.valueOf(protocolID),
                Integer.valueOf(priority),
                deliveryTime,
                expiryTime,
                Integer.valueOf(registered),
                Integer.valueOf(replaceIfPresent),
                Integer.valueOf(dataCoding),
                Integer.valueOf(defaultMsg),
                Integer.valueOf(getMessageLen()),
                message,
        };
    }

    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        source = (Address) params.get(1);
        destination = (Address) params.get(2);
        esmClass = ((Number) params.get(3)).intValue();
        protocolID = ((Number) params.get(4)).intValue();
        priority = ((Number) params.get(5)).intValue();
        deliveryTime = (SMPPDate) params.get(6);
        expiryTime = (SMPPDate) params.get(7);
        registered = ((Number) params.get(8)).intValue();
        replaceIfPresent = ((Number) params.get(9)).intValue();
        dataCoding = ((Number) params.get(10)).intValue();
        defaultMsg = ((Number) params.get(11)).intValue();
        // Intentionally skipping param[12]
        message = (byte[]) params.get(13);
    }
}
