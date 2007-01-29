package ie.omk.smpp.message;

import ie.omk.smpp.SMPPRuntimeException;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.version.SMPPVersion;
import ie.omk.smpp.version.VersionException;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the abstract class that all SMPP messages are inherited from.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class SMPPPacket {
    private static final String INCORRECT_PARAM_COUNT =
        "Number of mandatory parameters supplied does not match the body descriptor!";

    /** Command Id: Negative Acknowledgement */
    public static final int GENERIC_NACK = 0x80000000;

    /** Command Id: Bind Receiver */
    public static final int BIND_RECEIVER = 0x00000001;

    /** Command Id: Bind Receiver Response */
    public static final int BIND_RECEIVER_RESP = 0x80000001;

    /** Command Id: Bind transmitter */
    public static final int BIND_TRANSMITTER = 0x00000002;

    /** Command Id: Bind transmitter response */
    public static final int BIND_TRANSMITTER_RESP = 0x80000002;

    /** Command Id: Query message */
    public static final int QUERY_SM = 0x00000003;

    /** Command Id: Query message response */
    public static final int QUERY_SM_RESP = 0x80000003;

    /** Command Id: Submit message */
    public static final int SUBMIT_SM = 0x00000004;

    /** Command Id: Submit message response */
    public static final int SUBMIT_SM_RESP = 0x80000004;

    /** Command Id: Deliver Short message */
    public static final int DELIVER_SM = 0x00000005;

    /** Command Id: Deliver message response */
    public static final int DELIVER_SM_RESP = 0x80000005;

    /** Command Id: Unbind */
    public static final int UNBIND = 0x00000006;

    /** Command Id: Unbind response */
    public static final int UNBIND_RESP = 0x80000006;

    /** Command Id: Replace message */
    public static final int REPLACE_SM = 0x00000007;

    /** Command Id: replace message response */
    public static final int REPLACE_SM_RESP = 0x80000007;

    /** Command Id: Cancel message */
    public static final int CANCEL_SM = 0x00000008;

    /** Command Id: Cancel message response */
    public static final int CANCEL_SM_RESP = 0x80000008;

    /** Command Id: Bind transceiver */
    public static final int BIND_TRANSCEIVER = 0x00000009;

    /** Command Id: Bind transceiever response. */
    public static final int BIND_TRANSCEIVER_RESP = 0x80000009;

    /** Command Id: Outbind. */
    public static final int OUTBIND = 0x0000000b;

    /** Command Id: Enquire Link */
    public static final int ENQUIRE_LINK = 0x00000015;

    /** Command Id: Enquire link respinse */
    public static final int ENQUIRE_LINK_RESP = 0x80000015;

    /** Command Id: Submit multiple messages */
    public static final int SUBMIT_MULTI = 0x00000021;

    /** Command Id: Submit multi response */
    public static final int SUBMIT_MULTI_RESP = 0x80000021;

    /** Command Id: Parameter retrieve */
    public static final int PARAM_RETRIEVE = 0x00000022;

    /** Command Id: Paramater retrieve response */
    public static final int PARAM_RETRIEVE_RESP = 0x80000022;

    /** Command Id: Query last messages */
    public static final int QUERY_LAST_MSGS = 0x00000023;

    /** Command Id: Query last messages response */
    public static final int QUERY_LAST_MSGS_RESP = 0x80000023;

    /** Command Id: Query message details */
    public static final int QUERY_MSG_DETAILS = 0x00000024;

    /** Command Id: Query message details response */
    public static final int QUERY_MSG_DETAILS_RESP = 0x80000024;

    /** Command Id: alert notification. */
    public static final int ALERT_NOTIFICATION = 0x00000102;

    /** Command Id: Data message. */
    public static final int DATA_SM = 0x00000103;

    /** Command Id: Data message response. */
    public static final int DATA_SM_RESP = 0x80000103;

    /** Message state at Smsc: En route */
    public static final int SM_STATE_EN_ROUTE = 1;

    /** Message state at Smsc: Delivered (final) */
    public static final int SM_STATE_DELIVERED = 2;

    /** Message state at Smsc: Expired (final) */
    public static final int SM_STATE_EXPIRED = 3;

    /** Message state at Smsc: Deleted (final) */
    public static final int SM_STATE_DELETED = 4;

    /** Message state at Smsc: Undeliverable (final) */
    public static final int SM_STATE_UNDELIVERABLE = 5;

    /** Message state at Smsc: Accepted */
    public static final int SM_STATE_ACCEPTED = 6;

    /** Message state at Smsc: Invalid message (final) */
    public static final int SM_STATE_INVALID = 7;

    /** Esm class: Mobile Terminated; Normal delivery, no address swapping */
    public static final int SMC_MT = 1;

    /** Esm class: Mobile originated */
    public static final int SMC_MO = 2;

    /** Esm class: Mobile Originated / Terminated */
    public static final int SMC_MOMT = 3;

    /** Esm class: Delivery receipt, no address swapping */
    public static final int SMC_RECEIPT = 4;

    /** Esm class: Predefined message */
    public static final int SMC_DEFMSG = 8;

    /** Esm class: Normal delivery , address swapping on */
    public static final int SMC_LOOPBACK_RECEIPT = 16;

    /** Esm class: Delivery receipt, address swapping on */
    public static final int SMC_RECEIPT_SWAP = 20;

    /** Esm class: Store message, do not send to Kernel */
    public static final int SMC_STORE = 32;

    /** Esm class: Store message and send to kernel */
    public static final int SMC_STORE_FORWARD = 36;

    /** Esm class: Distribution submission */
    public static final int SMC_DLIST = 64;

    /** Esm class: Multiple recipient submission */
    public static final int SMC_MULTI = 128;

    /** Esm class: Distribution list and multiple recipient submission */
    public static final int SMC_CAS_DL = 256;

    /** Esm class: Escalated message FFU */
    public static final int SMC_ESCALATED = 512;

    /** Esm class: Submit with replace message */
    public static final int SMC_SUBMIT_REPLACE = 1024;

    /** Esm class: Memory capacity error */
    public static final int SMC_MCE = 2048;

    /** Esme error code: No error */
    public static final int ESME_ROK = 0;

    /** Command ID. */
    protected int commandId;

    /** Command status. */
    protected int commandStatus;

    /** Packet sequence number. */
    protected long sequenceNum = -1;

    /** Optional parameter table. */
    protected TLVTable tlvTable = new TLVTable();

    /**
     * Create a new SMPPPacket with specified Id.
     * 
     * @param commandId
     *            Command Id value
     */
    protected SMPPPacket(int commandId) {
        this.commandId = commandId;
    }

    /**
     * Create a new SMPPPacket that represents a response to the specified
     * packet. This constructor will duplicate the sequence number and version
     * from the request packet, and set the command id to the appropriate
     * value representing the response packet.
     * @param request
     */
    protected SMPPPacket(SMPPPacket request) {
        commandId = request.commandId | 0x80000000;
        sequenceNum = request.sequenceNum;
    }
    
    /**
     * Is this command a request packet.
     * @return <code>true</code> if this packet is an SMPP request, <code>
     * false</code> if it is a response.
     */
    public boolean isRequest() {
        return (commandId & 0x80000000) == 0;
    }

    /**
     * Is this command a response packet.
     * @return <code>true</code> if this packet is an SMPP response, <code>
     * false</code> if it is a request.
     */
    public boolean isResponse() {
        return (commandId & 0x80000000) != 0;
    }
    
    /**
     * Get the Command Id of this SMPP packet.
     * 
     * @return The Command Id of this packet
     */
    public int getCommandId() {
        return commandId;
    }

    /**
     * Get the status of this packet.
     * 
     * @return The error status of this packet (only relevent to Response
     *         packets)
     */
    public int getCommandStatus() {
        return commandStatus;
    }

    /**
     * Set the status of this packet.
     * @param commandStatus
     */
    public void setCommandStatus(int commandStatus) {
        this.commandStatus = commandStatus;
    }
    /**
     * Get the sequence number of this packet.
     * 
     * @return The sequence number of this SMPP packet
     */
    public long getSequenceNum() {
        return sequenceNum;
    }

    /**
     * Set the sequence number of this packet.
     */
    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    /**
     * Get the optional parameter (TLV) table.
     * 
     * @see ie.omk.smpp.message.tlv.TLVTable
     */
    public TLVTable getTLVTable() {
        return tlvTable;
    }

    /**
     * Set the optional parameter (TLV) table. This method discards the entire
     * optional paramter table and replaces it with <code>table</code>. The
     * discarded table is returned. If <code>null</code> is passed in, a new,
     * empty TLVTable object will be created.
     * 
     * @see ie.omk.smpp.message.tlv.TLVTable
     * @return the old tlvTable.
     */
    public TLVTable setTLVTable(TLVTable tlvTable) {
        TLVTable oldTable = this.tlvTable;
        if (tlvTable == null) {
            this.tlvTable = new TLVTable();
        } else {
            this.tlvTable = tlvTable;
        }
        return oldTable;
    }

    /**
     * Set an optional parameter. This is a convenience method and merely calls
     * {@link ie.omk.smpp.message.tlv.TLVTable#set}on this message's optional
     * parameter table.
     * 
     * @param tag
     *            the tag of the parameter to set.
     * @param value
     *            the value object to set.
     * @throws ie.omk.smpp.message.tlv.BadValueTypeException
     *             if the type of <code>value</code> is incorrect for the
     *             <code>tag</code>.
     * @return the previous value of the parameter, or null if it was unset.
     */
    public Object setOptionalParameter(Tag tag, Object value) {
        return tlvTable.put(tag, value);
    }

    /**
     * Remove, or un-set, an optional parameter.
     * @param tag The tag for the optional parameter to remove.
     */
    public void removeOptionalParameter(Tag tag) {
        tlvTable.remove(tag);
    }
    
    /**
     * Get an optional parameter. This is a convenience method and merely calls
     * {@link ie.omk.smpp.message.tlv.TLVTable#get}on this message's optional
     * parameter table.
     * 
     * @param tag
     *            the tag of the parameter value to get.
     */
    public Object getOptionalParameter(Tag tag) {
        return tlvTable.get(tag);
    }

    /**
     * Check if a particular optional parameter is set. This is a convenience
     * method and merely calls {@link ie.omk.smpp.message.tlv.TLVTable#isSet}on
     * this message's optional parameter table.
     * 
     * @param tag
     *            the tag of the parameter to check.
     * @return true if the parameter is set, false if it is not.
     */
    public boolean isSet(Tag tag) {
        return tlvTable.containsKey(tag);
    }

    /**
     * Get the length this packet encodes as.
     * @return The size, in bytes, of this packet as it would encode on the
     * network.
     */
    public final int getLength() {
        return 16 + getBodyLength() + tlvTable.getLength();
    }
    
    /**
     * Write the byte representation of this SMPP packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to use
     * @throws IOException
     *             if there's an error writing to the output stream.
     */
    public final void writeTo(OutputStream out) throws IOException {
        this.writeTo(out, true);
    }

    /**
     * Write the byte representation of this SMPP packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to use
     * @param withOptional
     *            true to send optional parameters over the link, false to only
     *            write the mandatory parameters.
     * @throws IOException
     *             if there's an error writing to the output stream.
     */
    public final void writeTo(OutputStream out, boolean withOptional)
            throws IOException {
        int commandLen = getLength();

        SMPPIO.writeInt(commandLen, out);
        SMPPIO.writeInt(commandId, out);
        SMPPIO.writeInt(commandStatus, out);
        SMPPIO.writeLongInt(sequenceNum, out);
        writeMandatory(out);
        if (withOptional) {
            tlvTable.writeTo(out);
        }
    }

    /**
     * Decode an SMPP packet from a byte array.
     * 
     * @param data
     *            the byte array to read the SMPP packet's fields from.
     * @param offset
     *            the offset into <code>b</code> to begin reading the packet
     *            fields from.
     * @throws ie.omk.smpp.message.SMPPProtocolException
     *             if there is an error parsing the packet fields.
     */
    public void readFrom(byte[] data, int offset) throws SMPPProtocolException {
        // Clear out the TLVTable..
        tlvTable.clear();

        if (data.length < (offset + 16)) {
            throw new SMPPProtocolException("Not enough bytes for a header: "
                    + (data.length - (offset + 16)));
        }

        int len = SMPPIO.bytesToInt(data, offset);
        int id = SMPPIO.bytesToInt(data, offset + 4);

        if (id != commandId) {
            // Command type mismatch...ye can't do that, lad!
            throw new SMPPProtocolException(
                    "The packet on the input stream is not"
                            + " the same as this packet's type.");
        }
        if (data.length < (offset + len)) {
            // not enough bytes there for me to read in, buddy!
            throw new SMPPProtocolException(
                    "Insufficient bytes available: need = " + len
                    + ", available = " + (data.length - offset));
        }
        commandStatus = SMPPIO.bytesToInt(data, offset + 8);
        sequenceNum = SMPPIO.bytesToLongInt(data, offset + 12);
        try {
            if (commandStatus == 0) {
                // Read the mandatory body parameters..
                ParsePosition pos = new ParsePosition(16 + offset);
                List<Object> mandatory = readMandatory(data, pos);
                setMandatoryParameters(mandatory);

                // Read the optional parameters..
                if (offset + len > pos.getIndex()) {
                    tlvTable.readFrom(data, pos, len);
                }
            }
        } catch (ArrayIndexOutOfBoundsException x) {
            throw new SMPPProtocolException(
                    "Ran out of bytes to read for packet body", x);
        }
    }

    /**
     * Validate this packet against an SMPP version. If any part of this
     * packet is in violation of <code>smppVersion</code>, a
     * {@link ie.omk.smpp.version.VersionException} will be thrown.
     * Examples of violations are:
     * <ul>
     * <li>The specified version does not support this packet type</li>
     * <li>A mandatory parameter field is too short or long, or specifies an
     * unsupported value</li>
     * </ul>
     * @param smppVersion The version to validate against.
     */
    public void validate(SMPPVersion smppVersion) {
        if (!smppVersion.isSupported(commandId)) {
            throw new VersionException("Unsupported command ID.");
        }
        validateMandatory(smppVersion);
    }
    
    /**
     * Return a String representation of this packet. This is provided
     * for debugging/display purposes only and is not intended to be used
     * programatically.
     * @return A string representation of this packet.
     */
    public String toString() {
        String packetName = getClass().getName();
        packetName = packetName.substring(packetName.lastIndexOf(".") + 1);
        StringBuffer buffer = new StringBuffer();
        buffer.append('[').append(packetName)
        .append(" Header:[length=").append(getLength())
        .append(",id=").append(Integer.toHexString(commandId))
        .append(",status=").append(commandStatus)
        .append(",sequenceNum=").append(sequenceNum)
        .append("] Mandatory:[");
        toString(buffer);
        buffer.append("] Optional:[")
        .append(tlvTable)
        .append("]]");
        return buffer.toString();
    }
    
    /**
     * Get the mandatory parameters in string form (for display purposes only).
     * @param buffer
     */
    protected void toString(StringBuffer buffer) {
    }

    /**
     * Validate the mandatory parameters for this packet.
     * @param smppVersion The version to validate against.
     */
    protected void validateMandatory(SMPPVersion smppVersion) {
    }

    /**
     * Set the mandatory parameters of this packet from the supplied list.
     * <code>params</code> will contain the set of objects parsed from
     * a byte array according to the packet&apos;s body descriptor.
     * @param params The mandatory parameters parsed from a byte array
     * according to the packet&apos;s body descriptor.
     */
    protected void setMandatoryParameters(List<Object> params) {
    }

    /**
     * Get this packet&apos;s body descriptor. This default implementation
     * returns <code>null</code>, implying the packet does not have any
     * mandatory parameters.
     * @return This packet&apos;s body descriptor;
     */
    protected BodyDescriptor getBodyDescriptor() {
        return null;
    }
    
    /**
     * Get the objects that make up this packet&apos;s mandatory parameters.
     * This method is called when the packet is being written out. It must
     * never return <code>null</code>. This default implementation returns
     * a zero-length array, implying the packet does not have any mandatory
     * parameters. The objects returned by this method <b>must</b> match
     * with the body descriptor.
     * @return An array of mandatory parameters.
     */
    protected Object[] getMandatoryParameters() {
        return new Object[0];
    }
    
    /**
     * Read this packet's mandatory parameters from a byte array.
     * 
     * @param data
     *            the byte array to read the mandatory parameters from.
     * @param offset
     *            the offset into b that the mandatory parameter's begin at.
     * @throws ie.omk.smpp.message.SMPPProtocolException
     *             if there is an error parsing the packet fields.
     */
    private List<Object> readMandatory(byte[] data, ParsePosition pos) throws SMPPProtocolException {
        List<Object> body = new ArrayList<Object>();
        BodyDescriptor bodyDescriptor = getBodyDescriptor();
        if (bodyDescriptor == null) {
            return body;
        }
        try {
            for (ParamDescriptor param : bodyDescriptor.getBody()) {
                int length = -1;
                int lengthSpecifier = param.getLengthSpecifier();
                if (lengthSpecifier > -1) {
                    length = getLengthFromBody(body, lengthSpecifier);
                }
                Object obj = param.readObject(data, pos, length);
                body.add(obj);
            }
        } catch (IllegalArgumentException x) {
            throw new SMPPProtocolException("Invalid values in an SMPP date", x);
        } catch (ParseException x) {
            throw new SMPPProtocolException("Could not parse an SMPP date.", x);
        }
        return body;
    }
    
    private int getLengthFromBody(List<Object> body, int lengthSpecifier)
    throws ParseException {
        int length = 0;
        try {
            length = ((Number) body.get(lengthSpecifier)).intValue();
        } catch (ArrayIndexOutOfBoundsException x) {
            throw new ParseException(
                    "Cannot read count from link index " + lengthSpecifier, 0);
        } catch (ClassCastException x) {
            throw new ParseException("Mandatory parameter at index " + lengthSpecifier
                    + " is not a java.lang.Number", 0);
        }
        return length;
    }
    
    private void writeMandatory(OutputStream out) throws IOException {
        BodyDescriptor bodyDescriptor = getBodyDescriptor();
        if (bodyDescriptor == null) {
            return;
        }
        Object[] body = getMandatoryParameters();
        if (body.length != bodyDescriptor.getSize()) {
            throw new SMPPRuntimeException(INCORRECT_PARAM_COUNT);
        }
        int index = 0;
        for (ParamDescriptor param : bodyDescriptor.getBody()) {
            param.writeObject(body[index], out);
            index++;
        }
    }
    
    private int getBodyLength() {
        int size = 0;
        BodyDescriptor bodyDescriptor = getBodyDescriptor();
        if (bodyDescriptor != null) {
            Object[] body = getMandatoryParameters();
            if (body.length != bodyDescriptor.getSize()) {
                throw new SMPPRuntimeException(INCORRECT_PARAM_COUNT);
            }
            int index = 0;
            for (ParamDescriptor param : bodyDescriptor.getBody()) {
                size += param.sizeOf(body[index]);
                index++;
            }
        }
        return size;
    }
}
