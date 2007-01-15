package ie.omk.smpp.message;

import java.util.List;

/**
 * SMSC response to a Bind request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class BindResp extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    /** System Id */
    private String sysId;

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
    
    /**
     * Set the system Id
     * 
     * @param sysId
     *            The new System Id string (Up to 15 characters)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the system id is too long.
     */
    public void setSystemId(String sysId) throws InvalidParameterValueException {
        if (sysId != null) {
            if (version.validateSystemId(sysId)) {
                this.sysId = sysId;
            } else {
                throw new InvalidParameterValueException("Invalid system Id",
                        sysId);
            }
        } else {
            this.sysId = null;
            return;
        }
    }

    /** Get the system Id */
    public String getSystemId() {
        return sysId;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_resp");
    }

    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                sysId,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        sysId = (String) params.get(0);
    }
}
