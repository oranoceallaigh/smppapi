package ie.omk.smpp.message;

import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * SMSC response to a ParamRetrieve request. Returns the value of the requested
 * parameter.
 * 
 * @version $Id$
 */
public class ParamRetrieveResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;

    /** String value of the requested parameter */
    private String paramValue;

    /**
     * Construct a new BindReceiverResp.
     */
    public ParamRetrieveResp() {
        super(PARAM_RETRIEVE_RESP);
    }

    /**
     * Create a new ParamRetrieveResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public ParamRetrieveResp(SMPPPacket request) {
        super(request);
    }

    /**
     * Set the parameter value.
     * @param paramValue
     *            Value to be returned for the requested parameter.
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    /** Get the value of the parameter */
    public String getParamValue() {
        return paramValue;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("paramValue=").append(paramValue);
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateParamValue(paramValue);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BodyDescriptor.ONE_CSTRING;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                paramValue,
        };
    }

    @Override
    protected void setMandatoryParameters(List<Object> params) {
        paramValue = (String) params.get(0);
    }
}
