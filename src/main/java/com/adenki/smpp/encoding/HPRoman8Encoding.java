package com.adenki.smpp.encoding;

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

    private static final char[] CHAR_TABLE = {
        '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
        '\b',     '\t',     '\n',     '\u000b', '\f',     '\r',     '\u000e', '\u000f',
        '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017',
        '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f',
        ' ',      '!',      '"',      '#',      '$',      '%',      '&',      '\'',
        '(',      ')',      '*',      '+',      ',',      '-',      '.',      '/',
        '0',      '1',      '2',      '3',      '4',      '5',      '6',      '7',
        '8',      '9',      ':',      ';',      '<',      '=',      '>',      '?',
        '@',      'A',      'B',      'C',      'D',      'E',      'F',      'G',
        'H',      'I',      'J',      'K',      'L',      'M',      'N',      'O',
        'P',      'Q',      'R',      'S',      'T',      'U',      'V',      'W',
        'X',      'Y',      'Z',      '[',      '\\',     ']',     '^',     '_',
        '`',      'a',      'b',      'c',      'd',      'e',      'f',      'g',
        'h',      'i',      'j',      'k',      'l',      'm',      'n',      'o',
        'p',      'q',      'r',      's',      't',      'u',      'v',      'w',
        'x',      'y',      'z',      '{',      '|',      '}',      '~',      0,
        0,        0,        0,        0,        0,        0,        0,        0,
        0,        0,        0,        0,        0,        0,        0,        0,
        0,        0,        0,        0,        0,        0,        0,        0,
        0,        0,        0,        0,        0,        0,        0,        0,
        '\u00a0', '\u00c0', '\u00c2', '\u00c8', '\u00ca', '\u00cb', '\u00ce', '\u00cf',
        '\u00b4', '\u02cb', '\u02c6', '\u00a8', '\u02dc', '\u00d9', '\u00db', '\u20a4',
        '\u00af', '\u00dd', '\u00fd', '\u00b0', '\u00c7', '\u00e7', '\u00d1', '\u00f1',
        '\u00a1', '\u00bf', '\u00a4', '\u00a3', '\u00a5', '\u00a7', '\u0192', '\u00a2',
        '\u00e2', '\u00ea', '\u00f4', '\u00fb', '\u00e1', '\u00e9', '\u00f3', '\u00fa',
        '\u00e0', '\u00e8', '\u00f2', '\u00f9', '\u00e4', '\u00eb', '\u00f6', '\u00fc',
        '\u00c5', '\u00ee', '\u00d8', '\u00c6', '\u00e5', '\u00ed', '\u00f8', '\u00e6',
        '\u00c4', '\u00ec', '\u00d6', '\u00dc', '\u00c9', '\u00ef', '\u00df', '\u00d4',
        '\u00c1', '\u00c3', '\u00e3', '\u00d0', '\u00f0', '\u00cd', '\u00cc', '\u00d3',
        '\u00d2', '\u00d5', '\u00f5', '\u0160', '\u0161', '\u00da', '\u0178', '\u00ff',
        '\u00de', '\u00fe', '\u00b7', '\u00b5', '\u00b6', '\u00be', '\u2014', '\u00bc',
        '\u00bd', '\u00aa', '\u00ba', '\u00ab', '\u25a0', '\u00bb', '\u00b1',
    };

    private int unknownCharReplacement = 0x3f;
    
    public HPRoman8Encoding() {
        super(DCS);
    }

    public int getUnknownCharReplacement() {
        return unknownCharReplacement;
    }

    /**
     * Set the byte to use when there is no code point for a Unicode character.
     * This byte will be inserted into an encoded byte array if the String
     * being encoded contains a character that HPRoman8
     * has no code point for. The default is to insert the code point for
     * the '?' character - that is, byte 0x3f.
     * @param unknownCharReplacement A code point for one of the characters
     * in the Roman8 character table.
     * @throws IllegalArgumentException If <code>0 &lt; unknownCharReplacement
     * &lt; 256</code> or <code>unknownCharReplacement</code> is <code>0x1b
     * </code>.
     */
    public void setUnknownCharReplacement(int unknownCharReplacement) {
        if (unknownCharReplacement < 0 || unknownCharReplacement > 255) {
            throw new IllegalArgumentException(
                    "Replacement code point is out of bounds.");
        }
        this.unknownCharReplacement = unknownCharReplacement;
    }

    @Override
    public String decode(byte[] data, int offset, int length) {
        if (data == null) {
            throw new NullPointerException("Data cannot be null.");
        }
        StringBuilder buf = new StringBuilder();
        int endIndex = offset + length;
        for (int i = offset; i < endIndex; i++) {
            int code = (int) data[i] & 0x000000ff;
            buf.append(CHAR_TABLE[code]);
        }
        return buf.toString();
    }
    
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

                if (c[loop] == CHAR_TABLE[search]) {
                    enc.write((byte) search);
                    break;
               }
            }
            if (search == CHAR_TABLE.length) {
                enc.write(unknownCharReplacement);
            }
        }
        return enc.toByteArray();
    }
}
