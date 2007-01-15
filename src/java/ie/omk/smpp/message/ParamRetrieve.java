package ie.omk.smpp.message;

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
     * 
     * @param paramName
     *            Parameter name, up to 31 characters
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the parameter name is too long.
     */
    public void setParamName(String paramName)
            throws InvalidParameterValueException {
        if (paramName == null) {
            this.paramName = null;
            return;
        }

        if (paramName.length() < 32) {
            this.paramName = paramName;
        } else {
            throw new InvalidParameterValueException(
                    "Parameter name is invalid", paramName);
        }
    }

    /** Get the parameter name */
    public String getParamName() {
        return paramName;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("param_retrieve");
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
