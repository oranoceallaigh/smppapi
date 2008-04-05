package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

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
        byte[] encoded = encoding.encode(ALPHABET);
        String decoded = encoding.decode(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded.length, ALPHABET.length());
        assertEquals(decoded, ALPHABET);
    }
    
    public void testNonASCII() throws Exception {
        ASCIIEncoding encoding = new ASCIIEncoding();
        byte[] encoded = encoding.encode(ALPHABET);
        String decoded = encoding.decode(encoded);
        String nonASCII = "\u00e9\u00e8";
        encoded = encoding.encode(nonASCII);
        decoded = encoding.decode(encoded);
        assertEquals(encoded.length, nonASCII.length());
        assertFalse(nonASCII.equals(decoded));
    }
}
