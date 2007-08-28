package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

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
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();

    private String serviceType;
    private String messageId;
    private Address source;
    private Address destination;
    
    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.ADDRESS);
    }
    
    /**
     * Construct a new CancelSM.
     */
    public CancelSM() {
        super(CANCEL_SM);
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
    protected void toString(StringBuffer buffer) {
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
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                serviceType,
                messageId,
                source,
                destination,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        messageId = (String) params.get(1);
        source  = (Address) params.get(2);
        destination = (Address) params.get(3);
    }
}
