package com.adenki.smpp.encoding;

import java.io.ByteArrayOutputStream;

/**
 * This class encodes and decodes Java Strings to and from the SMS default
 * alphabet. It also supports the default extension table. The default alphabet
 * and it's extension table is defined in GSM 03.38.
 * @version $Id$
 */
public class DefaultAlphabetEncoding extends AlphabetEncoding {
    private static final int DCS = 0;

    public static final int EXTENDED_ESCAPE = 0x1b;

    /** Page break (extended table). */
    public static final int PAGE_BREAK = 0x0a;

    protected final char[] CHAR_TABLE = {
        '@',      '\u00a3', '$',      '\u00a5', '\u00e8', '\u00e9', '\u00f9', '\u00ec',
        '\u00f2', '\u00c7', '\n',     '\u00d8', '\u00f8', '\r',     '\u00c5', '\u00e5',
        '\u0394', '_',      '\u03a6', '\u0393', '\u039b', '\u03a9', '\u03a0', '\u03a8',
        '\u03a3', '\u0398', '\u039e', ' ',      '\u00c6', '\u00e6', '\u00df', '\u00c9',
        ' ',      '!',      '"',      '#',      '\u00a4', '%',      '&',      '\'',
        '(',      ')',      '*',      '+',      ',',      '-',      '.',      '/',
        '0',      '1',      '2',      '3',      '4',      '5',      '6',      '7',
        '8',      '9',      ':',      ';',      '<',      '=',      '>',      '?',
        '\u00a1', 'A',      'B',      'C',      'D',      'E',      'F',      'G',
        'H',      'I',      'J',      'K',      'L',      'M',      'N',      'O',
        'P',      'Q',      'R',      'S',      'T',      'U',      'V',      'W',
        'X',      'Y',      'Z',      '\u00c4', '\u00d6', '\u00d1', '\u00dc', '\u00a7',
        '\u00bf', 'a',      'b',      'c',      'd',      'e',      'f',      'g',
        'h',      'i',      'j',      'k',      'l',      'm',      'n',      'o',
        'p',      'q',      'r',      's',      't',      'u',      'v',      'w',
        'x',      'y',      'z',      '\u00e4', '\u00f6', '\u00f1', '\u00fc', '\u00e0',
    };

    /**
     * Extended character table. Characters in this table are accessed by the
     * 'escape' character in the base table. It is important that none of the
     * 'inactive' characters ever be matchable with a valid base-table
     * character as this breaks the encoding loop.
     * @see #EXTENDED_ESCAPE
     */
    protected final char[] EXT_CHAR_TABLE = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, '^', 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            '{', '}', 0, 0, 0, 0, 0, '\\',
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, '[', '~', ']', 0,
            '|', 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, '\u20ac', 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    /**
     * @see #setUnknownCharReplacement(int)
     */
    private int unknownCharReplacement = 0x3f;
    
    public DefaultAlphabetEncoding() {
        super(DCS);
    }

    /**
     * Set the byte to use when there is no code point for a Unicode character.
     * This byte will be inserted into an encoded byte array if the String
     * being encoded contains a character that the GSM default alphabet
     * has no code point for. The default is to insert the code point for
     * the '?' character - that is, byte 0x3f.
     * @param unknownCharReplacement A code point for one of the characters
     * in the basic character table.
     * @throws IllegalArgumentException If <code>0 &lt; unknownCharReplacement
     * &lt; 127</code> or <code>unknownCharReplacement</code> is <code>0x1b
     * </code> (the extended escape character).
     */
    public void setUnknownCharReplacement(int unknownCharReplacement) {
        if (unknownCharReplacement < 0 || unknownCharReplacement > 127
                || unknownCharReplacement == EXTENDED_ESCAPE) {
            throw new IllegalArgumentException(
                    "Illegal replacement code point " + unknownCharReplacement);
        }
        this.unknownCharReplacement = unknownCharReplacement;
    }
    
    /**
     * Get the current code point in use for unknown characters.
     * @return The current code point in use for unknown characters.
     * @see #setUnknownCharReplacement(int)
     */
    public int getUnknownCharReplacement() {
        return unknownCharReplacement;
    }
    
    /**
     * Decode an SMS default alphabet-encoded octet string into a Java String.
     */
    @Override
    public String decode(byte[] data, int offset, int length) {
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }
        char[] table = CHAR_TABLE;
        StringBuffer buf = new StringBuffer();

        for (int i = offset; i < (offset + length); i++) {
            int code = (int) data[i] & 0x000000ff;
            if (code == EXTENDED_ESCAPE) {
                // take next char from extension table
                table = EXT_CHAR_TABLE;
            } else {
                if (code >= table.length) {
                    code = unknownCharReplacement;
                }
                buf.append(table[code]);
                // Go back to the default table.
                table = CHAR_TABLE;
            }
        }

        return buf.toString();
    }

    /**
     * Encode a Java String into a byte array using the SMS Default alphabet.
     */
    @Override
    public byte[] encode(String s) {
        if (s == null) {
            return new byte[0];
        }

        char[] c = s.toCharArray();
        ByteArrayOutputStream enc = new ByteArrayOutputStream(256);

        for (int loop = 0; loop < c.length; loop++) {
            int search = 0;
            for (; search < CHAR_TABLE.length; search++) {
                if (search == EXTENDED_ESCAPE) {
                    continue;
                }

                if (c[loop] == CHAR_TABLE[search]) {
                    enc.write((byte) search);
                    break;
               }

                if (c[loop] == EXT_CHAR_TABLE[search]) {
                    enc.write((byte) EXTENDED_ESCAPE);
                    enc.write((byte) search);
                    break;
               }
            }
            if (search == CHAR_TABLE.length) {
                enc.write((byte) unknownCharReplacement);
            }
        }

        return enc.toByteArray();
    }

    public int getCharSize() {
        return 7;
    }

    /**
     * Pack a byte array according to the GSM bit-packing algorithm.
     * The GSM specification defines a simple compression mechanism for its
     * default alphabet to pack more message characters into a smaller space.
     * Since the alphabet only contains 128 symbols, each one can be represented
     * in 7 bits. The packing algorithm squeezes the bits for each symbol
     * "down" into the preceeding byte (so bit 7 of the first byte actually
     * contains bit 0 of the second symbol in a default alphabet string, bits
     * 6 and 7 in the second byte contain bits 0 and 1 of the third symbol etc.)
     * Since the maximum short message length is 140 <b>bytes</b>, you save
     * one bit per byte using the default alphabet giving you a total of
     * 140 + (140 / 8) = 160 characters to use. This is where the 160 character
     * limit comes from in SMPP packets.
     * <p>
     * Having said all that, most SMSCs do <b>NOT</b> use the packing
     * algorithm when communicating over TCP/IP. They either use a full
     * 8-bit alphabet such as ASCII or Latin-1, or they accept the default
     * alphabet in its unpacked form. As such, you will be unlikely to
     * need this method.
     * </p>
     * @param unpacked The unpacked byte array. 
     * @return A new byte array containing the bytes in their packed form.
     */
    public byte[] pack(final byte[] unpacked) {
        final int packedLen = unpacked.length - (unpacked.length / 8);
        final byte[] packed = new byte[packedLen];
        if (unpacked.length == 0) {
            return packed;
        }
        // Index into the unpacked array
        int unpIndex = 0;
        // Index into the packed array
        int pIndex = 0;
        int shiftRight = 0;
        for (; pIndex < packed.length; unpIndex++, pIndex++) {
            int shiftLeft = 7 - shiftRight;
            int b = ((int) unpacked[unpIndex] & 0xff) >>> shiftRight;
            if (unpIndex + 1 < unpacked.length) {
                b |= (((int) unpacked[unpIndex + 1]) & 0xff) << shiftLeft;
            }
            packed[pIndex] = (byte) b;
            shiftRight++;
            if (shiftRight > 6) {
                // We've saved a byte
                unpIndex++;
                shiftRight = 0;
            }
        }
        return packed;
    }

    /**
     * Unpack a byte array according to the GSM bit-packing algorithm.
     * Read the full description in the documentation of the
     * <code>pack</code> method.
     * @see #pack(byte[])
     * @param packed The packed byte array.
     * @return A new byte array containing the unpacked bytes.
     */
    public byte[] unpack(final byte[] packed) {
        final int unpackedLen = (packed.length * 8) / 7;
        final byte[] unpacked = new byte[unpackedLen];
        if (packed.length == 0) {
            return unpacked;
        }
        int shiftLeft = 0;
        int unpIndex = 0;
        int pIndex = 0;
        // Now iterate the remaining bytes, shifting as necessary
        for (; pIndex < packed.length; pIndex++, unpIndex++) {
            final int shiftRight = 8 - shiftLeft;
            int b = (((int) packed[pIndex]) << shiftLeft) & 0x7f;
            if (shiftLeft > 0) {
                b |= ((int) (packed[pIndex - 1] & 0xff)) >>> shiftRight;
            }
            unpacked[unpIndex] = (byte) b;
            shiftLeft++;
            if (shiftLeft > 6) {
                // We have a full extra character packed in to the upper 7
                // bits of the current octet.
                shiftLeft = 0;
                unpIndex++;
                b = ((int) packed[pIndex] & 0xff) >>> 1;
                unpacked[unpIndex] = (byte) b;
            }
        }
        return unpacked;
    }
}
