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
 *
 * $Id$
 */
package ie.omk.smpp.util;

import java.util.Arrays;

import junit.framework.TestCase;

public class EncodingTest extends TestCase {

    public EncodingTest(String s) {
	super (s);
    }

    public void testDefaultAlphabet() {
    }

    public void testASCIIEncoding() {
	// "Test message" in ASCII characters.
	byte[] msg_bytes = {
	    0x54, 0x65, 0x73, 0x74, 0x20, 0x6d, 0x65, 0x73, 0x73,
	    0x61, 0x67, 0x65
	};

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
	byte[] msg_bytes = {
	    0x54, 0x65, 0x73, 0x74, 0x20, 0x6d, 0x65, 0x73, 0x73,
	    0x61, 0x67, 0x65, (byte)0xa5, (byte)0xa3, (byte)0xb3, (byte)0xc6
	};

	String msg = "Test message¥£³Æ";

	Latin1Encoding enc = Latin1Encoding.getInstance();
	assertTrue(Arrays.equals(msg_bytes, enc.encodeString(msg)));
	assertEquals(msg, enc.decodeString(msg_bytes));
    }

    public void testUCS2Encoding() {
    }

    public void testBinaryEncoding() {
    }
}
