package ie.omk.smpp.util;

import junit.framework.TestCase;

public class UCS2EncodingTest extends TestCase {
    public void testUCS2Encoding() throws Exception {
        final String string = "abcdefJKLMN1234567890\u00c0\u00c3\u20ac";
        UCS2Encoding encoding = new UCS2Encoding();
        byte[] encoded = encoding.encodeString(string);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(string.length() * 2, encoded.length);
        assertEquals(string, decoded);
    }
}
