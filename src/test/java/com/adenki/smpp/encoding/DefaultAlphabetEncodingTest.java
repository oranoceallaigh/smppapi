package com.adenki.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

/**
 * @version $Id:$
 */
@Test
public class DefaultAlphabetEncodingTest extends BaseAlphabetEncodingTest<DefaultAlphabetEncoding> {

    private static final String ALPHABET =
        "@\u00a3$\u00a5\u00e8\u00e9\u00f9\u00ec"
        + "\u00f2\u00c7\n\u00d8\u00f8\r\u00c5\u00e5"
        + "\u0394_\u03a6\u0393\u039b\u03a9\u03a0\u03a8"
        + "\u03a3\u0398\u039e\u00c6\u00e6\u00df\u00c9"
        + " !\"#\u00a4%&'"
        + "()*+,-./"
        + "01234567"
        + "89:;<=>?"
        + "\u00a1ABCDEFG"
        + "HIJKLMNO"
        + "PQRSTUVW"
        + "XYZ\u00c4\u00d6\u00d1\u00dc\u00a7"
        + "\u00bfabcdefg"
        + "hijklmno"
        + "pqrstuvw"
        + "xyz\u00e4\u00f6\u00f1\u00fc\u00e0"
        + "^{}\\[~]|\u20ac";

    private static final int[] BYTES = {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
        0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
        0x18, 0x19, 0x1a, 0x1c, 0x1d, 0x1e, 0x1f,
        0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
        0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f,
        0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
        0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f,
        0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
        0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f,
        0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57,
        0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f,
        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,
        0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f,
        0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
        0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0x7f,
        0x1b, 0x14, 0x1b, 0x28, 0x1b, 0x29, 0x1b, 0x2f,
        0x1b, 0x3c, 0x1b, 0x3d, 0x1b, 0x3e, 0x1b, 0x40,
        0x1b, 0x65,
    };
    
    @Override
    protected TestData getArrayToDecode() {
        return new TestData(BYTES, ALPHABET);
    }

    @Override
    protected DefaultAlphabetEncoding getEncodingToTest() throws UnsupportedEncodingException {
        return new DefaultAlphabetEncoding();
    }

    @Override
    protected TestData getFullySupportedStringToEncode() {
        return new TestData(ALPHABET, BYTES);
    }

    @Override
    protected TestData getPartiallySupportedStringToEncode() {
        String string = "Unsupported character: \u010c";
        int[] expectedBytes = new int[] {
                85, 110, 115, 117, 112, 112, 111, 114,
                116, 101, 100, 32, 99, 104, 97, 114,
                97, 99, 116, 101, 114, 58, 32, 63,
        };
        return new TestData(expectedBytes, string);
    }

    public void testPackUnder8ByteBoundary() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        final byte[] testArray = new byte[] {
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66,
        };
        final byte[] expected = new byte[] {
                (byte) 0x61, (byte) 0xf1, (byte) 0x98, 0x5c, 0x36, 0x3,
        };
        byte[] actual = encoding.pack(testArray);
        assertEquals(actual.length, expected.length);
        assertEquals(actual, expected);
    }
    
    public void testPackOn8ByteBoundary() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
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
        assertEquals(actual.length, expected.length);
        assertEquals(actual, expected);
    }

    public void testPackOver8ByteBoundary() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
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
        assertEquals(actual.length, expected.length);
        assertEquals(actual, expected);
    }
    
    public void testPackZeroLengthArray() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        byte[] packed = encoding.pack(new byte[0]);
        assertNotNull(packed);
        assertEquals(packed.length, 0);
    }
    
    public void testUnpackUnder8ByteBoundary() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        final byte[] testArray = new byte[] {
                (byte) 0x61, (byte) 0xf1, (byte) 0x98, 0x5c, 0x36, 0x3,
        };
        final byte[] expected = new byte[] {
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66,
        };
        byte[] actual = encoding.unpack(testArray);
        assertEquals(actual.length, expected.length);
        assertEquals(actual, expected);
    }
    
    public void testUnpackOn8ByteBoundary() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
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
        assertEquals(actual.length, expected.length);
        assertEquals(actual, expected);
    }
    
    public void testUnpackOver8ByteBoundary() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
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
        assertEquals(actual.length, expected.length);
        assertEquals(actual, expected);
    }
    
    public void testUnpackZeroLengthArray() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        byte[] unpacked = encoding.unpack(new byte[0]);
        assertNotNull(unpacked);
        assertEquals(unpacked.length, 0);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testNegativeReplacementCharIsRejected() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        encoding.setUnknownCharReplacement(-3);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testTooLargeReplacementCharIsRejected() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        encoding.setUnknownCharReplacement(134);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testEscapeCharAsReplacementCharIsRejected() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        encoding.setUnknownCharReplacement(DefaultAlphabetEncoding.EXTENDED_ESCAPE);
    }
    
    public void testUnsupportedCharsAreReplacedByUnknownCharReplacement() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        encoding.setUnknownCharReplacement(0x2a);
        byte[] bytes = encoding.encode("a\u00e3");
        assertFalse(bytes[0] == encoding.getUnknownCharReplacement());
        assertEquals(bytes[1], encoding.getUnknownCharReplacement());
    }
    
    public void testDecodeReplacesInvalidCharactersWithUnknownCharReplacement() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        encoding.setUnknownCharReplacement(0x2a);
        String s = encoding.decode(new byte[] {0x41, (byte) 0xfb});
        assertEquals(s, "A*");
    }
    
    public void testCharacterSize() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        assertEquals(encoding.getCharSize(), 7);
    }
    
    public void testToString() throws Exception {
        DefaultAlphabetEncoding encoding = getEncodingToTest();
        System.out.println(encoding.toString());
    }
}
