package ie.omk.smpp.message;

import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Abstract parent of BindTransmitter and BindReceiver.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class Bind extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    /** System Id */
    private String sysId;

    /** Authentication password */
    private String password;

    /** System type */
    private String sysType;

    /** Address range for message routing */
    private String addressRange;

    /** Address Type Of Number for message routing */
    private int addrTon;

    /** Address Numbering Plan Indicator for message routing */
    private int addrNpi;

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

    /**
     * Set the system Id
     * 
     * @param sysId
     *            The System Id to use (Up to 15 characters)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the system ID fails version validation.
     */
    public void setSystemId(String sysId) throws InvalidParameterValueException {
        if (sysId != null) {
            if (version.validateSystemId(sysId)) {
                this.sysId = sysId;
            } else {
                throw new InvalidParameterValueException("Invalid system ID",
                        sysId);
            }
        } else {
            this.sysId = null;
        }
    }

    /**
     * Set the password for this transmitter
     * 
     * @param password
     *            The new password to use (Up to 8 characters in length)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the password fails version validation.
     */
    public void setPassword(String password)
            throws InvalidParameterValueException {
        if (password != null) {
            if (version.validatePassword(password)) {
                this.password = password;
            } else {
                throw new InvalidParameterValueException("Invalid password",
                        password);
            }
        } else {
            this.password = null;
        }
    }

    /**
     * Set the system type for this transmitter
     * 
     * @param sysType
     *            The new system type (Up to 12 characters in length)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the system type fails version validation.
     */
    public void setSystemType(String sysType)
            throws InvalidParameterValueException {
        if (sysType != null) {
            if (version.validateSystemType(sysType)) {
                this.sysType = sysType;
            } else {
                throw new InvalidParameterValueException("Invalid system type",
                        sysType);
            }
        } else {
            this.sysType = null;
        }
    }

    /**
     * Set the message routing Ton for this transmitter
     * 
     * @param addrTon
     *            The new Type Of Number to use
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the TON fails version validation.
     */
    public void setAddressTon(int addrTon)
            throws InvalidParameterValueException {
        this.addrTon = addrTon;
    }

    /**
     * Set the message routing Npi for this transmitter
     * 
     * @param addrNpi
     *            The new Numbering plan indicator to use
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the NPI fails version validation.
     */
    public void setAddressNpi(int addrNpi)
            throws InvalidParameterValueException {
        this.addrNpi = addrNpi;
    }

    /**
     * Set the message routing address range for this transmitter
     * 
     * @param addressRange
     *            The new address range to use (Up to 40 characters)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the address range fails version validation.
     */
    public void setAddressRange(String addressRange)
            throws InvalidParameterValueException {
        if (addressRange != null) {
            if (version.validateAddressRange(addressRange)) {
                this.addressRange = addressRange;
            } else {
                throw new InvalidParameterValueException(
                        "Invalid address range", addressRange);
            }
        } else {
            this.addressRange = null;
        }
    }

    /** Get the system Id */
    public String getSystemId() {
        return sysId;
    }

    /** Get the authentication password */
    public String getPassword() {
        return password;
    }

    /** Get the current system type */
    public String getSystemType() {
        return sysType;
    }

    /** Get the routing address regular expression */
    public String getAddressRange() {
        return addressRange;
    }

    /** Get the Type of number */
    public int getAddressTon() {
        return addrTon;
    }

    /** Get the Numbering plan indicator */
    public int getAddressNpi() {
        return addrNpi;
    }

    /** Get the interface version */
    public int getInterfaceVersion() {
        return version.getVersionID();
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind");
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }

    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                sysId,
                password,
                sysType,
                Integer.valueOf(version.getVersionID()),
                Integer.valueOf(addrTon),
                Integer.valueOf(addrNpi),
                addressRange,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        sysId = (String) params.get(0);
        password = (String) params.get(1);
        sysType = (String) params.get(2);
        version = SMPPVersion.getVersion(((Number) params.get(3)).intValue());
        addrTon = ((Number) params.get(4)).intValue();
        addrNpi = ((Number) params.get(5)).intValue();
        addressRange = (String) params.get(6);
    }
}
