package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * SMS Alphabet to Java String mapping interface. Implementations of this
 * interface convert Java Unicode strings into a series of bytes representing
 * the String in a particular SMS alphabet.
 */
public class AlphabetEncoding extends AbstractMessageEncoding<String> {
    private String charset;

    /**
     * Create a new alphabet encoding.
     * @param dcs The data coding value to be used for this encoding.
     */
    protected AlphabetEncoding(int dcs) {
        super(dcs);
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
    public String decode(byte[] data) {
        if (data != null) {
            return decode(data, 0, data.length);
        } else {
            return "";
        }
    }

    /**
     * Convert SMS message text into a Java String.
     * @param data The bytes to decode.
     * @param offset The offset within the data to begin decoding characters.
     * @param length The number of bytes to decode to characters.
     * @throws NullPointerException If <tt>data</tt> is <tt>null</tt>.
     */
    public String decode(byte[] data, int offset, int length) {
        try {
            if (data != null) {
                return new String(data, offset, length, charset);
            } else {
                throw new NullPointerException("Data cannot be null");
            }
        } catch (UnsupportedEncodingException x) {
            // Shouldn't happen - setCharset should have detected this.
            throw new RuntimeException();
        }
    }

    /**
     * Convert a Java String into SMS message text. Implementations of this
     * method <b>must </b> support encoding a <code>null</code> string. In
     * such cases, a byte array of length 0 will be returned.
     */
    public byte[] encode(String string) {
        try {
            if (string != null) {
                return string.getBytes(charset);
            }
        } catch (UnsupportedEncodingException x) {
            // Shouldn't happen - setCharset should have detected this.
            throw new RuntimeException();
        }
        return new byte[0];
    }

    /**
     * Get the number of bytes a particular string would encode as on the
     * wire.
     * @return The number of bytes <code>string</code> would encode to.
     */
    public int getEncodedSize(String string) {
        if (string != null) {
            return encode(string).length;
        } else {
            return 0;
        }
    }
    
    /**
     * Set the charset of this alphabet encoding. Sub-classes can use this
     * to create new instances of alphabet encoding for character sets that
     * are supported by the JVM. This method can only be called once.
     * Subsequent calls will throw a RuntimeException.
     * @param charset The character set to use for encoding and decoding.
     * @throws UnsupportedEncodingException If the JVM does not support the
     * specified character set.
     */
    protected void setCharset(String charset) throws UnsupportedEncodingException {
        if (this.charset != null) {
            throw new SMPPRuntimeException("Cannot change charset.");
        }
        new String("probe").getBytes(charset);
        this.charset = charset;
    }
}
