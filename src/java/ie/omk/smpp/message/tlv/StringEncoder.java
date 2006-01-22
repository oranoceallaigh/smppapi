package ie.omk.smpp.message.tlv;

/**
 * Value encoder for string types. Operates on the java.lang.String type.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class StringEncoder implements Encoder {

    /**
     * Create a new StringEncoder.
     */
    public StringEncoder() {
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset)
            throws ArrayIndexOutOfBoundsException {
        try {
            String s = value.toString();
            int len = s.length();

            byte[] b1 = s.getBytes("US-ASCII");
            System.arraycopy(b1, 0, b, offset, len);
            b[offset + len] = (byte) 0;
        } catch (java.io.UnsupportedEncodingException x) {
            // Java spec _requires_ US-ASCII support
        }
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length)
            throws ArrayIndexOutOfBoundsException {
        try {
            String s = new String(b, offset, length - 1, "US-ASCII");
            return s;
        } catch (java.io.UnsupportedEncodingException x) {
            // Java spec _requires_ US-ASCII support
        }
        return "";
    }

    public int getValueLength(Tag tag, Object value) {
        return value.toString().length() + 1; // 1 for the nul byte
    }
}

