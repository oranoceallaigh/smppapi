package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

/**
 * Encoding class representing the ASCII (IA5) alphabet encoding.
 */
public class ASCIIEncoding extends AlphabetEncoding {
    private static final int DCS = 1;

    /**
     * Construct a new ASCIIEncoding.
     */
    public ASCIIEncoding() throws UnsupportedEncodingException {
        super(DCS);
        setCharset("US-ASCII");
    }
}
