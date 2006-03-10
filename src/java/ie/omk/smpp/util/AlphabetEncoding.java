package ie.omk.smpp.util;

import ie.omk.smpp.SMPPRuntimeException;

import java.io.UnsupportedEncodingException;

/**
 * SMS Alphabet to Java String mapping interface. Implementations of this
 * interface convert Java Unicode strings into a series of bytes representing
 * the String in a particular SMS alphabet.
 */
public class AlphabetEncoding extends MessageEncoding {
    
    private static final String BAD_IMPLEMENTATION =
        "Missing charset in implementation of AlphabetEncoding "
        + AlphabetEncoding.class.getName();
        
    private String charset;
    
    protected AlphabetEncoding(int dcs) {
        super(dcs);
    }

    /**
     * Set the charset of this alphabet encoding. Sub-classes can use this
     * to create new instances of alphabet encoding for character sets that
     * are supported by the JVM.
     * @param charset The character set to use for encoding and decoding.
     * @throws UnsupportedEncodingException If the JVM does not support the
     * specified character set.
     */
    protected void setCharset(String charset) throws UnsupportedEncodingException {
        new String("probe").getBytes(charset);
        this.charset = charset;
    }
    
    /**
     * Get the character set in use by this alpabet encoding (if any).
     * @return The character set in use by this alphabet encoding. This method
     * may return <code>null</code> if the implementation is not using a JVM-
     * supported character set.
     */
    public String getCharset() {
        return charset;
    }
    
    /**
     * Convert SMS message text into a Java String. Implementations of this
     * method <b>must </b> support decoding <code>null</code>. In such cases,
     * the String "" will be returned.
     */
    public String decodeString(byte[] b) {
        if (charset == null) {
            throw new SMPPRuntimeException(BAD_IMPLEMENTATION);
        }
        try {
            if (b != null) {
                return new String(b, charset);
            }
        } catch (UnsupportedEncodingException x) {
            // Will already have been detected by the constructor.
        }
        return "";
    }

    /**
     * Convert a Java String into SMS message text. Implementations of this
     * method <b>must </b> support encoding a <code>null</code> string. In
     * such cases, a byte array of length 0 will be returned.
     */
    public byte[] encodeString(String s) {
        if (charset == null) {
            throw new SMPPRuntimeException(BAD_IMPLEMENTATION);
        }
        try {
            if (s != null) {
                return s.getBytes(charset);
            }
        } catch (java.io.UnsupportedEncodingException x) {
            // Will already have been detected by the constructor.
        }
        return new byte[0];
    }
}

