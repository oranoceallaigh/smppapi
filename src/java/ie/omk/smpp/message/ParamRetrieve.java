package ie.omk.smpp.message;

import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Parameter retrieve. Gets the current value of a configurable parameter at the
 * SMSC.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class ParamRetrieve extends SMPPPacket {
    /** Name of the parameter to retrieve */
    private String paramName;

    /**
     * Construct a new ParamRetrieve.
     */
    public ParamRetrieve() {
        super(PARAM_RETRIEVE);
    }

    /**
     * Set the name of the parameter to retrieve
     * @param paramName
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /** Get the parameter name */
    public String getParamName() {
        return paramName;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("paramName=").append(paramName);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateParamName(paramName);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BodyDescriptor.ONE_CSTRING;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                paramName,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        paramName = (String) params.get(0);
    }
}
