/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */

package ie.omk.smpp.util;

import java.io.ByteArrayOutputStream;

/** This class encodes and decodes Java Strings to and from the SMS default
  * alphabet.
  * It supports the default extension table too.
  */
public class DefaultAlphabetExt
    implements ie.omk.smpp.util.SMSAlphabet
{
    public static final int SPACE = 0x20;

    public static final int CARRIAGE_RETURN = 0x0d;

    public static final int LINE_FEED = 0x0a;

    public static final int EXTENDED_ESCAPE = 0x1b;

    public static final int PAGE_BREAK = 0x0a;


    // XXX Missing all the Greek characters!
    private static final char[] charTable = {
	'@', '£', '$', '¥', 'è', 'é', 'ù', 'ì', 'ò', 'Ç', '\n', 'Ø', 'ø', '\r',
	'Å', 'å', 'x', '_', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
	'Æ', 'æ', 'ß', 'É', ' ', '!', '"', '#', '¤', '%', '&', '\'', '(', ')',
	'*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7',
	'8', '9', ':', ';', '<', '=', '>', '?', '¡', 'A', 'B', 'C', 'D', 'E',
	'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
	'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ñ', 'Ü', '§', '¿', 'a',
	'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
	'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ñ',
	'ü', 'à'
    };


    private static char[] extCharTable = new char[charTable.length];

    static {
	// Initialise the extended table:
	for (int i = 0; i < extCharTable.length; i++)
	    extCharTable[i] = (char)0;

	extCharTable[0x14] = '^';
	extCharTable[0x28] = '{';
	extCharTable[0x29] = '}';
	extCharTable[0x2f] = '\\';
	extCharTable[0x3c] = '[';
	extCharTable[0x3d] = '~';
	extCharTable[0x3e] = ']';
	extCharTable[0x65] = 'x'; // XXX EURO
    }


    /** Decode a byte array into a Java String.
      */
    public String decodeString(byte[] b)
    {
	char[] table = charTable;

	byte[] unpacked = unpack(b);
	StringBuffer buf = new StringBuffer();

	for (int i = 0; i < unpacked.length; i++) {
	    int code = (int)unpacked[i] & 0x000000ff;
	    if (code == EXTENDED_ESCAPE) {
		table = extCharTable; // take next char from extension table
	    } else {
		buf.append((code >= table.length) ? '?' : table[code]);
		table = charTable; // Go back to default table.
	    }
	}

	return (buf.toString());
    }


    /** Encode and pack a Java String into a byte array using the SMS Default
      * alphabet.
      */
    public byte[] encodeString(String s)
    {
	char[] c = s.toCharArray();
	ByteArrayOutputStream enc = new ByteArrayOutputStream();

	for (int loop = 0; loop < c.length; loop++) {
	    int search = 0;
	    for (; search < charTable.length; search++) {
		if (c[loop] == charTable[search]) {
		    enc.write((byte)search);
		    break;
		}

		if (c[loop] == extCharTable[search]) {
		    enc.write((byte)EXTENDED_ESCAPE);
		    enc.write((byte)search);
		    break;
		}
	    }
	    if (search == charTable.length)
		enc.write((byte)SPACE);
	}

	return (pack(enc.toByteArray()));
    }

    /** Get the data_coding value for the Default alphabet.
      * The code value is '0' for the default alphabet.
      */
    public int getDataCoding()
    {
	return (0);
    }


    private static byte[] unpack(byte[] packed)
    {
	int unpackedLen = ((packed.length * 8) / 7);
	byte[] unpacked = new byte[unpackedLen];
	int pos = 0;

	int i = 0;
	while (i < packed.length) {
	    int mask = 0x7f;
	    int jmax = (i + 8) > packed.length ? (packed.length - i) : 8;

	    for (int j = 0; j < jmax; j++) {
		int b1 = (int)packed[i + j] & mask;
		int b2 = 0x0;
		try {
		    b2 = (int)packed[(i + j) - 1] & 0x00ff;
		} catch (ArrayIndexOutOfBoundsException x) {
		}

		unpacked[pos++] =
		    (byte)((b1 << j) | (b2 >>> (8 - j)));

		mask >>= 1;
	    }
	    i += 7;
	}
	return (unpacked);
    }

    private static byte[] pack(byte[] unpacked)
    {
	int packedLen = unpacked.length - (unpacked.length / 8);
	byte[] packed = new byte[packedLen];
	int pos = 0;

	int i = 0;
	while (i < unpacked.length) {

	    int jmax = (i + 7) > unpacked.length
		? unpacked.length - i : 7;
	    int mask = 0x1;
	    for (int j = 0; j < jmax; j++) {
		int b1 = (int)unpacked[i + j] & 0xff;
		int b2 = 0x0;
		try {
		    b2 = (int)unpacked[i + j + 1] & mask;
		} catch (ArrayIndexOutOfBoundsException x) {
		}

		packed[pos++] = (byte)((b1 >>> j) | (b2 << (8 - (j + 1))));
		mask = (mask << 1) | 1;
	    }

	    i += 8;
	}

	return (packed);
    }



    /*public static void main(String[] args)
    {
	try {
	    DefaultAlphabetExt alpha = new DefaultAlphabetExt();

	    if (args.length > 0 && args[0].equals("-dt")) {
		dumpTable();
		System.exit(0);
	    }

	    String[] s = {
		"O",
		"Or",
		"Ora",
		"Oran",
		"OranK",
		"Oran Kelly testing long string",
		"[Byte array}should be[ 33 long"
	    };

	    for (int i = 0; i < s.length; i++) {
		byte[] enc = alpha.encodeString(s[i]);

		System.out.println("String \"" + s[i] + "\", size: "
			+ s[i].length());
		System.out.println("    ASCII: " + alpha.showByteArray(
			    s[i].getBytes("US-ASCII")));
		System.out.println("  Encoded: " + alpha.showByteArray(enc));
		System.out.println("  Decoded: " + alpha.decodeString(enc));
	    }
	} catch (Exception x) {
	    x.printStackTrace(System.err);
	}
    }

    private String showByteArray(byte[] b)
    {
	java.io.StringWriter sw = new java.io.StringWriter();

	for (int i = 0; i < b.length; i++)
	    sw.write(" 0x" + Integer.toHexString((int)b[i]&0x00ff));

	return (sw.toString());
    }

    private static void dumpTable()
    {
	System.out.println("Table size: " + charTable.length);
	for (int i = 0; i < 0xf; i++) {
	    for (int j = 0; j < 8; j++) {
		System.out.print(" " + (i + (16 * j))
			+ ":" + charTable[i + (16 * j)]);
	    }
	    System.out.print("\n");
	}
    }*/
}
