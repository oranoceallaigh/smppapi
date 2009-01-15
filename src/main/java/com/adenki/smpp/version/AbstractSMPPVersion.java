package com.adenki.smpp.version;

import com.adenki.smpp.Address;


/**
 * Class representing an SMPP protocol version. Instances of this object are
 * used by the rest of the API to determine is an SMPP message is supported by a
 * certain version of the protocol.
 * @version $Id$
 */
public abstract class AbstractSMPPVersion implements SMPPVersion {
    private static final long serialVersionUID = 2L;
    /**
     * Integer representing this version number. The SMPP specification states
     * integer values that represent protocol revisions. These values are used
     * mainly in the bind_* and bind response messages. Integer value 0x33
     * represents version 3.3 of the protocol, integer value 0x34 represents
     * version 3.4...it's assumed further major and minor revisions of the SMPP
     * specification will continue this numbering scheme.
     */
    private int versionID;

    /**
     * Descriptive text for this protocol version. This value is used only to
     * return a representative string from toString.
     */
    private String versionString;

    /**
     * Create a new SMPPVersion object.
     */
    protected AbstractSMPPVersion(int versionID, String versionString) {
        this.versionID = versionID;
        this.versionString = versionString;
    }

    public int getVersionID() {
        return versionID;
    }

    public boolean isOlderThan(SMPPVersion otherVersion) {
        return versionID < otherVersion.getVersionID();
    }

    public boolean isNewerThan(SMPPVersion otherVersion) {
        return versionID > otherVersion.getVersionID();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof SMPPVersion)) {
            return false;
        } else {
            return ((SMPPVersion) obj).getVersionID() == versionID;
        }
    }

    public int hashCode() {
        return new Integer(versionID).hashCode();
    }
    
    public boolean equals(int versionID) {
        return versionID == this.versionID;
    }

    public String toString() {
        return versionString;
    }

    public void validateTon(int ton) {
        if (ton < 0 || ton > 0xff) {
            throw new VersionException("Invalid TON: " + ton);
        }
    }
    
    public void validateNpi(int npi) {
        if (npi < 0 || npi > 0xff) {
            throw new VersionException("Invalid NPI: " + npi);
        }
    }
    
    public void validateAddress(Address address) {
        if (address != null) {
            validateTon(address.getTON());
            validateNpi(address.getNPI());
            String addr = address.getAddress();
            if (addr != null && addr.length() > 20) {
                throw new VersionException("Address is too long, 20 max: " + addr);
            }
        }
    }

    public void validateEsmClass(int esmClass) {
        if (esmClass < 0 || esmClass > 0xff) {
            throw new VersionException("Invalid ESM class: " + esmClass);
        }
    }

    public void validateProtocolID(int protocolId) {
        if (protocolId < 0 || protocolId > 0xff) {
            throw new VersionException("Invalid protocol ID: " + protocolId);
        }
    }

    public void validateDataCoding(int dataCoding) {
        if (dataCoding < 0 || dataCoding > 0xff) {
            throw new VersionException("Invalid data coding: " + dataCoding);
        }
    }

    public void validateDefaultMsg(int defaultMsgId) {
        if (defaultMsgId < 0 || defaultMsgId > 0xff) {
            throw new VersionException(
                    "Invalid default message ID: " + defaultMsgId);
        }
    }

    public void validateServiceType(String serviceType) {
        if (serviceType != null && serviceType.length() > 5) {
            throw new VersionException("Invalid service type: " + serviceType);
        }
    }

    public void validateMessageState(int messageState) {
        if (messageState < 0 || messageState > 0xff) {
            throw new VersionException(
                    "Invalid message state: " + messageState);
        }
    }

    public void validateErrorCode(int code) {
        if (code < 0 || code > 0xff) {
            throw new VersionException("Invalid error code: " + code);
        }
    }

    public void validateReplaceIfPresent(int flag) {
        if (flag < 0 || flag > 1) {
            throw new VersionException(
                    "Replace-if-present flag must be 0 or 1: " + flag);
        }
    }

    public void validateNumUnsuccessful(int num) {
        if (num < 0 || num > 0xff) {
            throw new VersionException(
                    "Invalid number of unsuccessful destinations: " + num);
        }
    }

    public void validateDistListName(String name) {
        if (name != null && name.length() > 20) {
            throw new VersionException(
                    "Distribution list name too long: " + name);
        }
    }

    public void validateSystemId(String sysId) {
        if (sysId != null && sysId.length() > 15) {
            throw new VersionException("System ID too long: " + sysId);
        }
    }

    public void validatePassword(String password) {
        if (password != null && password.length() > 8) {
            throw new VersionException("Password too long");
        }
    }

    public void validateSystemType(String sysType) {
        if (sysType != null && sysType.length() > 12) {
            throw new VersionException("System type too long: " + sysType);
        }
    }

    public void validateAddressRange(String addressRange) {
        // Possibly add some checks for allowed characters??
        if (addressRange != null && addressRange.length() > 40) {
            throw new VersionException(
                    "Address range too long: " + addressRange);
        }
    }

    public void validateParamName(String paramName) {
        throw new VersionException("Parameter retrieval is not supported.");
    }

    public void validateParamValue(String paramValue) {
        throw new VersionException("Parameter retrieval is not supported.");
    }
}
