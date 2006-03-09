package ie.omk.smpp.util;

/**
 * Constants representing the SMPP message status. These are the possible
 * values that can be returned in the status field of the SMPP packet header.
 * This set of codes is not definitive, as the SMPP specification defines
 * status ranges for SMPP extension status codes, as well as for vendor-specific
 * codes.
 * 
 * @version $Id:$
 */
public final class PacketStatus {
    public static final int OK = 0;
    public static final int INVALID_MESSAGE_LEN = 1;
    public static final int INVALID_COMMAND_LEN = 2;
    public static final int INVALID_COMMAND_ID = 3;
    public static final int INVALID_BIND_STATUS = 4;
    public static final int ALREADY_BOUND = 5;
    public static final int INVALID_PRIORITY_FLAG = 6;
    public static final int INVALID_REGISTERED_DELIVERY_FLAG = 7;
    public static final int SYSTEM_ERROR = 8;
    public static final int INVALID_SOURCE_ADDRESS = 0xa;
    public static final int INVALID_DEST_ADDRESS = 0xb;
    public static final int INVALID_MESSAGE_ID = 0xc;
    public static final int BIND_FAILED = 0xd;
    public static final int INVALID_PASSWORD = 0xe;
    public static final int INVALID_SYSTEM_ID = 0xf;
    public static final int CANCEL_SM_FAILED = 0x11;
    public static final int REPLACE_SM_FAILED = 0x13;
    public static final int MESSAGE_QUEUE_FULL = 0x14;
    public static final int INVALID_SERVICE_TYPE = 0x15;
    public static final int INVALID_NUMBER_OF_DESTINATIONS = 0x33;
    public static final int INVALID_DISTRIBUTION_LIST = 0x34;
    public static final int INVALID_DESTINATION_FLAG = 0x40;
    public static final int INVALID_SUBMIT_WITH_REPLACE = 0x42;
    public static final int INVALID_ESM_CLASS = 0x43;
    public static final int SUBMIT_TO_DISTRIBUTION_LIST_FAILED = 0x44;
    public static final int SUBMIT_FAILED = 0x45;
    public static final int INVALID_SOURCE_TON = 0x48;
    public static final int INVALID_SOURCE_NPI = 0x49;
    public static final int INVALID_DESTINATION_TON = 0x50;
    public static final int INVALID_DESTINATION_NPI = 0x51;
    public static final int INVALID_SYSTEM_TYPE = 0x53;
    public static final int INVALID_REPLACE_IF_PRESENT_FLAG = 0x54;
    public static final int INVALID_NUMBER_OF_MESSAGES = 0x55;
    public static final int THROTTLING_ERROR = 0x58;
    public static final int INVALID_SCHEDULED_DELIVERY_TIME = 0x61;
    public static final int INVALID_EXPIRY_TIME = 0x62;
    public static final int INVALID_PREDEFINED_MESSAGE = 0x63;
    public static final int RECEIVER_TEMPORARY_ERROR = 0x64;
    public static final int RECEIVER_PERMANENT_ERROR = 0x65;
    public static final int RECEIVER_REJECT_MESSAGE = 0x66;
    public static final int QUERY_SM_FAILED = 0x67;
    public static final int INVALID_OPTIONAL_PARAMETERS = 0xc0;
    public static final int OPTIONAL_PARAMETER_NOT_ALLOWED = 0xc1;
    public static final int INVALID_PARAMETER_LENGTH = 0xc2;
    public static final int MISSING_EXPECTED_PARAMETER = 0xc3;
    public static final int INVALID_PARAMETER_VALUE = 0xc4;
    public static final int DELIVERY_FAILED = 0xfe;
    
    private PacketStatus() {
    }
}
