package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import ie.omk.smpp.encoding.UCS2Encoding;

import org.testng.annotations.Test;

@Test
public class UCS2EncodingTest {
    public void testUCS2Encoding() throws Exception {
        final String string = "abcdefJKLMN1234567890\u00c0\u00c3\u20ac";
        UCS2Encoding encoding = new UCS2Encoding();
        byte[] encoded = encoding.encodeString(string);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded.length, string.length() * 2);
        assertEquals(decoded, string);
    }
}
