package ie.omk.smpp.util;

/**
 * Encoding class representing the ASCII (IA5) alphabet encoding.
 */
public class ASCIIEncoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final int DCS = 1;

    private static final ASCIIEncoding instance = new ASCIIEncoding();

    /**
     * Construct a new ASCIIEncoding.
     */
    public ASCIIEncoding() {
        super(DCS);
    }

    /**
     * Get the singleton instance of ASCIIEncoding.
     */
    public static ASCIIEncoding getInstance() {
        return instance;
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in ASCII format.
     */
    public String decodeString(byte[] b) {
        if (b == null) {
            return "";
        }

        try {
            return new String(b, "US-ASCII");
        } catch (java.io.UnsupportedEncodingException x) {
            return "";
        }
    }

    /**
     * Encode a Java String to bytes using the ASCII encoding.
     */
    public byte[] encodeString(String s) {
        if (s == null) {
            return new byte[0];
        }

        try {
            return s.getBytes("US-ASCII");
        } catch (java.io.UnsupportedEncodingException x) {
            return new byte[0];
        }
    }
}

