package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;

public class UCS2Encoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final String ENCODING = "ISO-10646-UCS-2";

    private static final int DCS = 8;

    private static UCS2Encoding INSTANCE = null;
    
    static {
        try {
            INSTANCE = new UCS2Encoding();
        } catch (UnsupportedEncodingException x) {
        }
    }

    /**
     * Construct a new UCS2 encoding.
     * @throws java.io.UnsupportedEncodingException if the ISO-10646-UCS-2
     * charset is not supported by the JVM.
     */
    public UCS2Encoding() throws UnsupportedEncodingException {
        super(DCS);

        // Force an exception if the charset is not supported.
        new String(new byte[0], ENCODING);
    }

    /**
     * Get an instance of the UCS2Encoding.
     * @throws java.io.UnsupportedEncodingException if the ISO-10646-UCS-2
     * charset is not supported by the JVM.
     * @deprecated
     */
    public static UCS2Encoding getInstance() throws UnsupportedEncodingException {
        return INSTANCE;
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in UCS2 format.
     */
    public String decodeString(byte[] b) {
        try {
            if (b != null) {
                return new String(b, ENCODING);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return "";
    }

    /**
     * Encode a Java String to bytes using UCS2 (UTF-16).
     */
    public byte[] encodeString(String s) {
        try {
            if (s != null) {
                return s.getBytes(ENCODING);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return new byte[0];
    }
}
