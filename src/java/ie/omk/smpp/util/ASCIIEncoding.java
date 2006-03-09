package ie.omk.smpp.util;

/**
 * Encoding class representing the ASCII (IA5) alphabet encoding.
 */
public class ASCIIEncoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final String ASCII = "US-ASCII";

    private static final int DCS = 1;

    private static final ASCIIEncoding INSTANCE = new ASCIIEncoding();

    /**
     * Construct a new ASCIIEncoding.
     */
    public ASCIIEncoding() {
        super(DCS);
    }

    /**
     * Get the singleton instance of ASCIIEncoding.
     * @deprecated
     */
    public static ASCIIEncoding getInstance() {
        return INSTANCE;
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in ASCII format.
     */
    public String decodeString(byte[] b) {
        try {
            if (b != null) {
                return new String(b, ASCII);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return "";
    }

    /**
     * Encode a Java String to bytes using the ASCII encoding.
     */
    public byte[] encodeString(String s) {
        try {
            if (s != null) {
                return s.getBytes(ASCII);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return new byte[0];
    }
}

