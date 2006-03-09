package ie.omk.smpp.message.tlv;

import ie.omk.smpp.util.SMPPIO;

/**
 * Encode a <code>java.lang.Number</code> to a byte array. The number encoder
 * is for encoding any optional parameters that are defined as integers.
 * NumberEncoder operates on the {@link java.lang.Number}type and therefore
 * accepts values of Byte, Short, Integer and Long. Encoding and decoding of
 * values using this class will never fail due to a lenght mismatch..the value
 * will always be either zero-padded or truncated down to the appropriate size.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class NumberEncoder implements Encoder {

    /**
     * Create a new NumberEncoder.
     */
    public NumberEncoder() {
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) {

        long longVal = 0;
        long mask = 0;
        Number num;
        try {
            num = (Number) value;
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type "
                    + "java.lang.Number");
        }

        if (value instanceof Byte) {
            mask = 0xff;
        } else if (value instanceof Short) {
            mask = 0xffff;
        } else if (value instanceof Integer) {
            mask = 0xffffffff;
        } else {
            mask = 0xffffffffffffffffL;
        }

        longVal = num.longValue() & mask;
        SMPPIO.longToBytes(longVal, tag.getLength(), b, offset);
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
        long val = SMPPIO.bytesToLong(b, offset, length);

        if (length <= 4) {
            return new Integer((int) val);
        } else {
            return new Long(val);
        }
    }

    public int getValueLength(Tag tag, Object value) {
        return tag.getLength();
    }
}
