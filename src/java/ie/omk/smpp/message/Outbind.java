package ie.omk.smpp.message;

import java.util.List;


/**
 * $Id:$
 */
public class Outbind extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    private String systemId;
    private String password;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.CSTRING);
    }
    
    public Outbind() {
        super(SMPPPacket.OUTBIND);
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        systemId = (String) params.get(0);
        password = (String) params.get(1);
    }
}
