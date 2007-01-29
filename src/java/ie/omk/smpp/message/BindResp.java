package ie.omk.smpp.message;

import ie.omk.smpp.message.param.ParamDescriptor;

import java.util.List;

/**
 * SMSC response to a Bind request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class BindResp extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    private String systemId;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING);
    }
    
    /**
     * Construct a new BindResp.
     */
    protected BindResp(int id) {
        super(id);
    }

    protected BindResp(SMPPPacket request) {
        super(request);
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("systemId=").append(systemId);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                systemId,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        systemId = (String) params.get(0);
    }
}
