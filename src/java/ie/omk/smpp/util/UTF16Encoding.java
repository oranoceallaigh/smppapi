package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;

public class UTF16Encoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final int DCS = 8;

    /**
     * Construct a new UTF16 encoding.
     * 
     * @param bigEndian
     *            true to use UTF-16BE, false to use UTF-16LE.
     */
    public UTF16Encoding(boolean bigEndian) throws UnsupportedEncodingException {
        super(DCS);
        if (!bigEndian) {
            setCharset("UTF-16LE");
        } else {
            setCharset("UTF-16BE");
        }
    }
}

