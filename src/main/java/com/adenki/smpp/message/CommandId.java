package com.adenki.smpp.message;

/**
 * Enumeration of command IDs.
 * <p>
 * A proper Java <code>enum</code> was intentionally not used as it added
 * an extra level of complexity that was not desirable. For a parsed packet,
 * the command ID of the packet would have to have been converted back into
 * a CommandId object necessitating either a scan of all the enumeration
 * values to find the correct object or a <code>Map</code> lookup. In either
 * case, it added no value to the API.
 * </p>
 * @version $Id$
 * @since 0.4.0
 */
public class CommandId {
    public static final int GENERIC_NACK = 0x80000000;
    public static final int BIND_RECEIVER = 0x00000001;
    public static final int BIND_RECEIVER_RESP = 0x80000001;
    public static final int BIND_TRANSMITTER = 0x00000002;
    public static final int BIND_TRANSMITTER_RESP = 0x80000002;
    public static final int QUERY_SM = 0x00000003;
    public static final int QUERY_SM_RESP = 0x80000003;
    public static final int SUBMIT_SM = 0x00000004;
    public static final int SUBMIT_SM_RESP = 0x80000004;
    public static final int DELIVER_SM = 0x00000005;
    public static final int DELIVER_SM_RESP = 0x80000005;
    public static final int UNBIND = 0x00000006;
    public static final int UNBIND_RESP = 0x80000006;
    public static final int REPLACE_SM = 0x00000007;
    public static final int REPLACE_SM_RESP = 0x80000007;
    public static final int CANCEL_SM = 0x00000008;
    public static final int CANCEL_SM_RESP = 0x80000008;
    public static final int BIND_TRANSCEIVER = 0x00000009;
    public static final int BIND_TRANSCEIVER_RESP = 0x80000009;
    public static final int OUTBIND = 0x0000000b;
    public static final int ENQUIRE_LINK = 0x00000015;
    public static final int ENQUIRE_LINK_RESP = 0x80000015;
    public static final int SUBMIT_MULTI = 0x00000021;
    public static final int SUBMIT_MULTI_RESP = 0x80000021;
    public static final int PARAM_RETRIEVE = 0x00000022;
    public static final int PARAM_RETRIEVE_RESP = 0x80000022;
    public static final int QUERY_LAST_MSGS = 0x00000023;
    public static final int QUERY_LAST_MSGS_RESP = 0x80000023;
    public static final int QUERY_MSG_DETAILS = 0x00000024;
    public static final int QUERY_MSG_DETAILS_RESP = 0x80000024;
    public static final int ALERT_NOTIFICATION = 0x00000102;
    public static final int DATA_SM = 0x00000103;
    public static final int DATA_SM_RESP = 0x80000103;
    public static final int BROADCAST_SM = 0x00000111;
    public static final int BROADCAST_SM_RESP = 0x80000111;
    public static final int QUERY_BROADCAST_SM = 0x00000112;
    public static final int QUERY_BROADCAST_SM_RESP = 0x80000112;
    public static final int CANCEL_BROADCAST_SM = 0x00000113;
    public static final int CANCEL_BROADCAST_SM_RESP = 0x80000113;
}
