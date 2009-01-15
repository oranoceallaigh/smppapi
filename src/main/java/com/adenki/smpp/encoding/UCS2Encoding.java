package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

public class UCS2Encoding extends AlphabetEncoding {
    private static final String ENCODING = "ISO-10646-UCS-2";
    private static final int DCS = 8;

    /**
     * Construct a new UCS2 encoding.
     * @throws java.io.UnsupportedEncodingException if the ISO-10646-UCS-2
     * charset is not supported by the JVM.
     */
    public UCS2Encoding() throws UnsupportedEncodingException {
        super(DCS);
        setCharset(ENCODING);
    }
}
