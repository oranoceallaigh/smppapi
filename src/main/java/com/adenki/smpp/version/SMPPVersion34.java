package com.adenki.smpp.version;

import com.adenki.smpp.message.CommandId;

public class SMPPVersion34 extends AbstractSMPPVersion {
    private static final long serialVersionUID = 2L;
    private static final int MAX_MSG_LENGTH = 254;

    SMPPVersion34() {
        super(0x34, "SMPP version 3.4");
    }

    public boolean isSupported(int commandID) {
        // Turn off the msb, which is used to signify a response packet..
        switch (commandID & 0x7fffffff) {
        case CommandId.ALERT_NOTIFICATION:
        case CommandId.BIND_RECEIVER:
        case CommandId.BIND_TRANSCEIVER:
        case CommandId.BIND_TRANSMITTER:
        case CommandId.CANCEL_SM:
        case CommandId.DATA_SM:
        case CommandId.DELIVER_SM:
        case CommandId.ENQUIRE_LINK:
        case CommandId.GENERIC_NACK:
        case CommandId.OUTBIND:
        case CommandId.QUERY_SM:
        case CommandId.REPLACE_SM:
        case CommandId.SUBMIT_MULTI:
        case CommandId.SUBMIT_SM:
        case CommandId.UNBIND:
            return true;

        default:
            return false;
        }
    }

    public boolean isSupportTLV() {
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
