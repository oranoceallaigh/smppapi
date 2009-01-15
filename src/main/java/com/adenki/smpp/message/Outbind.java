package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;


/**
 * $Id$
 */
public class Outbind extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    private String systemId;
    private String password;

    public Outbind() {
        super(CommandId.OUTBIND);
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            Outbind other = (Outbind) obj;
            equals |= safeCompare(systemId, other.systemId);
            equals |= safeCompare(password, other.password);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (systemId != null) ? systemId.hashCode() : 0;
        hc += (password != null) ? password.hashCode() : 0;
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
        buffer.append("systemId=").append(systemId)
        .append(",password=").append(password);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateSystemId(systemId);
        smppVersion.validatePassword(password);
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        systemId = decoder.readCString();
        password = decoder.readCString();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(systemId);
        encoder.writeCString(password);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 2;
        length += sizeOf(systemId);
        length += sizeOf(password);
        return length;
    }
}
