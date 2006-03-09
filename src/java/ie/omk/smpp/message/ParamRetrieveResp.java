package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * SMSC response to a ParamRetrieve request. Returns the value of the requested
 * parameter.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class ParamRetrieveResp extends ie.omk.smpp.message.SMPPResponse {
    /** String value of the requested parameter */
    private String paramValue;

    /**
     * Construct a new BindReceiverResp.
     */
    public ParamRetrieveResp() {
        super(PARAM_RETRIEVE_RESP);
        paramValue = null;
    }

    /**
     * Construct a new BindReceiverResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public ParamRetrieveResp(int seqNum) {
        super(PARAM_RETRIEVE_RESP, seqNum);
        paramValue = null;
    }

    /**
     * Create a new ParamRetrieveResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public ParamRetrieveResp(ParamRetrieve r) {
        super(r);
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

    public int getBodyLength() {
        int len = (paramValue != null) ? paramValue.length() : 0;

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
        SMPPIO.writeCString(paramValue, out);
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        paramValue = SMPPIO.readCString(body, offset);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("param_retrieve_resp");
    }
}

