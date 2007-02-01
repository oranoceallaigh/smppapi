package ie.omk.smpp.util;

import junit.framework.TestCase;

public class UTF16EncodingTest extends TestCase {
    public void testUTF16Encoding() throws Exception {
        final String string = "abcdefJKLMN1234567890\u00c0\u00c3\u20ac";
        
        UTF16Encoding encoding = new UTF16Encoding(true);
        byte[] encoded = encoding.encodeString(string);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(string.length() * 2, encoded.length);
        assertEquals(string, decoded);

        encoding = new UTF16Encoding(false);
        encoded = encoding.encodeString(string);
        decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(string.length() * 2, encoded.length);
        assertEquals(string, decoded);
    }

}
