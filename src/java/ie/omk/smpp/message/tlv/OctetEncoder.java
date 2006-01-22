package ie.omk.smpp.message.tlv;

/**
 * Encode an octet string to a byte array. This class is encoding byte arrays to
 * byte arrays! Therefore it's just copying bytes around. Not much more to it.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class OctetEncoder implements Encoder {

    /**
     * Create a new OctetEncoder.
     */
    public OctetEncoder() {
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset)
            throws ArrayIndexOutOfBoundsException {
        try {
            byte[] valBytes = (byte[]) value;
            System.arraycopy(valBytes, 0, b, offset, valBytes.length);
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type byte[]");
        }
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length)
            throws ArrayIndexOutOfBoundsException {
        byte[] val = new byte[length];
        System.arraycopy(b, offset, val, 0, length);
        return val;
    }

    public int getValueLength(Tag tag, Object value) {
        try {
            byte[] b = (byte[]) value;
            return b.length;
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type byte[]");
        }
    }
}

