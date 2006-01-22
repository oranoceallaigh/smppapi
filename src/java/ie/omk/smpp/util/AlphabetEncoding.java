package ie.omk.smpp.util;

/**
 * SMS Alphabet to Java String mapping interface. Implementations of this
 * interface convert Java Unicode strings into a series of bytes representing
 * the String in a particular SMS alphabet.
 */
public abstract class AlphabetEncoding extends ie.omk.smpp.util.MessageEncoding {
    protected AlphabetEncoding(int dcs) {
        super(dcs);
    }

    /**
     * Convert SMS message text into a Java String. Implementations of this
     * method <b>must </b> support decoding <code>null</code>. In such cases,
     * the String "" will be returned.
     */
    public abstract String decodeString(byte[] b);

    /**
     * Convert a Java String into SMS message text. Implementations of this
     * method <b>must </b> support encoding a <code>null</code> string. In
     * such cases, a byte array of length 0 will be returned.
     */
    public abstract byte[] encodeString(String s);
}

