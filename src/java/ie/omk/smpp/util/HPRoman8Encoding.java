package ie.omk.smpp.util;

import java.io.ByteArrayOutputStream;

/**
 * Encoding class representing the HP-Roman8 character set.
 * 
 * @version $Id$
 */
public final class HPRoman8Encoding extends AlphabetEncoding {

    /**
     * Data coding value. There isn't an 'official' value for HP-Roman8. Usually
     * it is the default encoding of the SMSC.
     */
    private static final int DCS = 0;

    private static final HPRoman8Encoding instance = new HPRoman8Encoding();

    private static final char[] CHAR_TABLE = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            ' ', '!', '"', '#', '$', '%', '&', '\'',
            '(', ')', '*', '+', ',', '-', '.', '/',
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', ':', ';', '<', '=', '>', '?',
            '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
            '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '{', '|', '}', '~', 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            '\u00a0', '\u00c0', '\u00c2', '\u00c8', '\u00ca', '\u00cb',
            '\u00ce', '\u00cf',
            '\u00b4', '\u0300', '\u0302', '\u00a8', '\u0303', '\u00d9',
            '\u00db', '\u20a4',
            '\u007e', '\u00dd', '\u00fd', '\u00b0', '\u00c7', '\u00e7',
            '\u00d1', '\u00f1',
            '\u00a1', '\u00bf', '\u00a4', '\u00a3', '\u00a5', '\u00a7',
            '\u0192', '\u00a2',
            '\u00e2', '\u00ea', '\u00f4', '\u00fb', '\u00e1', '\u00e9',
            '\u00f3', '\u00fa',
            '\u00e0', '\u00e8', '\u00f2', '\u00f9', '\u00e4', '\u00eb',
            '\u00f6', '\u00fc',
            '\u00c5', '\u00ee', '\u00d8', '\u00c6', '\u00e5', '\u00ed',
            '\u00f8', '\u00e6',
            '\u00c4', '\u00ec', '\u00d6', '\u00dc', '\u00c9', '\u00ef',
            '\u00df', '\u00d4',
            '\u00c1', '\u00c3', '\u00e3', '\u00d0', '\u00f0', '\u00cd',
            '\u00cc', '\u00d3',
            '\u00d2', '\u00d5', '\u00f5', '\u00a6', '\u00a8', '\u00da',
            '\u00be', '\u00ff',
            '\u00de', '\u00fe', '\u00b7', '\u00b5', '\u00b6', '\u00be',
            '\u00ad', '\u00bc',
            '\u00bd', '\u00aa', '\u00ba', '\u00ab', '\u25a0', '\u00bb',
            '\u00b1',
    };

    private HPRoman8Encoding() {
        super(DCS);
    }

    public static HPRoman8Encoding getInstance() {
        return instance;
    }

    public String decodeString(byte[] b) {
        if (b == null) {
            return "";
        }

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            int code = (int) b[i] & 0x000000ff;
            buf.append((code >= CHAR_TABLE.length) ? '?' : CHAR_TABLE[code]);
        }

        return buf.toString();
    }

    public byte[] encodeString(String s) {
        if (s == null) {
            return new byte[0];
        }

        char[] c = s.toCharArray();
        ByteArrayOutputStream enc = new ByteArrayOutputStream(256);

        for (int loop = 0; loop < c.length; loop++) {

            int search = 0;
            for (; search < CHAR_TABLE.length; search++) {

                if (c[loop] == CHAR_TABLE[search]) {
                    enc.write((byte) search);
                    break;
               }
            }
            if (search == CHAR_TABLE.length) {
                enc.write(0x3f); // A '?'
            }
        }

        return enc.toByteArray();
    }
}

