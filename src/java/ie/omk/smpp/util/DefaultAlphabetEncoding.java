package ie.omk.smpp.util;

import java.io.ByteArrayOutputStream;

/**
 * This class encodes and decodes Java Strings to and from the SMS default
 * alphabet. It also supports the default extension table. The default alphabet
 * and it's extension table is defined in GSM 03.38.
 */
public class DefaultAlphabetEncoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final int DCS = 0;

    public static final int EXTENDED_ESCAPE = 0x1b;

    /** Page break (extended table). */
    public static final int PAGE_BREAK = 0x0a;

    // XXX Didn't have a Unicode font with greek chars available to see the
    // greek characters in the default alphabet...some of them may be wrong!
    private static final char[] charTable = {'@', '\u00a3', '$', '\u00a5',
            '\u00e8', '\u00e9', '\u00f9', '\u00ec',
            '\u00f2', '\u00c7', '\n', '\u00d8', '\u00f8', '\r', '\u00c5',
            '\u00e5',
            '\u0394', '_', '\u03a6', '\u0393', '\u039b',
            '\u03a9', '\u03a0', '\u03a8',
            '\u03a3', '\u0398', '\u039e', ' ', '\u00c6', '\u00e6', '\u00df',
            '\u00c9',
            ' ', '!', '"', '#', '\u00a4', '%', '&', '\'',
            '(', ')', '*', '+', ',', '-', '.', '/',
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', ':', ';', '<', '=', '>', '?',
            '\u00a1', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '\u00c4', '\u00d6', '\u00d1', '\u00dc', '\u00a7',
            '\u00bf', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '\u00e4', '\u00f6', '\u00f1', '\u00fc', '\u00e0',
    };

    /**
     * Extended character table. Characters in this table are accessed by the
     * 'escape' character in the base table. It is important that none of the
     * 'inactive' characters never be matchable with a valid base-table
     * character as this breaks the encoding loop.
     * 
     * @see #EXTENDED_ESCAPE
     */
    public static char[] extCharTable = {0, 0, 0, 0, 0, 0, 0, 0, // 0 to 7
            0, 0, 0, 0, 0, 0, 0, 0, // 8 to 15
            0, 0, 0, 0, '^', 0, 0, 0, // 16 to 23
            0, 0, 0, 0, 0, 0, 0, 0, // 24 to 31
            0, 0, 0, 0, 0, 0, 0, 0, // 32 to 39
            '{', '}', 0, 0, 0, 0, 0, '\\', // 40 to 47
            0, 0, 0, 0, 0, 0, 0, 0, // 48 to 55
            0, 0, 0, 0, '[', '~', ']', 0, // 56 to 63
            0, 0, 0, 0, 0, 0, 0, 0, // 64 to 71
            0, 0, 0, 0, 0, 0, 0, 0, // 72 to 79
            0, 0, 0, 0, 0, 0, 0, 0, // 80 to 87
            0, 0, 0, 0, 0, 0, 0, 0, // 88 to 95
            0, 0, 0, 0, 0, '\u20ac', 0, 0, // 96 to 103
            0, 0, 0, 0, 0, 0, 0, 0, // 104 to 111
            0, 0, 0, 0, 0, 0, 0, 0, // 112 to 119
            0, 0, 0, 0, 0, 0, 0, 0, // 120 to 127
    };

    private static final DefaultAlphabetEncoding instance = new DefaultAlphabetEncoding();

    public DefaultAlphabetEncoding() {
        super(DCS);
    }

    /**
     * Get the singleton instance of DefaultAlphabetEncoding.
     * @deprecated
     */
    public static DefaultAlphabetEncoding getInstance() {
        return instance;
    }

    /**
     * Decode an SMS default alphabet-encoded octet string into a Java String.
     */
    public String decodeString(byte[] b) {
        if (b == null) {
            return "";
        }

        char[] table = charTable;
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            int code = (int) b[i] & 0x000000ff;
            if (code == EXTENDED_ESCAPE) {
                table = extCharTable; // take next char from extension table
            } else {
                buf.append((code >= table.length) ? '?' : table[code]);
                table = charTable; // Go back to default table.
            }
        }

        return buf.toString();
    }

    /**
     * Encode a Java String into a byte array using the SMS Default alphabet.
     */
    public byte[] encodeString(String s) {
        if (s == null) {
            return new byte[0];
        }

        char[] c = s.toCharArray();
        ByteArrayOutputStream enc = new ByteArrayOutputStream(256);

        for (int loop = 0; loop < c.length; loop++) {
            int search = 0;
            for (; search < charTable.length; search++) {
                if (search == EXTENDED_ESCAPE) {
                    continue;
                }

                if (c[loop] == charTable[search]) {
                    enc.write((byte) search);
                    break;
               }

                if (c[loop] == extCharTable[search]) {
                    enc.write((byte) EXTENDED_ESCAPE);
                    enc.write((byte) search);
                    break;
               }
            }
            if (search == charTable.length) {
                enc.write(0x3f); // A '?'
            }
        }

        return enc.toByteArray();
    }

    public int getEncodingLength() {
        return 7;
    }

    /*
     * private static byte[] unpack(byte[] packed) {int unpackedLen =
     * ((packed.length * 8) / 7); byte[] unpacked = new byte[unpackedLen]; int
     * pos = 0;
     * 
     * int i = 0; while (i < packed.length) {int mask = 0x7f; int jmax = (i +
     * 8) > packed.length ? (packed.length - i) : 8;
     * 
     * for (int j = 0; j < jmax; j++) {int b1 = (int)packed[i + j] & mask; int
     * b2 = 0x0; try {b2 = (int)packed[(i + j) - 1] & 0x00ff;} catch
     * (ArrayIndexOutOfBoundsException x) {}
     * 
     * unpacked[pos++] = (byte)((b1 < < j) | (b2 >>> (8 - j)));
     * 
     * mask >>= 1;} i += 7;} return unpacked;}
     * 
     * private static byte[] pack(byte[] unpacked) {int packedLen =
     * unpacked.length - (unpacked.length / 8); byte[] packed = new
     * byte[packedLen]; int pos = 0;
     * 
     * int i = 0; while (i < unpacked.length) {
     * 
     * int jmax = (i + 7) > unpacked.length ? unpacked.length - i : 7; int mask =
     * 0x1; for (int j = 0; j < jmax; j++) {int b1 = (int)unpacked[i + j] &
     * 0xff; int b2 = 0x0; try {b2 = (int)unpacked[i + j + 1] & mask;} catch
     * (ArrayIndexOutOfBoundsException x) {}
     * 
     * packed[pos++] = (byte)((b1 >>> j) | (b2 < < (8 - (j + 1)))); mask = (mask < <
     * 1) | 1;}
     * 
     * i += 8;}
     * 
     * return packed;}
     */

    public static void main(String[] args) {
        try {
            DefaultAlphabetEncoding alpha = new DefaultAlphabetEncoding();

            if (args.length > 0 && args[0].equals("-dt")) {
                dumpTable();
                System.exit(0);
            }

            String[] s = {"O", "Or", "Ora", "Oran", "OranK",
                    "Oran Kelly testing long string",
                    "[Byte array}should be[ 33 long", };

            for (int i = 0; i < s.length; i++) {
                byte[] enc = alpha.encodeString(s[i]);

                System.out.println("String \"" + s[i] + "\", size: "
                        + s[i].length());
                System.out.println("    ASCII: "
                        + alpha.showByteArray(s[i].getBytes("US-ASCII")));
                System.out.println("  Encoded: " + alpha.showByteArray(enc));
                System.out.println("  Decoded: " + alpha.decodeString(enc));
            }
        } catch (Exception x) {
            x.printStackTrace(System.err);
        }
    }

    public String showByteArray(byte[] b) {
        java.io.StringWriter sw = new java.io.StringWriter();

        for (int i = 0; i < b.length; i++) {
            sw.write(" 0x" + Integer.toHexString((int) b[i] & 0x00ff));
        }

        return sw.toString();
    }

    private static void dumpTable() {
        String fmt1 = "{0,number,###}: {1}  ";
        String fmt2 = "{0,number,###}:{1}  ";

        System.out.println("Table size: " + charTable.length);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                int pos = i + (16 * j);

                if (charTable[pos] == '\r') {
                    Object[] a = {new Integer(pos), "CR"};
                    System.out.print(java.text.MessageFormat.format(fmt2, a));
                    continue;
               } else if (charTable[pos] == '\n') {
                    Object[] a = {new Integer(pos), "LF"};
                    System.out.print(java.text.MessageFormat.format(fmt2, a));
                    continue;
               } else if (charTable[pos] == ' ') {
                    Object[] a = {new Integer(pos), "SP"};
                    System.out.print(java.text.MessageFormat.format(fmt2, a));
                    continue;
               }

                Object[] a = {new Integer(pos), new Character(charTable[pos])};
                System.out.print(java.text.MessageFormat.format(fmt1, a));
            }
            System.out.print("\n");
        }
    }
}

