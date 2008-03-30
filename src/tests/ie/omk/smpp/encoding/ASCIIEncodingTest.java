package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import ie.omk.smpp.encoding.ASCIIEncoding;

import org.testng.annotations.Test;

/**
 * @version $Id:$
 */
@Test
public class ASCIIEncodingTest {
    private static final String ALPHABET = 
        "\t\n\r!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWX"
        + "YZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    public void testEncodeFullAlphabet() throws Exception {
        ASCIIEncoding encoding = new ASCIIEncoding();
        byte[] encoded = encoding.encodeString(ALPHABET);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded.length, ALPHABET.length());
        assertEquals(decoded, ALPHABET);
    }
    
    public void testNonASCII() throws Exception {
        ASCIIEncoding encoding = new ASCIIEncoding();
        byte[] encoded = encoding.encodeString(ALPHABET);
        String decoded = encoding.decodeString(encoded);
        String nonASCII = "\u00e9\u00e8";
        encoded = encoding.encodeString(nonASCII);
        decoded = encoding.decodeString(encoded);
        assertEquals(encoded.length, nonASCII.length());
        assertFalse(nonASCII.equals(decoded));
    }
}
