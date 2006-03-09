package ie.omk.smpp.version;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.MessageEncoding;

public class SMPPVersion33 extends SMPPVersion {

    /** Maximum number of bytes in an smppv3.3 message. */
    private static final int MAX_MSG_LENGTH = 140;

    SMPPVersion33() {
        super(0x33, "SMPP version 3.3");
    }

    public boolean isSupported(int commandID) {
        // Turn off the msb, which is used to signify a response packet..
        switch (commandID & 0x7fffffff) {
        case SMPPPacket.BIND_TRANSCEIVER:
        case SMPPPacket.DATA_SM:
        case SMPPPacket.ALERT_NOTIFICATION:
            return false;

        default:
            return true;
        }
    }

    public boolean isSupportOptionalParams() {
        return false;
    }

    public int getMaxLength(int field) {
        switch (field) {
        case MESSAGE_PAYLOAD:
            return 140;

        default:
            return Integer.MAX_VALUE;
        }
    }

    public boolean validateAddress(Address s) {
        int ton = s.getTON();
        int npi = s.getNPI();
        boolean tonValid = ton >= 0 && ton <= 0xff;
        boolean npiValid = npi >= 0 && npi <= 0xff;
        boolean addressValid = s.getAddress().length() <= 20;
        return tonValid && npiValid && addressValid;
    }

    public boolean validateEsmClass(int c) {
        return c >= 0 && c <= 0xff;
    }

    public boolean validateProtocolID(int id) {
        return id >= 0 && id <= 0xff;
    }

    public boolean validateDataCoding(int dc) {
        return dc >= 0 && dc <= 0xff;
    }

    public boolean validateDefaultMsg(int id) {
        return id >= 0 && id <= 0xff;
    }

    public boolean validateMessageText(String text, AlphabetEncoding alphabet) {
        if (text != null) {
            return alphabet.encodeString(text).length <= MAX_MSG_LENGTH;
        } else {
            return true;
        }
    }

    public boolean validateMessage(byte[] message, MessageEncoding encoding) {
        if (message != null) {
            return message.length <= MAX_MSG_LENGTH;
        } else {
            return true;
        }
    }

    public boolean validateServiceType(String type) {
        return type.length() <= 5;
    }

    public boolean validateMessageId(String id) {
        try {
            // Message IDs must be valid Hex numbers in v3.3
            Long.parseLong(id, 16);
            return id.length() <= 8;
        } catch (NumberFormatException x) {
            return false;
        }
    }

    public boolean validateMessageState(int st) {
        return st >= 0 && st <= 0xff;
    }

    public boolean validateErrorCode(int code) {
        return code >= 0 && code <= 0xff;
    }

    public boolean validatePriorityFlag(int flag) {
        return flag == 0 || flag == 1;
    }

    public boolean validateRegisteredDelivery(int flag) {
        return flag == 0 || flag == 1;
    }

    public boolean validateReplaceIfPresent(int flag) {
        return flag == 0 || flag == 1;
    }

    public boolean validateNumberOfDests(int num) {
        return num >= 0 && num <= 255;
    }

    public boolean validateNumUnsuccessful(int num) {
        return num >= 0 && num <= 255;
    }

    public boolean validateDistListName(String name) {
        return name.length() <= 20;
    }

    public boolean validateSystemId(String sysId) {
        return sysId.length() <= 15;
    }

    public boolean validatePassword(String password) {
        return password.length() <= 8;
    }

    public boolean validateSystemType(String sysType) {
        return sysType.length() <= 12;
    }

    public boolean validateAddressRange(String addressRange) {
        // Possibly add some checks for allowed characters??
        return addressRange.length() <= 40;
    }

    public boolean validateParamName(String paramName) {
        return paramName.length() <= 31;
    }

    public boolean validateParamValue(String paramValue) {
        return paramValue.length() <= 100;
    }
}

