package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * SMSC response to a Bind request.
 * 
 * @version $Id$
 */
public abstract class BindResp extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    private String systemId;

    /**
     * Construct a new BindResp.
     */
    protected BindResp(int id) {
        super(id);
    }

    protected BindResp(SMPPPacket request) {
        super(request);
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            BindResp other = (BindResp) obj;
            equals |= safeCompare(systemId, other.systemId);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc1 = (systemId != null) ? systemId.hashCode() : 996631;
        return super.hashCode() + hc1;
    }
    
    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("systemId=").append(systemId);
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateSystemId(systemId);
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        systemId = decoder.readCString();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(systemId);
    }
    
    @Override
    protected int getMandatorySize() {
        return 1 + sizeOf(systemId);
    }
}
