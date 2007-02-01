package ie.omk.smpp.util;

import junit.framework.TestCase;

/**
 * @version $Id:$
 */
public class ASCIIEncodingTest extends TestCase {
    private static final String ALPHABET = 
        "\t\n\r!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWX"
        + "YZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    public void testEncodeFullAlphabet() throws Exception {
        ASCIIEncoding encoding = new ASCIIEncoding();
        byte[] encoded = encoding.encodeString(ALPHABET);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(ALPHABET.length(), encoded.length);
        assertEquals(ALPHABET, decoded);
    }
    
    public void testNonASCII() throws Exception {
        ASCIIEncoding encoding = new ASCIIEncoding();
        byte[] encoded = encoding.encodeString(ALPHABET);
        String decoded = encoding.decodeString(encoded);
        String nonASCII = "\u00e9\u00e8";
        encoded = encoding.encodeString(nonASCII);
        decoded = encoding.decodeString(encoded);
        assertEquals(nonASCII.length(), encoded.length);
        assertFalse(nonASCII.equals(decoded));
    }
}
