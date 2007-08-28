package ie.omk.smpp.message;

import java.util.List;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

/**
 * Submit a broadcast message to the SMSC.
 * @version $Id:$
 */
public class BroadcastSM extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor bodyDescriptor = new BodyDescriptor();
    
    private String serviceType;
    private Address source;
    private String messageId;
    private int priority;
    private SMPPDate deliveryTime;
    private SMPPDate expiryTime;
    private int replaceIfPresent;
    private int dataCoding;
    private int defaultMsg;
    
    static {
        bodyDescriptor.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1);
    }
    
    public BroadcastSM() {
        super (SMPPPacket.BROADCAST_SM);
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

    public SMPPDate getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(SMPPDate expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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
    protected BodyDescriptor getBodyDescriptor() {
        return bodyDescriptor;
    }

    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                serviceType,
                source,
                messageId,
                Integer.valueOf(priority),
                deliveryTime,
                expiryTime,
                Integer.valueOf(replaceIfPresent),
                Integer.valueOf(dataCoding),
                Integer.valueOf(defaultMsg),
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        source = (Address) params.get(1);
        messageId = (String) params.get(2);
        priority = ((Number) params.get(3)).intValue();
        deliveryTime = (SMPPDate) params.get(4);
        expiryTime = (SMPPDate) params.get(5);
        replaceIfPresent = ((Number) params.get(6)).intValue();
        dataCoding = ((Number) params.get(7)).intValue();
        defaultMsg = ((Number) params.get(8)).intValue();
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateServiceType(serviceType);
        smppVersion.validateAddress(source);
        smppVersion.validateMessageId(messageId);
        smppVersion.validatePriorityFlag(priority);
        smppVersion.validateReplaceIfPresent(replaceIfPresent);
        smppVersion.validateDataCoding(dataCoding);
        smppVersion.validateDefaultMsg(defaultMsg);
    }
    
    protected boolean validateTLVTable(SMPPVersion version) {
        boolean valid = true;
        TLVTable tlvTable = getTLVTable();
        valid &= tlvTable.containsKey(Tag.BROADCAST_AREA_IDENTIFIER);
        valid &= tlvTable.containsKey(Tag.BROADCAST_CONTENT_TYPE);
        valid &= tlvTable.containsKey(Tag.BROADCAST_REP_NUM);
        valid &= tlvTable.containsKey(Tag.BROADCAST_FREQUENCY_INTERVAL);
        return valid;
    }
}
