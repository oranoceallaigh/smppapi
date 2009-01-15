package com.adenki.smpp.message.tlv;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.adenki.smpp.message.param.ParamDescriptor;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Implementation of the TLVTable interface.
 * This implementation will maintain the ordering of added parameters.
 * @version $Id$
 */
public class TLVTableImpl extends LinkedHashMap<Tag, Object> implements TLVTable {
    private static final long serialVersionUID = 2L;

    public TLVTableImpl() {
    }
    
    /**
     * Decode a full set of optional parameters from a byte array.
     */
    public void readFrom(PacketDecoder decoder, int length) {
        int endIndex = (decoder.getParsePosition() + length) - 1;
        while (decoder.getParsePosition() < endIndex) {
            Object val = null;
            Tag tag = Tag.getTag(decoder.readUInt2());
            int valueLen = decoder.readUInt2();
            ParamDescriptor descriptor = tag.getParamDescriptor();
            val = descriptor.readObject(decoder, valueLen);
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
    public void writeTo(PacketEncoder encoder) throws IOException {
        for (Map.Entry<Tag, Object> entry : entrySet()) {
            Tag tag = entry.getKey();
            Object value = entry.getValue();
            ParamDescriptor descriptor = tag.getParamDescriptor();
            int valueLen = descriptor.sizeOf(value);
            encoder.writeUInt2(tag.intValue());
            encoder.writeUInt2(valueLen);
            descriptor.writeObject(value, encoder);
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
    
    /**
     * Set the value of a TLV.
     * @param tag The tag of the parameter to set.
     * @param value The tag&apos;s value.
     * @throws BadValueTypeException If <code>tag</code> does not accept
     * the type that <code>value</code> is.
     * @throws InvalidSizeForValueException If <code>value</code>
     * exceeds either the minimum or maximum size allowed by <code>tag</code>.
     */
    @Override
    public Object put(Tag tag, Object value)
            throws BadValueTypeException, InvalidSizeForValueException {
        ParamDescriptor descriptor = tag.getParamDescriptor();
        if (descriptor.equals(BasicDescriptors.NULL) && value != null) {
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

    public Object put(Tag tag, char value) {
        return put(tag, Character.valueOf(value));
    }
    
    public Object put(Tag tag, short value) {
        return put(tag, Short.valueOf(value));
    }
    
    public Object put(Tag tag, int value) {
        return put(tag, Integer.valueOf(value));
    }
    
    public Object put(Tag tag, long value) {
        return put(tag, Long.valueOf(value));
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
