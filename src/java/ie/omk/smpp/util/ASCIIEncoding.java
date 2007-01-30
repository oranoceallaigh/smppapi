package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;

/**
 * Encoding class representing the ASCII (IA5) alphabet encoding.
 */
public class ASCIIEncoding extends AlphabetEncoding {
    private static final int DCS = 1;

    /**
     * Construct a new ASCIIEncoding.
     */
    public ASCIIEncoding() {
        super(DCS);
        try {
            setCharset("US-ASCII");
        } catch (UnsupportedEncodingException x) {
            // All JVMs are required to support ASCII..
            throw new RuntimeException();
        }
    }
}

