package ie.omk.smpp.message;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;
import ie.omk.smpp.version.SMPPVersion;

import java.io.IOException;

/**
 * Parameter retrieve. Gets the current value of a configurable parameter at the
 * SMSC.
 * 
 * @version $Id$
 */
public class ParamRetrieve extends SMPPPacket {
    private static final long serialVersionUID = 1L;

    /** Name of the parameter to retrieve */
    private String paramName;

    /**
     * Construct a new ParamRetrieve.
     */
    public ParamRetrieve() {
        super(CommandId.PARAM_RETRIEVE);
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
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            ParamRetrieve other = (ParamRetrieve) obj;
            equals |= safeCompare(paramName, other.paramName);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (paramName != null) ? paramName.hashCode() : 0;
        return hc;
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
    protected void readMandatory(PacketDecoder decoder) {
        paramName = decoder.readCString();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(paramName);
    }
    
    @Override
    protected int getMandatorySize() {
        return 1 + sizeOf(paramName);
    }
}
