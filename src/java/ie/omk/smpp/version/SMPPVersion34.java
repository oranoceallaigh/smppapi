package ie.omk.smpp.version;

import ie.omk.smpp.message.SMPPPacket;

public class SMPPVersion34 extends AbstractSMPPVersion {
    private static final long serialVersionUID = 1;
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

    public int getMaxLength(MandatoryParameter mandatoryParameter) {
        switch (mandatoryParameter) {
        case SHORT_MESSAGE:
            return MAX_MSG_LENGTH;

        default:
            return Integer.MAX_VALUE;
        }
    }

    public void validateMessage(byte[] message, int start, int length) {
        if (message != null && length > MAX_MSG_LENGTH) {
            throw new VersionException("Message is too long: " + length);
        }
    }

    public void validateMessageId(String messageId) {
        if (messageId != null && messageId.length() > 64) {
            throw new VersionException("Invalid message ID: " + messageId);
        }
    }

    public void validatePriorityFlag(int priority) {
        if (priority < 0 || priority > 3) {
            throw new VersionException(
                    "Invalid message priority: " + priority);
        }
    }

    public void validateRegisteredDelivery(int registeredDelivery) {
        // Registered delivery flag is split up into various bits for the
        // purpose of SMPP version 3.4. However, when taken in all their
        // permutations, the allowed values of this flag range from zero up to
        // 0x1f. So the following check is valid..
        if (registeredDelivery < 0 || registeredDelivery > 0x1f) {
            throw new VersionException(
                    "Invalid registered delivery: " + registeredDelivery);
        }
    }

    public void validateNumberOfDests(int num) {
        if (num < 0 || num > 254) {
            throw new VersionException(
                    "Invalid number of destinations: " + num);
        }
    }
}
