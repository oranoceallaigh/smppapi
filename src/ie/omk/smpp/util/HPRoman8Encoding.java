/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
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
 * $Id$
 */

package ie.omk.smpp.util;

import java.io.ByteArrayOutputStream;

/** Encoding class representing the HP-Roman8 character set.
 */
public class HPRoman8Encoding extends ie.omk.smpp.util.AlphabetEncoding {

    /** Data coding value. There isn't an 'official' value for HP-Roman8.
     * Usually it is the default encoding of the SMSC.
     */
    private static final int DCS = 0;

    private static final HPRoman8Encoding instance = new HPRoman8Encoding();
 

    private static final char[] charTable = {
	0,   0,   0,   0,   0,   0,   0,   0,			    // 0
	0,   0,   0,   0,   0,   0,   0,   0,			    // 8
	0,   0,   0,   0,   0,   0,   0,   0,			    // 16
	0,   0,   0,   0,   0,   0,   0,   0,			    // 24
	' ', '!', '"', '#', '$', '%', '&', '\'',		    // 32
	'(', ')', '*', '+', ',', '-', '.', '/',			    // 40
	'0', '1', '2', '3', '4', '5', '6', '7',			    // 48
	'8', '9', ':', ';', '<', '=', '>', '?',			    // 56
	'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',			    // 64
	'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',			    // 72
	'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',			    // 80
	'X', 'Y', 'Z', '[', '\\', ']', '^', '_',		    // 88
	'`', 'a', 'b', 'c', 'd', 'e', 'f', 'g',                     // 96
	'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',                     // 104
	'p', 'q', 'r', 's', 't', 'u', 'v', 'w',                     // 112
	'x', 'y', 'z', '{', '|', '}', '~', 0,			    // 120
	0,   0,   0,   0,   0,   0,   0,   0,			    // 128
	0,   0,   0,   0,   0,   0,   0,   0,                       // 136
	0,   0,   0,   0,   0,   0,   0,   0,                       // 144
	0,   0,   0,   0,   0,   0,   0,   0,                       // 152
	'\u00a0', 'À', 'Â', 'È', 'Ê', 'Ë', 'Î', 'Ï',                // 160
	'\u00b4', '\u0300', '\u0302', '\u00a8', '\u0303', 'Ù', 'Û', '\u20a4',
	'\u007e', 'Ý', 'ý', '°', 'Ç', 'ç', 'Ñ', 'ñ',                // 176
	'¡', '¿', '¤', '£', '¥', '§', '\u0192', '¢',                // 184
	'â', 'ê', 'ô', 'û', 'á', 'é', 'ó', 'ú',			    // 192
	'à', 'è', 'ò', 'ù', 'ä', 'ë', 'ö', 'ü',                     // 200
	'Å', 'î', 'Ø', 'Æ', 'å', 'í', 'ø', 'æ',                     // 208
	'Ä', 'ì', 'Ö', 'Ü', 'É', 'ï', 'ß', 'Ô',                     // 216
	'Á', 'Ã', 'ã', 'Ð', 'ð', 'Í', 'Ì', 'Ó',                     // 224
	'Ò', 'Õ', 'õ', '¦', '¨', 'Ú', '¾', 'ÿ',                     // 232
	'Þ', 'þ', '·', 'µ', '¶', '\u00be', '­', '\u00bc',           // 240
	'\u00bd', 'ª', 'º', '«', '\u25a0', '»', '±'                 // 248
    };


    private HPRoman8Encoding() {
	super (DCS);
    }

    public static HPRoman8Encoding getInstance() {
	return (instance);
    }

    public String decodeString(byte[] b) {
	if (b == null)
	    return ("");

	StringBuffer buf = new StringBuffer();

	for (int i = 0; i < b.length; i++) {
	    int code = (int)b[i] & 0x000000ff;
	    buf.append((code >= charTable.length) ? '?' : charTable[code]);
	}

	return (buf.toString());
    }


    public byte[] encodeString(String s) {
	if (s == null)
	    return (new byte[0]);

	char[] c = s.toCharArray();
	ByteArrayOutputStream enc = new ByteArrayOutputStream(256);

	for (int loop = 0; loop < c.length; loop++) {

	    int search = 0;
	    for (; search < charTable.length; search++) {

		if (c[loop] == charTable[search]) {
		    enc.write((byte)search);
		    break;
		}
	    }
	    if (search == charTable.length)
		enc.write(0x3f); // A '?'
	}

	return (enc.toByteArray());
    }
}
