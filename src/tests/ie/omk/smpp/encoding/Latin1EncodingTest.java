package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import ie.omk.smpp.encoding.Latin1Encoding;

import org.testng.annotations.Test;

/**
 * @version $Id:$
 */
@Test
public class Latin1EncodingTest {
    // "Test message" followed by:
    // Yen symbol
    // Pound sign (European interpretation, not what I would call a "hash").
    // Superscript 3
    // Latin capital letter AE
    private final static byte[] EXPECTED_BYTES = {
        0x54, 0x65, 0x73, 0x74,
        0x20, 0x6d, 0x65, 0x73,
        0x73, 0x61, 0x67, 0x65,
        (byte) 0xa5, (byte) 0xa3, (byte) 0xb3, (byte) 0xc6,
    };
    private static final String STRING = "Test message\u00a5\u00a3\u00b3\u00c6";

    public void testLatinEncoding() throws Exception {
        Latin1Encoding encoding = new Latin1Encoding();
        byte[] encoded = encoding.encodeString(STRING);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded, EXPECTED_BYTES);
        assertEquals(decoded, STRING);
    }
    
    public void testNonLatin1() throws Exception {
        Latin1Encoding encoding = new Latin1Encoding();
        String nonLatin1 = "\u20ac";
        byte[] encoded = encoding.encodeString(nonLatin1);
        String decoded = encoding.decodeString(encoded);
        assertEquals(encoded.length, nonLatin1.length());
        assertFalse(nonLatin1.equals(decoded));
    }
}
