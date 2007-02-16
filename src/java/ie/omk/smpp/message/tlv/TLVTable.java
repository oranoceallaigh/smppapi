package ie.omk.smpp.message.tlv;

import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Map of optional parameters (TLVs).
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
public class TLVTable extends HashMap<Tag, Object> {
    static final long serialVersionUID = 2L;

    /**
     * Create a new, empty, TLVTable.
     */
    public TLVTable() {
    }

    /**
     * Decode a full set of optional parameters from a byte array.
     * 
     * @param data
     *            The byte array to decode from.
     * @param position
     *            The array index of byte to begin parsing the parameter table
     *            from.
     * @param length
     *            The length in the byte array of all the optional parameters.
     */
    public void readFrom(byte[] data, ParsePosition position, int length) {
        int endIndex = position.getIndex() + length;
        while (position.getIndex() < endIndex) {
            Object val = null;
            Tag tag = Tag.getTag(SMPPIO.bytesToShort(
                    data, position.getIndex()));
            int valueLen = SMPPIO.bytesToShort(data, position.getIndex() + 2);
            position.inc(4);
            ParamDescriptor descriptor = tag.getParamDescriptor();
            val = descriptor.readObject(data, position, valueLen);
            put(tag, val);
        }
    }

    /**
     * Encode all the optional parameters in this table to an output stream.
     * 
     * @param out
     *            The output stream to encode the parameters to.
     * @throws java.io.IOException
     *             If an error occurs writing to the output stream.
     */
    public void writeTo(OutputStream out) throws IOException {
        for (Map.Entry<Tag, Object> entry : entrySet()) {
            Tag tag = entry.getKey();
            Object value = entry.getValue();
            ParamDescriptor descriptor = tag.getParamDescriptor();
            int valueLen = descriptor.sizeOf(value);
            SMPPIO.writeShort(tag.intValue(), out);
            SMPPIO.writeShort(valueLen, out);
            descriptor.writeObject(value, out);
        }
    }

    /**
     * Get the value for a tag. This is a convenience method to convert
     * the tag integer to its appropriate Tag object and then look that
     * tag up in the map.
     * @param tag The tag&apos;s integer value.
     */
    public Object get(int tag) {
        Tag tagObj = Tag.getTag(tag);
        return get(tagObj);
    }

    /**
     * Get the tag&apos;s value as a string.
     * @param tag The tag to retrieve the value for.
     * @return The value as a string, or <code>null</code> if the specified
     * tag is not set in this table.
     */
    public String getString(Tag tag) {
        Object obj = get(tag);
        return obj != null ? obj.toString() : null;
    }

    /**
     * Get the tag&apos;s value as an int.
     * @param tag The tag to retrieve the value for.
     * @return The value as an integer, or <code>-1</code> if the specified
     * tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a number (castable as a <code>java.lang.Number</code>).
     */
    public int getInt(Tag tag) {
        Object obj = get(tag);
        if (obj != null) {
            return ((Number) obj).intValue();
        } else {
            return -1;
        }
    }
    
    /**
     * Get the tag&apos;s value as a long.
     * @param tag The tag to retrieve the value for.
     * @return The value as a long, or <code>-1</code> if the specified
     * tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a number (castable as a <code>java.lang.Number</code>).
     */
    public long getLong(Tag tag) {
        Object obj = get(tag);
        if (obj != null) {
            return ((Number) obj).intValue();
        } else {
            return -1;
        }
    }

    /**
     * Get the tag&apos;s value as a bit set.
     * @param tag The tag to retrieve the value for.
     * @return The value, cast as a <code>java.util.BitSet</code>, or
     * <code>null</code> if the specified tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a bit mask.
     */
    public BitSet getBitmask(Tag tag) {
        return ((BitSet) get(tag));
    }
    
    /**
     * Get the tag&apos;s value as a byte array.
     * @param tag The tag to retrieve the value for.
     * @return The value, cast as a <code>byte[]</code>, or <code>null</code>
     * if the specified tag is not set in this table.
     * @throws ClassCastException If the value for the specified tag is not
     * a byte array.
     */
    public byte[] getBytes(Tag tag) {
        return ((byte[]) get(tag));
    }
    
    @Override
    public Object put(Tag tag, Object value)
            throws BadValueTypeException, InvalidSizeForValueException {
        ParamDescriptor descriptor = tag.getParamDescriptor();
        if (descriptor == ParamDescriptor.NULL && value != null) {
            String error = MessageFormat.format(
                    "Tag {0} does not accept any value.",
                    new Object[] {tag});
            throw new BadValueTypeException(error);
        } else if (value == null) {
            String error = MessageFormat.format(
                    "Tag {0} does not accept a null value.",
                    new Object[] {tag});
            throw new BadValueTypeException(error);
        }

        // Enforce the length restrictions on the Value specified by the
        // Tag.
        int min = tag.getMinLength();
        int max = tag.getMaxLength();
        int actual = descriptor.sizeOf(value);
        if ((min > -1 && actual < min) || (max > -1 && actual > max)) {
            throw new InvalidSizeForValueException("Tag "
                    + tag.toHexString()
                    + " must have a length in the range " + min
                    + " <= len <= " + max);
        }
        return super.put(tag, value);
    }

    /**
     * Remove (or un-set) a tag/value from this table.
     * @param tag The tag to remove from the table.
     */
    public void remove(int tag) {
        super.remove(Tag.getTag(tag));
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<Tag, Object> entry : entrySet()) {
            ParamDescriptor descriptor = entry.getKey().getParamDescriptor();
            Object value = entry.getValue();
            buffer.append('{')
            .append(entry.getKey().toHexString())
            .append(',').append(descriptor.sizeOf(value))
            .append(',').append(value);
        }
        return buffer.toString();
    }

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
    public int getLength() {
        // Length is going to be (number of options) * (2 bytes for tag) * (2
        // bytes for length) + (size of all encoded values)
        int length = size() * 4;
        for (Map.Entry<Tag, Object> entry : entrySet()) {
            Tag tag = entry.getKey();
            length += tag.getParamDescriptor().sizeOf(entry.getValue());
        }
        return length;
    }
}
