package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Parameter retrieve. Gets the current value of a configurable parameter at the
 * SMSC.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class ParamRetrieve extends ie.omk.smpp.message.SMPPRequest {
    /** Name of the parameter to retrieve */
    private String paramName;

    /**
     * Construct a new ParamRetrieve.
     */
    public ParamRetrieve() {
        super(PARAM_RETRIEVE);
        paramName = null;
    }

    /**
     * Construct a new ParamRetrieve with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public ParamRetrieve(int seqNum) {
        super(PARAM_RETRIEVE, seqNum);
        paramName = null;
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

    public int getBodyLength() {
        int len = (paramName != null) ? paramName.length() : 0;

        // 1 c-string
        return len + 1;
    }

    /**
     * Write a byte representation of this packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to write to
     * @throws java.io.IOException
     *             if there's an error writing to the output stream.
     */
    protected void encodeBody(OutputStream out) throws java.io.IOException {
        SMPPIO.writeCString(paramName, out);
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        paramName = SMPPIO.readCString(body, offset);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("param_retrieve");
    }
}

