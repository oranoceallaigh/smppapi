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

import ie.omk.smpp.util.SMPPIO;

import java.util.Arrays;

import junit.framework.TestCase;

public class TestSMPPIO extends TestCase {

    int i1 = 0x11;
    byte[] b1 = { 0x11 };

    int i2 = 0x1122;
    byte[] b2 = { 0x11, 0x22 };

    int i3 = 0x112233;
    byte[] b3 = { 0x11, 0x22, 0x33 };

    int i4 = 0x11223344;
    byte[] b4 = { 0x11, 0x22, 0x33, 0x44 };

    long l5 = 0x1122334455L;
    byte[] b5 = { 0x11, 0x22, 0x33, 0x44, 0x55 };

    long l6 = 0x112233445566L;
    byte[] b6 = { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66 };

    long l7 = 0x11223344556677L;
    byte[] b7 = { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77 };

    long l8 = 0x1122334455667788L;
    byte[] b8 = { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte)0x88 };

    public TestSMPPIO(String n) {
	super (n);
    }

    public void testIntToBytes() {
	byte[] b;

	b = new byte[1];
	SMPPIO.intToBytes(i1, 1, b, 0);
	assertTrue(Arrays.equals(b1, b));

	b = new byte[2];
	SMPPIO.intToBytes(i2, 2, b, 0);
	assertTrue(Arrays.equals(b2, b));

	b = new byte[3];
	SMPPIO.intToBytes(i3, 3, b, 0);
	assertTrue(Arrays.equals(b3, b));

	b = new byte[4];
	SMPPIO.intToBytes(i4, 4, b, 0);
	assertTrue(Arrays.equals(b4, b));
    }

    public void testBytesToInt() {
	assertEquals(i1, SMPPIO.bytesToInt(b1, 0, 1));
	assertEquals(i2, SMPPIO.bytesToInt(b2, 0, 2));
	assertEquals(i3, SMPPIO.bytesToInt(b3, 0, 3));
	assertEquals(i4, SMPPIO.bytesToInt(b4, 0, 4));
    }

    public void testLongToBytes() {
	byte[] b;

	b = new byte[1];
	SMPPIO.longToBytes((long)i1, 1, b, 0);
	assertTrue(Arrays.equals(b1, b));

	b = new byte[2];
	SMPPIO.longToBytes((long)i2, 2, b, 0);
	assertTrue(Arrays.equals(b2, b));
	
	b = new byte[3];
	SMPPIO.longToBytes((long)i3, 3, b, 0);
	assertTrue(Arrays.equals(b3, b));
	
	b = new byte[4];
	SMPPIO.longToBytes((long)i4, 4, b, 0);
	assertTrue(Arrays.equals(b4, b));
	
	b = new byte[5];
	SMPPIO.longToBytes(l5, 5, b, 0);
	assertTrue(Arrays.equals(b5, b));
	
	b = new byte[6];
	SMPPIO.longToBytes(l6, 6, b, 0);
	assertTrue(Arrays.equals(b6, b));
	
	b = new byte[7];
	SMPPIO.longToBytes(l7, 7, b, 0);
	assertTrue(Arrays.equals(b7, b));
	
	b = new byte[8];
	SMPPIO.longToBytes(l8, 8, b, 0);
	assertTrue(Arrays.equals(b8, b));
    }

    public void testBytesToLong() {
	assertEquals((long)i1, SMPPIO.bytesToLong(b1, 0, 1));
	assertEquals((long)i2, SMPPIO.bytesToLong(b2, 0, 2));
	assertEquals((long)i3, SMPPIO.bytesToLong(b3, 0, 3));
	assertEquals((long)i4, SMPPIO.bytesToLong(b4, 0, 4));
	assertEquals(l5, SMPPIO.bytesToLong(b5, 0, 5));
	assertEquals(l6, SMPPIO.bytesToLong(b6, 0, 6));
	assertEquals(l7, SMPPIO.bytesToLong(b7, 0, 7));
	assertEquals(l8, SMPPIO.bytesToLong(b8, 0, 8));
    }
}
