package ie.omk.smpp.message.tlv;

import ie.omk.smpp.util.APIConfig;

/**
 * Value encoder for string types. Operates on the java.lang.String type.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class StringEncoder implements Encoder {

    private static final String ASCII_UNSUPPORTED_MSG = "Your JVM doesn't support ASCII!";
    private static final String ASCII = "US-ASCII";

    private boolean mbloxHack;
    /**
     * Create a new StringEncoder.
     */
    public StringEncoder() {
        mbloxHack =
            APIConfig.getInstance().getBoolean(APIConfig.MBLOX_HACK, false);
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) {
        try {
            String s = value.toString();
            int len = s.length();

            byte[] b1 = s.getBytes(ASCII);
            System.arraycopy(b1, 0, b, offset, len);
            // Don't encode the nul-terminator if the mblox hack is
            // enabled.
            if (!mbloxHack) {
                b[offset + len] = (byte) 0;
            }
        } catch (java.io.UnsupportedEncodingException x) {
            // Java spec _requires_ US-ASCII support
            throw new RuntimeException(ASCII_UNSUPPORTED_MSG);
        }
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
        try {
            // Use all the bytes if the mblox hack is enabled, otherwise skip
            // the last byte as it should be the nul terminator.
            int realLen = mbloxHack ? length : length - 1;
            String s = new String(b, offset, realLen, ASCII);
            return s;
        } catch (java.io.UnsupportedEncodingException x) {
            // Java spec _requires_ US-ASCII support
            throw new RuntimeException(ASCII_UNSUPPORTED_MSG);
        }
    }

    public int getValueLength(Tag tag, Object value) {
        int len = value.toString().length();
        if (!mbloxHack) {
            // 1 for the nul byte
            len++;
        }
        return len;
    }
}

