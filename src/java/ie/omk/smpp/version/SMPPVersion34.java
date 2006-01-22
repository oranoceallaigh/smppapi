package ie.omk.smpp.version;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.MessageEncoding;

public class SMPPVersion34 extends SMPPVersion {

    private static final int MAX_MSG_LENGTH = 254;

    SMPPVersion34() {
        super(0x34, "SMPP version 3.4");
    }

    public boolean isSupported(int commandID) {
        // Turn off the msb, which is used to signify a response packet..
        switch (commandID & 0x7fffffff) {
        case SMPPPacket.QUERY_LAST_MSGS:
        case SMPPPacket.QUERY_MSG_DETAILS:
        case SMPPPacket.PARAM_RETRIEVE:
            return false;

        default:
            return true;
        }
    }

    public boolean isSupportOptionalParams() {
        return true;
    }

    public int getMaxLength(int field) {
        switch (field) {
        case MESSAGE_PAYLOAD:
            return 254;

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
        return id.length() <= 64;
    }

    public boolean validateMessageState(int st) {
        return st >= 0 && st <= 0xff;
    }

    public boolean validateErrorCode(int code) {
        return code >= 0 && code <= 0xff;
    }

    public boolean validatePriorityFlag(int flag) {
        return flag >= 0 && flag <= 3;
    }

    public boolean validateRegisteredDelivery(int flag) {
        // Registered delivery flag is split up into various bits for the
        // purpose of SMPP version 3.4. However, when taken in all their
        // permutations, the allowed values of this flag range from zero up to
        // 0x1f. So the following check is valid..
        return flag >= 0 && flag <= 0x1f;
    }

    public boolean validateReplaceIfPresent(int flag) {
        return flag == 0 || flag == 1;
    }

    public boolean validateNumberOfDests(int num) {
        return num >= 0 && num <= 254;
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
        // This is unsupported in 3.4
        return false;
    }

    public boolean validateParamValue(String paramValue) {
        // This is unsupported in 3.4
        return false;
    }
}

