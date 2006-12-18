package ie.omk.smpp.util;

import java.util.Arrays;

import junit.framework.TestCase;

public class AlphabetEncodingTest extends TestCase {

    public AlphabetEncodingTest(String s) {
        super(s);
    }

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
        
        DefaultAlphabetEncoding encoding = new DefaultAlphabetEncoding();
        byte[] encoded = encoding.encodeString(alphabet);
        String decoded = encoding.decodeString(encoded);
        
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(expectedEncodedLength, encoded.length);
        assertEquals(alphabet, decoded);
    }

    public void testASCIIEncoding() {
        final String alphabet =
            "\t\n\r!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWX"
            + "YZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        final int expectedEncodedLength = alphabet.length();

        ASCIIEncoding encoding = new ASCIIEncoding();
        byte[] encoded = encoding.encodeString(alphabet);
        String decoded = encoding.decodeString(encoded);
        
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(expectedEncodedLength, encoded.length);
        assertEquals(alphabet, decoded);
        
        String nonASCII = "\u00e9\u00e8";
        encoded = encoding.encodeString(nonASCII);
        decoded = encoding.decodeString(encoded);
        assertEquals(nonASCII.length(), encoded.length);
        assertFalse(nonASCII.equals(decoded));
    }

    public void testLatinEncoding() throws Exception {
        // "Test message" followed by:
        // Yen symbol
        // Pound sign (European interpretation, not what I would call a "hash").
        // Superscript 3
        // Latin capital letter AE
        byte[] expectedBytes = {0x54, 0x65, 0x73, 0x74, 0x20, 0x6d, 0x65, 0x73,
                0x73, 0x61, 0x67, 0x65, (byte) 0xa5, (byte) 0xa3, (byte) 0xb3,
                (byte) 0xc6, };
        String string = "Test message\u00a5\u00a3\u00b3\u00c6";

        Latin1Encoding encoding = new Latin1Encoding();
        byte[] encoded = encoding.encodeString(string);
        String decoded = encoding.decodeString(encoded);
        
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertTrue(Arrays.equals(expectedBytes, encoded));
        assertEquals(string, decoded);
        
        String nonLatin1 = "\u20ac";
        encoded = encoding.encodeString(nonLatin1);
        decoded = encoding.decodeString(encoded);
        assertEquals(nonLatin1.length(), encoded.length);
        assertFalse(nonLatin1.equals(decoded));
    }

    public void testHPRomanEncoding() {
        // The full character table in a string..
        final String string =
                  " !\"#$%&,()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRS"
                + "TUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
                + "\u00a0\u00c0\u00c2\u00c8\u00ca\u00cb\u00ce\u00cf\u00b4\u0300"
                + "\u0302\u00a8\u0303\u00d9\u00db\u20a4\u007e\u00dd\u00fd\u00b0"
                + "\u00c7\u00e7\u00d1\u00f1\u00a1\u00bf\u00a4\u00a3\u00a5\u00a7"
                + "\u0192\u00a2\u00e2\u00ea\u00f4\u00fb\u00e1\u00e9\u00f3\u00fa"
                + "\u00e0\u00e8\u00f2\u00f9\u00e4\u00eb\u00f6\u00fc\u00c5\u00ee"
                + "\u00d8\u00c6\u00e5\u00ed\u00f8\u00e6\u00c4\u00ec\u00d6\u00dc"
                + "\u00c9\u00ef\u00df\u00d4\u00c1\u00c3\u00e3\u00d0\u00f0\u00cd"
                + "\u00cc\u00d3\u00d2\u00d5\u00f5\u00a6\u00a8\u00da\u00be\u00ff"
                + "\u00de\u00fe\u00b7\u00b5\u00b6\u00be\u00ad\u00bc\u00bd\u00aa"
                + "\u00ba\u00ab\u25a0\u00bb\u00b1";

        HPRoman8Encoding encoding = new HPRoman8Encoding();
        byte[] encoded = encoding.encodeString(string);
        String decoded = encoding.decodeString(encoded);

        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(string.length(), encoded.length);
        assertEquals(string, decoded);
    }

    public void testUTF16Encoding() throws Exception {
        final String string = getUnicodeString();
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

    public void testUCS2Encoding() throws Exception {
        final String string = getUnicodeString();
        UCS2Encoding encoding = new UCS2Encoding();
        byte[] encoded = encoding.encodeString(string);
        String decoded = encoding.decodeString(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(string.length() * 2, encoded.length);
        assertEquals(string, decoded);
    }

    private String getUnicodeString() {
        StringBuffer buf = new StringBuffer(1000);
        for (int i = 33; buf.length() < 1000; i++) {
            if (Character.isLetterOrDigit((char) i)
                    || Character.isSpaceChar((char) i)) {
                buf.append((char) i);
            }
        }
        return buf.toString();
    }
}

