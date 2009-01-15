package com.adenki.smpp.version;

import java.io.Serializable;

import com.adenki.smpp.Address;

/**
 * Representation of an SMPP version.
 * @version $Id$
 */
public interface SMPPVersion extends Serializable {
    /**
     * SMPP version 3.3.
     */
    SMPPVersion VERSION_3_3 = new SMPPVersion33();
    
    /**
     * SMPP version 3.4.
     */
    SMPPVersion VERSION_3_4 = new SMPPVersion34();
    
    /**
     * SMPP version 5.0.
     */
    SMPPVersion VERSION_5_0 = new SMPPVersion50();
    
    /**
     * Get an integer value representing this SMPP version. At present, the
     * SMPP specification uses a hex representation to identify versions;
     * version 3.3 is represented by <code>0x33</code>, version 3.4 is
     * <code>0x34</code>.
     * @return An integer value that represents this integer version.
     */
    int getVersionID();
    
    /**
     * Determine if this SMPP version is older than another version.
     * @param otherVersion The version to test against.
     * @return <code>true</code> if this version is older than
     * <code>otherVersion</code>, <code>false</code> if it
     * newer than it.
     */
    boolean isOlderThan(SMPPVersion otherVersion);
    
    /**
     * Determine if this SMPP version is equal to or newer than another
     * version.
     * @param otherVersion The version to test against.
     * @return <code>true</code> if this version is newer than
     * <code>otherVersion</code>, <code>false</code> if it is equal to or
     * older than it.
     */
    boolean isNewerThan(SMPPVersion otherVersion);
    
    /**
     * Determine if the specified <code>versionID</code> matches this
     * version&apos;s ID.
     * @param versionID The version ID to test against.
     * @return <code>true</code> if this version&apos;s ID matches
     * <code>versionID</code>.
     */
    boolean equals(int versionID);
    
    /**
     * Get the maximum allowed length for a specified field.
     * @param mandatoryParameter The enumerated field identifier to get the
     * maximum length for.
     * @return The maximum length of the specified field.
     */
    int getMaxLength(MandatoryParameter mandatoryParameter);
    
    /**
     * Determine if this SMPP version supports the specified command.
     * @param commandId The command ID of the packet.
     * @return <code>true</code> if the command is supported, <code>false
     * </code> otherwise.
     */
    boolean isSupported(int commandId);
    
    /**
     * Determine if this SMPP version supports TLVs. This will
     * be false for SMPP version 3.3 and true for versions 3.4 and later.
     * @return <code>true</code> if this version supports TLV parameters,
     * <code>false</code> otherwise.
     */
    boolean isSupportTLV();

    void validateAddress(Address address);
    void validateTon(int ton);
    void validateNpi(int npi);
    void validateAddressRange(String addressRange);
    void validateEsmClass(int c);
    void validateProtocolID(int id);
    void validateDataCoding(int dc);
    void validateDefaultMsg(int id);
    void validateMessage(byte[] message, int start, int length);
    void validateServiceType(String type);
    void validateMessageId(String id);
    void validateMessageState(int state);
    void validateErrorCode(int code);
    void validatePriorityFlag(int flag);
    void validateRegisteredDelivery(int flag);
    void validateReplaceIfPresent(int flag);
    void validateNumberOfDests(int num);
    void validateNumUnsuccessful(int num);
    void validateDistListName(String name);
    void validateSystemId(String sysId);
    void validatePassword(String password);
    void validateSystemType(String sysType);
    void validateParamName(String paramName);
    void validateParamValue(String paramValue);
}
