package ie.omk.smpp.message;

import java.util.List;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;

/**
 * Query the status of a previously submitted broadcast message.
 * @version $Id:$
 */
public class QueryBroadcastSM extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor bodyDescriptor = new BodyDescriptor();

    private String messageId;
    private Address source;
    
    static {
        bodyDescriptor.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS);
    };
    
    public QueryBroadcastSM() {
        super (SMPPPacket.QUERY_BROADCAST_SM);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
                messageId,
                source,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
        source = (Address) params.get(1);
    }
}
