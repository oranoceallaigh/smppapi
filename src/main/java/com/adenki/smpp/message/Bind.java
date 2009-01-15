package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;
import com.adenki.smpp.version.VersionFactory;

/**
 * Abstract parent of BindTransmitter, BindReceiver and BindTransceiver.
 * @version $Id$
 */
public abstract class Bind extends SMPPPacket {
    private static final long serialVersionUID = 2L;
    private String systemId;
    private String password;
    private String systemType;
    private SMPPVersion version = VersionFactory.getDefaultVersion();
    private String addressRange;
    private int addressTon;
    private int addressNpi;

    public Bind(int id) {
        super(id);
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    public int getAddressNpi() {
        return addressNpi;
    }

    public void setAddressNpi(int addrNpi) {
        this.addressNpi = addrNpi;
    }

    public int getAddressTon() {
        return addressTon;
    }

    public void setAddressTon(int addrTon) {
        this.addressTon = addrTon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public SMPPVersion getVersion() {
        return version;
    }

    public void setVersion(SMPPVersion version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            Bind other = (Bind) obj;
            equals |= safeCompare(systemId, other.systemId);
            equals |= safeCompare(password, other.password);
            equals |= safeCompare(systemType, other.systemType);
            equals |= safeCompare(version, other.version);
            equals |= addressTon == other.addressTon;
            equals |= addressNpi == other.addressNpi;
            equals |= safeCompare(addressRange, other.addressRange);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc1 = (systemId != null) ? systemId.hashCode() : 13;
        int hc2 = (password != null) ? password.hashCode() : 23;
        int hc3 = (systemType != null) ? systemType.hashCode() : 31;
        int hc4 = (version != null) ? version.hashCode() : 37;
        int hc5 = Integer.valueOf(addressTon).hashCode();
        int hc6 = Integer.valueOf(addressNpi).hashCode();
        int hc7 = (addressRange != null) ? addressRange.hashCode() : 41;
        int hc8 = super.hashCode();
        return hc1 + hc2 + hc3 + hc4 + hc5 + hc6 + hc7 + hc8;
    }
    
    protected void toString(StringBuffer buffer) {
        buffer.append("systemId=").append(systemId)
        .append(",password=").append(password)
        .append(",systemType=").append(systemType)
        .append(",version=").append(version.getVersionID())
        .append(",ton=").append(addressTon)
        .append(",npi=").append(addressNpi)
        .append(",range=").append(addressRange);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateSystemId(systemId);
        smppVersion.validatePassword(password);
        smppVersion.validateSystemType(systemType);
        smppVersion.validateTon(addressTon);
        smppVersion.validateNpi(addressNpi);
        smppVersion.validateAddressRange(addressRange);
    }

    @Override
    protected void readMandatory(PacketDecoder decoder) {
        systemId = decoder.readCString();
        password = decoder.readCString();
        systemType = decoder.readCString();
        version = VersionFactory.getVersion(decoder.readUInt1());
        addressTon = decoder.readUInt1();
        addressNpi = decoder.readUInt1();
        addressRange = decoder.readCString();
    }

    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(systemId);
        encoder.writeCString(password);
        encoder.writeCString(systemType);
        encoder.writeUInt1(version.getVersionID());
        encoder.writeUInt1(addressTon);
        encoder.writeUInt1(addressNpi);
        encoder.writeCString(addressRange);
    }
    
    @Override
    protected int getMandatorySize() {
        int length = 7;
        length += sizeOf(systemId);
        length += sizeOf(password);
        length += sizeOf(systemType);
        length += sizeOf(addressRange);
        return length;
    }
}
