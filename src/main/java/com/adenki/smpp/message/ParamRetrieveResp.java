package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * SMSC response to a ParamRetrieve request. Returns the value of the requested
 * parameter.
 * 
 * @version $Id$
 */
public class ParamRetrieveResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    /** String value of the requested parameter */
    private String paramValue;

    /**
     * Construct a new BindReceiverResp.
     */
    public ParamRetrieveResp() {
        super(CommandId.PARAM_RETRIEVE_RESP);
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
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            ParamRetrieveResp other = (ParamRetrieveResp) obj;
            equals |= safeCompare(paramValue, other.paramValue);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (paramValue != null) ? paramValue.hashCode() : 0;
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("paramValue=").append(paramValue);
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateParamValue(paramValue);
    }
    
    @Override
    protected void readMandatory(PacketDecoder decoder) {
        paramValue = decoder.readCString();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(paramValue);
    }
    
    @Override
    protected int getMandatorySize() {
        return 1 + sizeOf(paramValue);
    }
}
