package ie.omk.smpp.message.tlv;

import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;

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

//        longVal = num.longValue() & mask;
//        SMPPIO.longToBytes(longVal, tag.getLength(), b, offset);
        // TODO:
        throw new UnsupportedOperationException("Use writeTo(Tag, Object, OutputStream)");
    }

    public void writeTo(Tag tag, Object value, OutputStream out) throws IOException {
        try {
            Number number = (Number) value;
            switch (tag.getLength()) {
            case 8:
                SMPPIO.writeLong(number.longValue(), out);
                break;
            case 4:
                SMPPIO.writeLongInt(number.longValue(), out);
                break;
            case 2:
                SMPPIO.writeShort(number.intValue(), out);
                break;
            default:
                SMPPIO.writeByte(number.intValue(), out);
            }
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type "
                    + "java.lang.Number");
        }
    }
    
    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
        Number number;
        switch (length) {
        case 8:
            number = new Long(SMPPIO.bytesToLong(b, offset));
            break;
        case 4:
            number = new Long(SMPPIO.bytesToLongInt(b, offset));
            break;
        case 2:
            number = new Integer(SMPPIO.bytesToShort(b, offset));
            break;
        default:
            number = new Integer(SMPPIO.bytesToByte(b, offset));
        }
        return number;
    }

    public int getValueLength(Tag tag, Object value) {
        return tag.getLength();
    }
}
