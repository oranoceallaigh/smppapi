package ie.omk.smpp.message;

import java.util.List;

/**
 * SMSC response to a ParamRetrieve request. Returns the value of the requested
 * parameter.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class ParamRetrieveResp extends SMPPPacket {
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
     * 
     * @param v
     *            Value to be returned for the requested parameter (Up to 100
     *            characters)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the parameter value is too long.
     */
    public void setParamValue(String v) throws InvalidParameterValueException {
        if (v == null) {
            paramValue = null;
            return;
        }

        if (v.length() < 101) {
            this.paramValue = v;
        } else {
            throw new InvalidParameterValueException(
                    "Parameter value is too long", v);
        }
    }

    /** Get the value of the parameter */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("param_retrieve_resp");
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
