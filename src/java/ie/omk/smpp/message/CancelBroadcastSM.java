package ie.omk.smpp.message;

import java.util.List;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;

/**
 * Cancel a previously submitted broadcast message.
 * @version $Id:$
 */
public class CancelBroadcastSM extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor bodyDescriptor = new BodyDescriptor();
    
    private String serviceType;
    private String messageId;
    private Address source;
    
    static {
        bodyDescriptor.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS);
    }
    
    public CancelBroadcastSM() {
        super (SMPPPacket.CANCEL_BROADCAST_SM);
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
    protected BodyDescriptor getBodyDescriptor() {
        return bodyDescriptor;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                serviceType,
                messageId,
                source,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        messageId = (String) params.get(1);
        source = (Address) params.get(3);
    }
}
