package ie.omk.smpp.message;

import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.version.SMPPVersion;
import ie.omk.smpp.version.VersionFactory;

import java.util.List;

/**
 * Abstract parent of BindTransmitter, BindReceiver and BindTransceiver.
 * @version $Id: $
 */
public abstract class Bind extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();

    private String systemId;
    private String password;
    private String systemType;
    private SMPPVersion version;
    private String addressRange;
    private int addressTon;
    private int addressNpi;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.CSTRING);
    }
    
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
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }

    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                systemId,
                password,
                systemType,
                Integer.valueOf(version.getVersionID()),
                Integer.valueOf(addressTon),
                Integer.valueOf(addressNpi),
                addressRange,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        systemId = (String) params.get(0);
        password = (String) params.get(1);
        systemType = (String) params.get(2);
        version = VersionFactory.getVersion(((Number) params.get(3)).intValue());
        addressTon = ((Number) params.get(4)).intValue();
        addressNpi = ((Number) params.get(5)).intValue();
        addressRange = (String) params.get(6);
    }
}
