package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import ie.omk.smpp.encoding.UTF16Encoding;

import org.testng.annotations.Test;

@Test
public class UTF16EncodingTest {
    public void testUTF16Encoding() throws Exception {
        final String string = "abcdefJKLMN1234567890\u00c0\u00c3\u20ac";
        
        UTF16Encoding encoding = new UTF16Encoding(true);
        byte[] encoded = encoding.encode(string);
        String decoded = encoding.decode(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded.length, string.length() * 2);
        assertEquals(decoded, string);

        encoding = new UTF16Encoding(false);
        encoded = encoding.encode(string);
        decoded = encoding.decode(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded.length, string.length() * 2);
        assertEquals(decoded, string);
    }

}
