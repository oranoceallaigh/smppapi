package com.adenki.smpp.message;

import java.io.IOException;
import java.io.Serializable;

import com.adenki.smpp.Address;
import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.message.tlv.TLVTable;
import com.adenki.smpp.message.tlv.TLVTableImpl;
import com.adenki.smpp.message.tlv.Tag;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.SMPPDate;
import com.adenki.smpp.version.SMPPVersion;
import com.adenki.smpp.version.VersionException;

/**
 * This is the abstract class that all SMPP messages are inherited from.
 * 
 * @version $Id$
 */
public abstract class SMPPPacket implements Serializable, Cloneable {
    private static final long serialVersionUID = 2L;
    /** Command ID. */
    protected int commandId;

    /** Command status. */
    protected int commandStatus;

    /** Packet sequence number. */
    protected long sequenceNum = -1;

    /**
     * TLV table.
     */
    protected TLVTable tlvTable = new TLVTableImpl();

    /**
     * Create a new SMPPPacket with the specified Id. This version of the
     * constructor is provided as an extension point for custom packets.
     * @param commandId The command ID.
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
        return !isResponse();
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
     * @see com.adenki.smpp.message.tlv.TLVTable
     */
    public TLVTable getTLVTable() {
        return tlvTable;
    }

    /**
     * Set a TLV parameter. This is a convenience method and is equivalent
     * to <code>getTLVTable().put(tag, value)</code>.
     * 
     * @param tag The tag of the parameter to set.
     * @param value The value object to set.
     * @throws com.adenki.smpp.message.tlv.BadValueTypeException
     *             If the type of <code>value</code> is incorrect for the
     *             <code>tag</code>.
     * @return The previous value of the parameter, or null if it was unset.
     * @see TLVTable#put
     */
    public Object setTLV(Tag tag, Object value) {
        return tlvTable.put(tag, value);
    }
    
    /**
     * Get a TLV parameter. This is a convenience method and is equivalent
     * to <code>getTLVTable().get(tag)</code>.
     * @param tag the tag of the TLV parameter to get.
     */
    public Object getTLV(Tag tag) {
        return tlvTable.get(tag);
    }

    /**
     * Remove a TLV parameter. This is a convenience method and is equivalent
     * to <code>getTLVTable().remove(tag)</code>.
     * @param tag The TLV to remove.
     * @return The TLV, if it was set or <code>null</code> if it wasn't.
     */
    public Object removeTLV(Tag tag) {
        return tlvTable.remove(tag);
    }
    
    /**
     * Check if a particular TLV parameter is set. This is a convenience
     * method and is equivalent to <code>getTLVTable().containsKey(tag)
     * </code>.
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
        return 16 + getMandatorySize() + tlvTable.getLength();
    }
    
    /**
     * Write the byte representation of this SMPP packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to use
     * @throws IOException
     *             if there's an error writing to the output stream.
     */
    public void writeTo(PacketEncoder encoder) throws IOException {
        this.writeTo(encoder, true);
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
    public final void writeTo(PacketEncoder encoder, boolean withOptional)
            throws IOException {
        int commandLen = 16 + getMandatorySize();
        if (withOptional) {
            commandLen += tlvTable.getLength();
        }
        encoder.writeInt4(commandLen);
        encoder.writeInt4(commandId);
        encoder.writeInt4(commandStatus);
        encoder.writeUInt4(sequenceNum);
        writeMandatory(encoder);
        if (withOptional) {
            tlvTable.writeTo(encoder);
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
     * @throws com.adenki.smpp.message.SMPPProtocolException
     *             if there is an error parsing the packet fields.
     * @throws SMPPRuntimeException If an attempt is made to parse a different
     * type of packet than this class supports (for example, trying to use
     * a <code>BindTransmitter</code> object to parse data that contains a
     * <code>BindTransceiver</code> packet).
     */
    public void readFrom(PacketDecoder decoder) throws SMPPProtocolException {
        tlvTable.clear();
        int startPos = decoder.getParsePosition();
        int commandLen = readHeader(decoder);
        if (commandStatus == 0) {
            readMandatory(decoder);
            int tlvLength = commandLen - (decoder.getParsePosition() - startPos);
            if (tlvLength > 0) {
                tlvTable.readFrom(decoder, tlvLength);
            }
        }
    }

    /**
     * Validate this packet against an SMPP version. If any part of this
     * packet is in violation of <code>smppVersion</code>, a
     * {@link com.adenki.smpp.version.VersionException} will be thrown.
     * Examples of violations are:
     * <ul>
     * <li>The specified version does not support this packet type</li>
     * <li>A mandatory parameter field is too short or long, or specifies an
     * unsupported value</li>
     * <li>For SMPP version 5.0 or newer, if the packet does not contain
     * all required TLVs.</li>
     * </ul>
     * @param smppVersion The version to validate against.
     * @throws VersionException If the package fails validation.
     */
    public final void validate(SMPPVersion smppVersion) {
        // TODO: you need to remove the isSupported from Versioning.
        validateMandatory(smppVersion);
        if (smppVersion.isNewerThan(SMPPVersion.VERSION_5_0)) {
            if (!validateTLVTable(smppVersion)) {
                throw new VersionException("Packet does not contain all required TLVs.");
            }
        }
    }
    
    /**
     * Return a String representation of this packet. This is provided
     * for debugging/display purposes only and is not intended to be used
     * programatically.
     * @return A string representation of this packet.
     */
    public String toString() {
        String packetName = getClass().getSimpleName();
        StringBuilder buffer = new StringBuilder();
        buffer.append(packetName)
        .append("(Header:(length=").append(getLength())
        .append(",id=0x").append(Integer.toHexString(commandId))
        .append(",status=").append(commandStatus)
        .append(",sequenceNum=").append(sequenceNum)
        .append("),Mandatory:(");
        toString(buffer);
        buffer.append(") Optional:(")
        .append(tlvTable)
        .append("))");
        return buffer.toString();
    }

    /**
     * Get the encoded size of an address, which may be <code>null</code>.
     * @param address A (possibly <code>null</code>) address object.
     * @return The encoded size of the address.
     */
    public int sizeOf(Address address) {
        return address != null ? address.getLength() : 3;
    }
    
    /**
     * Get the encoded size of a date, which may be <code>null</code>.
     * @param date A (possibly <code>null</code>) date object.
     * @return The encoded size of the address.
     */
    public int sizeOf(SMPPDate date) {
        return date != null ? date.getLength() : 1;
    }
    
    /**
     * Get the encoded size of a byte array, which may be <code>null</code>.
     * @param array A (possibly <code>null</code>) byte array.
     * @return The encoded size of the array.
     */
    public int sizeOf(byte[] array) {
        return array != null ? array.length : 0;
    }
    
    /**
     * Get the encoded size of a string, which may be <code>null</code>.
     * @param string A (possibly <code>null</code>) string object.
     * @return The length of the string.
     */
    public int sizeOf(String string) {
        return string != null ? string.length() : 0;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj || getClass() != obj.getClass()) {
            return false;
        }
        SMPPPacket other = (SMPPPacket) obj;
        return commandId == other.commandId
                && commandStatus == other.commandStatus
                && sequenceNum == other.sequenceNum;
    }
    
    @Override
    public int hashCode() {
        return new Integer(commandId).hashCode()
                + new Integer(commandStatus).hashCode()
                + new Long(sequenceNum).hashCode();
    }
    
    /**
     * Utility method to compare two objects, even if one or both are
     * <code>null</code>.
     * @param obj1 The first object to compare.
     * @param obj2 The second object to compare.
     * @return <code>true</code> if the objects are equal, or if they are
     * both <code>null</code>. <code>false</code> otherwise.
     */
    protected boolean safeCompare(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }
    
    /**
     * Get the mandatory parameters in string form (for display purposes only).
     * @param buffer
     */
    protected void toString(StringBuilder buffer) {
    }

    /**
     * Validate the mandatory parameters for this packet. If any
     * mandatory parameter fails validation, a <code>
     * com.adenki.smpp.version.VersionException</code> should be
     * thrown.
     * @param smppVersion The version to validate against.
     */
    protected void validateMandatory(SMPPVersion smppVersion) {
    }

    /**
     * Validate that the TLV table contains all required parameters.
     * @param smppVersion The version to validate against. Since
     * required TLVs were only introduced in SMPP version 5.0, this
     * method will only ever be called when using a version that
     * is equivalent to or newer than that.
     * @return <code>true</code> if all required TLVs are set,
     * <code>false</code> otherwise.
     */
    protected boolean validateTLVTable(SMPPVersion smppVersion) {
        return true;
    }

    /**
     * Read the mandatory parameters from a packet decoder. This default
     * implementation is empty, parsing no mandatory parameters. Sub-classes
     * may override this as they wish.
     * @param decoder The decoder to read fields from.
     */
    protected void readMandatory(PacketDecoder decoder) {
    }
    
    /**
     * Write the mandatory parameters to a packet encoder.
     * @param encoder The encoder to write mandatory parameters to.
     * @throws IOException If an problem occurs while writing.
     */
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
    }

    /**
     * Get the encoded size of the mandatory parameters of this packet.
     * @return The number of bytes the mandatory parameters will encode to.
     */
    protected int getMandatorySize() {
        return 0;
    }
    
    /**
     * Parse the header parameters for an SMPP packet.
     * @param decoder The decoder to read fields from.
     * @return The value of the "command length" header parameter.
     * @throws SMPPProtocolException If the header information is invalid.
     * This exception might be thrown if there are not enough bytes to parse
     * a header or if the command length specifies a number of bytes that are
     * not available in the <code>data</code>.
     * @throws SMPPRuntimeException If the command ID in the data does not
     * match the command ID of the implementing class.
     */
    private int readHeader(PacketDecoder decoder) {
        if (decoder.getAvailableBytes() < 16) {
            throw new SMPPProtocolException("Not enough bytes for a header: "
                    + decoder.getAvailableBytes());
        }
        int commandLen = (int) decoder.readUInt4();
        if (commandLen < 0) {
            throw new SMPPProtocolException("Packet is too large for smppapi!");
        }
        int id = (int) decoder.readUInt4();
        if (id != commandId) {
            // Command type mismatch...ye can't do that, lad!
            throw new SMPPRuntimeException(
                    "Packet of type " + getClass().getName()
                    + " cannot parse commandId " + commandId);
        }
        commandStatus = (int) decoder.readUInt4();
        sequenceNum = (int) decoder.readUInt4();
        return commandLen;
    }
}
