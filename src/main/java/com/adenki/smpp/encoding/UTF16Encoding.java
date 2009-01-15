package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

public class UTF16Encoding extends com.adenki.smpp.encoding.AlphabetEncoding {
    private static final int DCS = 8;

    /**
     * Construct a new big-endian UTF16 encoding.
     * @throws UnsupportedEncodingException If the JVM does not support
     * the UTF16 encoding.
     */
    public UTF16Encoding() throws UnsupportedEncodingException {
        this(true);
    }
    
    /**
     * Construct a new UTF16 encoding.
     * @param bigEndian <tt>true</tt> to use UTF-16BE, false to use UTF-16LE.
     * @throws UnsupportedEncodingException If the JVM does not support
     * the UTF16 encoding.
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
