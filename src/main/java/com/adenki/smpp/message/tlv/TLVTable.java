package com.adenki.smpp.message.tlv;

import java.io.IOException;
import java.util.BitSet;
import java.util.Map;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Map of tag/length/value (TLV) parameters.
 * <p>
 * TLV stands for Tag/Length/Value and was a capability added to SMPP version
 * 3.4. It is an extensible means of adding new parameter types to SMPP packets.
 * Each optional parameter has a 2-byte tag, which is a unique identifier of
 * that parameter, a 2-byte length, which is an integer value representing the
 * length of the value of the parameter and a value. The value may be of various
 * types including integers, C Strings, octet strings, bit masks etc. The tag
 * defines the type of the value.
 * </p>
 * <p>
 * TLVs were originally called "optional parameters". SMPP v5 altered this
 * since it introduced the concept of required TLV parameters.
 * </p>
 * <p>
 * This class holds a mapping of tags to values. Each SMPP packet holds a TLV
 * table which holds that packet's set of current optional parameters. Upon
 * serializing the packet to an output stream or byte array, the format of the
 * serialized packet is:
 * </p>
 * 
 * <pre>
 * 
 *    +-------------------------+
 *    | SMPP Packet             |
 *    | +----------------------+|
 *    | | SMPP Header          ||
 *    | +----------------------+|
 *    | |                      ||
 *    | |                      ||
 *    | | Mandatory parameters ||
 *    | |                      ||
 *    | |                      ||
 *    | +----------------------+|
 *    | | Optional parameters  ||
 *    | | +------------------+ ||
 *    | | | Tag/Length/Value | ||
 *    | | +------------------+ ||
 *    | | |     ...          | ||
 *    | | +------------------+ ||
 *    | +----------------------+|
 *    +-------------------------+
 *  
 * </pre>
 * 
 * @version $Id$
 */
public interface TLVTable extends Map<Tag, Object> {
    // TODO docs
    void readFrom(PacketDecoder decoder, int length);
    void writeTo(PacketEncoder encoder) throws IOException;

    /**
     * Get the value for a tag. This is a convenience method to convert
     * the tag integer to its appropriate Tag object and then look that
     * tag up in the map.
     * @param tag The tag&apos;s integer value.
     */
    Object get(int tag);

    /**
     * Get the tag&apos;s value as a string.
     * @param tag The tag to retrieve the value for.
     * @return The value as a string, or <code>null</code> if the specified
     * tag is not set in this table.
     */
    String getString(Tag tag);

    /**
     * Get the tag&apos;s value as an int.
     * @param tag The tag to retrieve the value for.
     * @return The value as an integer, or <code>-1</code> if the specified
     * tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a number (castable as a <code>java.lang.Number</code>).
     */
    int getInt(Tag tag);
    
    /**
     * Get the tag&apos;s value as a long.
     * @param tag The tag to retrieve the value for.
     * @return The value as a long, or <code>-1</code> if the specified
     * tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a number (castable as a <code>java.lang.Number</code>).
     */
    long getLong(Tag tag);

    /**
     * Get the tag&apos;s value as a bit set.
     * @param tag The tag to retrieve the value for.
     * @return The value, cast as a <code>java.util.BitSet</code>, or
     * <code>null</code> if the specified tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a bit mask.
     */
    BitSet getBitmask(Tag tag);

    /**
     * Get the tag&apos;s value as a byte array.
     * @param tag The tag to retrieve the value for.
     * @return The value, cast as a <code>byte[]</code>, or <code>null</code>
     * if the specified tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a byte array.
     */
    byte[] getBytes(Tag tag);
    
    Object put(Tag tag, char value);
    
    Object put(Tag tag, short value);
    
    Object put(Tag tag, int value);
    
    Object put(Tag tag, long value);
    
    /**
     * Remove (or un-set) a tag/value from this table.
     * @param tag The tag to remove from the table.
     */
    void remove(int tag);
    
    /**
     * Get the length the parameters in the table would encode as. The length of
     * an SMPP packet is determined by: <br>
     * <code>sizeof (smpp_header) + sizeof (mandatory_parameters)
     * + sizeof (optional_parameters).</code>
     * <br>
     * The value returned for this method is the last clause in this equation.
     * 
     * @return The full length that the optional parameters would encode as.
     */
    int getLength();
}
