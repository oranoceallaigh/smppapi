package ie.omk.smpp.util;

/**
 * Encoding class representing the Latin-1 (ISO-8859-1) alphabet encoding.
 */
public class Latin1Encoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final int DCS = 3;

    private static final Latin1Encoding instance = new Latin1Encoding();

    /**
     * Construct a new Latin1Encoding.
     */
    private Latin1Encoding() {
        super(DCS);
    }

    /**
     * Get the singleton instance of Latin1Encoding.
     */
    public static Latin1Encoding getInstance() {
        return instance;
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in Latin-1 format.
     */
    public String decodeString(byte[] b) {
        if (b == null) {
            return "";
        }

        try {
            return new String(b, "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException x) {
            return null;
        }
    }

    /**
     * Encode a Java String to bytes using Latin1.
     */
    public byte[] encodeString(String s) {
        if (s == null) {
            return new byte[0];
        }

        try {
            return s.getBytes("ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException x) {
            return null;
        }
    }
}

