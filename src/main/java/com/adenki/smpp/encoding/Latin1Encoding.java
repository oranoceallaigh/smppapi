package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

/**
 * Encoding class representing the Latin-1 (ISO-8859-1) alphabet encoding.
 */
public class Latin1Encoding extends AlphabetEncoding {
    private static final int DCS = 3;

    /**
     * Construct a new Latin1Encoding.
     */
    public Latin1Encoding() throws UnsupportedEncodingException {
        super(DCS);
        setCharset("ISO-8859-1");
    }
}
