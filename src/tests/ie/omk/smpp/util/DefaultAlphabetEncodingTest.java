package ie.omk.smpp.util;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @version $Id:$
 */
public class DefaultAlphabetEncodingTest extends TestCase {

    private final DefaultAlphabetEncoding encoding =
        new DefaultAlphabetEncoding();
    
    public void testDefaultAlphabet() {
        // 127 characters in the base table, and 2 bytes for each character
        // in the extended table.
        final int expectedEncodedLength = 127 + (2 * 9);
        final String alphabet =
            "@\u00a3$\u00a5\u00e8\u00e9\u00f9\u00ec\u00f2\u00c7\n\u00d8\u00f8"
            + "\r\u00c5\u00e5\u0394_\u03a6\u0393\u039b\u03a9\u03a0\u03a8\u03a3"
            + "\u0398\u039e\u00c6\u00e6\u00df\u00c9 !\"#\u00a4%&\'()*+,-./012"
            + "3456789:;<=>?\u00a1ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00c4\u00d6\u00d1"
            + "\u00dc\u00a7\u00bfabcdefghijklmnopqrstuvwxyz\u00e4\u00f6\u00f1"
            + "\u00fc\u00e0^{}\\[~]|\u20ac";
        
        byte[] encoded = encoding.encodeString(alphabet);
        String decoded = encoding.decodeString(encoded);
        
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(expectedEncodedLength, encoded.length);
        assertEquals(alphabet, decoded);
    }
    
    public void testPackUnder8ByteBoundary() throws Exception {
        final byte[] testArray = new byte[] {
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66,
        };
        final byte[] expected = new byte[] {
                (byte) 0x61, (byte) 0xf1, (byte) 0x98, 0x5c, 0x36, 0x3,
        };
        byte[] actual = encoding.pack(testArray);
        assertEquals(expected.length, actual.length);
        assertTrue(Arrays.equals(expected, actual));
    }
    
    public void testPackOn8ByteBoundary() throws Exception {
        final byte[] testArray = new byte[] {
                0x2, 0x45, 0x12, 0x7e, 0x33, 0x66, 0x7f, 0x1a,
        };
        final byte[] expected = new byte[] {
                (byte) 0x82,
                (byte) 0xa2,
                (byte) 0xc4,
                0x3f,
                0x33,
                (byte) 0xff,
                0x35,
        };
        byte[] actual = encoding.pack(testArray);
        assertEquals(expected.length, actual.length);
        assertTrue(Arrays.equals(expected, actual));
    }

    public void testPackOver8ByteBoundary() throws Exception {
        final byte[] testArray = new byte[] {
                0x02, 0x45, 0x12, 0x7e, 0x33, 0x66, 0x7f, 0x1a,
                0x53, 0x7a, 0x47, 0x39, 0x78, 0x6f, 0x14, 0x4b,
                0x0d, 0x33, 0x44,
        };
        final byte[] expected = new byte[] {
                (byte) 0x82,
                (byte) 0xa2,
                (byte) 0xc4,
                0x3f,
                0x33,
                (byte) 0xff,
                0x35,
                0x53,
                (byte) 0xfd,
                0x31,
                (byte) 0x87,
                0x7f,
                0x53,
                (byte) 0x96,
                (byte) 0x8d,
                0x19,
                0x11,
        };
        byte[] actual = encoding.pack(testArray);
        assertEquals(expected.length, actual.length);
        assertTrue(Arrays.equals(expected, actual));
    }
    
    public void testUnpackUnder8ByteBoundary() throws Exception {
        final byte[] testArray = new byte[] {
                (byte) 0x61, (byte) 0xf1, (byte) 0x98, 0x5c, 0x36, 0x3,
        };
        final byte[] expected = new byte[] {
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66,
        };
        byte[] actual = encoding.unpack(testArray);
        assertEquals(expected.length, actual.length);
        assertTrue(Arrays.equals(expected, actual));
    }
    
    public void testUnpackOn8ByteBoundary() throws Exception {
        final byte[] testArray = new byte[] {
                (byte) 0x82,
                (byte) 0xa2,
                (byte) 0xc4,
                0x3f,
                0x33,
                (byte) 0xff,
                0x35,
        };
        final byte[] expected = new byte[] {
                0x2, 0x45, 0x12, 0x7e, 0x33, 0x66, 0x7f, 0x1a,
        };
        byte[] actual = encoding.unpack(testArray);
        assertEquals(expected.length, actual.length);
        assertTrue(Arrays.equals(expected, actual));
    }
    
    public void testUnpackOver8ByteBoundary() throws Exception {
        final byte[] testArray = new byte[] {
                (byte) 0x82,
                (byte) 0xa2,
                (byte) 0xc4,
                0x3f,
                0x33,
                (byte) 0xff,
                0x35,
                0x53,
                (byte) 0xfd,
                0x31,
                (byte) 0x87,
                0x7f,
                0x53,
                (byte) 0x96,
                (byte) 0x8d,
                0x19,
                0x11,
        };
        final byte[] expected = new byte[] {
                0x02, 0x45, 0x12, 0x7e, 0x33, 0x66, 0x7f, 0x1a,
                0x53, 0x7a, 0x47, 0x39, 0x78, 0x6f, 0x14, 0x4b,
                0x0d, 0x33, 0x44,
        };
        byte[] actual = encoding.unpack(testArray);
        assertEquals(expected.length, actual.length);
        assertTrue(Arrays.equals(expected, actual));
    }
}
