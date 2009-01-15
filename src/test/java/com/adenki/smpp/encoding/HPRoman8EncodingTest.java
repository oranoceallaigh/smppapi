package com.adenki.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

/**
 * @version $Id:$
 */
@Test
public class HPRoman8EncodingTest extends BaseAlphabetEncodingTest<HPRoman8Encoding> {
    // Almost the full character table in a string..starting from index 32
    // anyway.
    private static final String ALPHABET =
              " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRS"
            + "TUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
            + "\u00a0\u00c0\u00c2\u00c8\u00ca\u00cb\u00ce\u00cf"
            + "\u00b4\u02cb\u02c6\u00a8\u02dc\u00d9\u00db\u20a4"
            + "\u00af\u00dd\u00fd\u00b0\u00c7\u00e7\u00d1\u00f1"
            + "\u00a1\u00bf\u00a4\u00a3\u00a5\u00a7\u0192\u00a2"
            + "\u00e2\u00ea\u00f4\u00fb\u00e1\u00e9\u00f3\u00fa"
            + "\u00e0\u00e8\u00f2\u00f9\u00e4\u00eb\u00f6\u00fc"
            + "\u00c5\u00ee\u00d8\u00c6\u00e5\u00ed\u00f8\u00e6"
            + "\u00c4\u00ec\u00d6\u00dc\u00c9\u00ef\u00df\u00d4"
            + "\u00c1\u00c3\u00e3\u00d0\u00f0\u00cd\u00cc\u00d3"
            + "\u00d2\u00d5\u00f5\u0160\u0161\u00da\u0178\u00ff"
            + "\u00de\u00fe\u00b7\u00b5\u00b6\u00be\u2014\u00bc"
            + "\u00bd\u00aa\u00ba\u00ab\u25a0\u00bb\u00b1";
    
    private static final int[] BYTES = {
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
        0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0xa0,
        0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8,
        0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0,
        0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8,
        0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0,
        0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8,
        0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0,
        0xd1, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8,
        0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0,
        0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8,
        0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0,
        0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
        0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 
    };

    public void testAlternateReplacementChar() throws Exception {
        HPRoman8Encoding encoding = new HPRoman8Encoding();
        // Use '!'
        encoding.setUnknownCharReplacement(0x21);
        assertEquals(encoding.getUnknownCharReplacement(), 0x21);
        byte[] encoded = encoding.encode("\u20ac");
        assertNotNull(encoded);
        assertEquals(encoded.length, 1);
        assertEquals(encoded[0], (byte) 0x21);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testNegativeAlternateReplacementChar() throws Exception {
        HPRoman8Encoding encoding = new HPRoman8Encoding();
        encoding.setUnknownCharReplacement(-2);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testIllegalAlternateReplacementChar() throws Exception {
        HPRoman8Encoding encoding = new HPRoman8Encoding();
        encoding.setUnknownCharReplacement(300);
    }
    
    @Override
    protected TestData getArrayToDecode() {
        return new TestData(ALPHABET, BYTES);
    }


    @Override
    protected HPRoman8Encoding getEncodingToTest() throws UnsupportedEncodingException {
        return new HPRoman8Encoding();
    }


    @Override
    protected TestData getFullySupportedStringToEncode() {
        return new TestData(BYTES, ALPHABET);
    }


    @Override
    protected TestData getPartiallySupportedStringToEncode() {
        String string = "Unsupported character: \u20ac";
        int[] expectedBytes = new int[] {
                85, 110, 115, 117, 112, 112, 111, 114,
                116, 101, 100, 32, 99, 104, 97, 114,
                97, 99, 116, 101, 114, 58, 32, 63,
        };
        return new TestData(expectedBytes, string);
    }
}
