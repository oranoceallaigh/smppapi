package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import ie.omk.smpp.encoding.HPRoman8Encoding;

import org.testng.annotations.Test;

/**
 * @version $Id:$
 */
@Test
public class HPRoman8EncodingTest {
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

    
    public void testHPRomanEncoding() {
        HPRoman8Encoding encoding = new HPRoman8Encoding();
        byte[] encoded = encoding.encode(ALPHABET);
        String decoded = encoding.decode(encoded);
        assertNotNull(encoded);
        assertNotNull(decoded);
        assertEquals(encoded.length, ALPHABET.length());
        assertEquals(decoded, ALPHABET);
        // The ALPHABET string characters must be in exact order of the
        // character table. 0x80 - 0x9f are unused entries.
        for (int i = 0x20; i <= 0x7e; i++) {
            assertEquals((int) encoded[i - 0x20] & 0xff, i);
        }
        for (int i = 0xa0; i <= 0xfe; i++) {
            assertEquals((int) encoded[i - 0x41] & 0xff, i);
        }
    }
}
