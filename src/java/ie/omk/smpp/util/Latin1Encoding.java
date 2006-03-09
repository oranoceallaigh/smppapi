package ie.omk.smpp.util;

/**
 * Encoding class representing the Latin-1 (ISO-8859-1) alphabet encoding.
 */
public class Latin1Encoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final String ISO_8859_1 = "ISO-8859-1";

    private static final int DCS = 3;

    private static final Latin1Encoding INSTANCE = new Latin1Encoding();

    /**
     * Construct a new Latin1Encoding.
     */
    public Latin1Encoding() {
        super(DCS);
    }

    /**
     * Get the singleton instance of Latin1Encoding.
     * @deprecated
     */
    public static Latin1Encoding getInstance() {
        return INSTANCE;
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in Latin-1 format.
     */
    public String decodeString(byte[] b) {
        try {
            if (b != null) {
                return new String(b, ISO_8859_1);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return "";
    }

    /**
     * Encode a Java String to bytes using Latin1.
     */
    public byte[] encodeString(String s) {
        try {
            if (s != null) {
                return s.getBytes(ISO_8859_1);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return new byte[0];
    }
}

