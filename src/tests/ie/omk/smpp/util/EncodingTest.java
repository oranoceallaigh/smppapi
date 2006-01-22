package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.TestCase;

public class EncodingTest extends TestCase {

    public EncodingTest(String s) {
        super(s);
    }

    public void testDefaultAlphabet() {
    }

    public void testASCIIEncoding() {
        // "Test message" in ASCII characters.
        byte[] msg_bytes = {0x54, 0x65, 0x73, 0x74, 0x20, 0x6d, 0x65, 0x73,
                0x73, 0x61, 0x67, 0x65, };

        String msg = "Test message";

        ASCIIEncoding ae = ASCIIEncoding.getInstance();
        assertTrue(Arrays.equals(msg_bytes, ae.encodeString(msg)));
        assertEquals(msg, ae.decodeString(msg_bytes));
    }

    public void testLatinEncoding() {
        // "Test message" followed by:
        // Yen symbol
        // Pound sign (European interpretation, not what I would call a "hash").
        // Superscript 3
        // Latin capital letter AE
        byte[] msg_bytes = {0x54, 0x65, 0x73, 0x74, 0x20, 0x6d, 0x65, 0x73,
                0x73, 0x61, 0x67, 0x65, (byte) 0xa5, (byte) 0xa3, (byte) 0xb3,
                (byte) 0xc6, };

        String msg = "Test message\u00a5\u00a3\u00b3\u00c6";

        Latin1Encoding enc = Latin1Encoding.getInstance();
        assertTrue(Arrays.equals(msg_bytes, enc.encodeString(msg)));
        assertEquals(msg, enc.decodeString(msg_bytes));
    }

    public void testHPRomanEncoding() {

        HPRoman8Encoding enc = HPRoman8Encoding.getInstance();

        // The full character table in a string..
        final String msg =
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

        byte[] b = enc.encodeString(msg);
        String decStr = enc.decodeString(b);

        assertEquals(msg.length(), b.length);
        assertEquals(msg, decStr);
    }

    public void testUTF16Encoding() {
        String msg = "A test message \u00e9 \u00f8 \u49ab";
        
        UTF16Encoding enc = UTF16Encoding.getInstance(true);
        byte[] b = enc.encodeString(msg);
        String decStr = enc.decodeString(b);
    
        assertEquals(msg.length() * 2, b.length);
        assertEquals(msg, decStr);

        enc = UTF16Encoding.getInstance(false);
        b = enc.encodeString(msg);
        decStr = enc.decodeString(b);
    
        assertEquals(msg.length() * 2, b.length);
        assertEquals(msg, decStr);
    }

    public void testUCS2Encoding() {
        try {
            String msg = "A test message \u00e9 \u00f8 \u49ab";
            UCS2Encoding enc = UCS2Encoding.getInstance();
            byte[] b = enc.encodeString(msg);
            String decStr = enc.decodeString(b);
            
            assertEquals(msg.length() * 2, b.length);
            assertEquals(msg, decStr);
        } catch (UnsupportedEncodingException x) {
            System.err.println("Unable to test UCS2Encoding as the JVM does not support the charset.");
        }
    }

    public void testBinaryEncoding() {
    }
}

