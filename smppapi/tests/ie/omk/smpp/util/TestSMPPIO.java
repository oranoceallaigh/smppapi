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

import ie.omk.TestUtils;

public class TestSMPPIO extends TestCase {

    long[] longs1 = {
	0x11L,
	0x2211L,
	0x332211L,
	0x44332211L,
	0x5544332211L,
	0x665544332211L,
	0x77665544332211L,
	0x8877665544332211L
    };

    long[] longs2 = {
	0xe1L,
	0xe2e1L,
	0xe3e2e1L,
	0xe4e3e2e1L,
	0xe5e4e3e2e1L,
	0xe6e5e4e3e2e1L,
	0xe7e6e5e4e3e2e1L,
	0xe8e7e6e5e4e3e2e1L,
    };

    // Initialised in setUp.
    int[] ints1, ints2;

    byte[][] bytes1 = {
	{ 0x11 },
	{ 0x22, 0x11 },
	{ 0x33, 0x22, 0x11 },
	{ 0x44, 0x33, 0x22, 0x11 },
	{ 0x55, 0x44, 0x33, 0x22, 0x11 },
	{ 0x66, 0x55, 0x44, 0x33, 0x22, 0x11 },
	{ 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11 },
	{ (byte)0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11 }
    };

    byte[][] bytes2 = {
	{ (byte)0xe1 },
	{ (byte)0xe2, (byte)0xe1 },
	{ (byte)0xe3, (byte)0xe2, (byte)0xe1 },
	{ (byte)0xe4, (byte)0xe3, (byte)0xe2, (byte)0xe1 },
	{ (byte)0xe5, (byte)0xe4, (byte)0xe3, (byte)0xe2, (byte)0xe1 },
	{ (byte)0xe6, (byte)0xe5, (byte)0xe4, (byte)0xe3, (byte)0xe2,
	    (byte)0xe1 },
	{ (byte)0xe7, (byte)0xe6, (byte)0xe5, (byte)0xe4, (byte)0xe3,
	    (byte)0xe2, (byte)0xe1 },
	{ (byte)0xe8, (byte)0xe7, (byte)0xe6, (byte)0xe5, (byte)0xe4,
	    (byte)0xe3, (byte)0xe2, (byte)0xe1 }
    };


    public TestSMPPIO(String n) {
	super (n);
    }

    protected void setUp() {
	ints1 = new int[4];
	ints2 = new int[4];

	for (int i = 0; i < 4; i++) {
	    ints1[i] = (int)longs1[i];
	    ints2[i] = (int)longs2[i];
	}
    }

    protected void tearDown() {
	// Nothing to tear down.
    }

    public void testIntToBytes() {
	byte[] b;

	for (int i = 1; i <= 4; i++) {
	    String msg = "" + i + ":";

	    b = new byte[i];
	    SMPPIO.intToBytes(ints1[i - 1], i, b, 0);
	    assertTrue(msg + "1", Arrays.equals(bytes1[i - 1], b));

	    SMPPIO.intToBytes(ints2[i - 1], i, b, 0);
	    TestUtils.displayArray(bytes2[0]);
	    TestUtils.displayArray(b);
	    assertTrue(msg + "2", Arrays.equals(bytes2[i - 1], b));
	}
    }

    public void testBytesToInt() {
	for (int i = 1; i < 4; i++) {
	    assertEquals(ints1[i - 1], SMPPIO.bytesToInt(bytes1[i - 1], 0, i));
	    assertEquals(ints2[i - 1], SMPPIO.bytesToInt(bytes2[i - 1], 0, i));
	}
    }

    public void testLongToBytes() {
	byte[] b;

	for (int i = 1; i <= 8; i++) {
	    b = new byte[i];
	    SMPPIO.longToBytes(longs1[i - 1], i, b, 0);
	    assertTrue(Arrays.equals(bytes1[i - 1], b));

	    SMPPIO.longToBytes(longs2[i - 1], i, b, 0);
	    assertTrue(Arrays.equals(bytes2[i - 1], b));
	}
    }

    public void testBytesToLong() {
	for (int i = 1; i < 8; i++) {
	    String msg = "" + i + ":";
	    assertEquals(msg + "1", longs1[i - 1],
		    SMPPIO.bytesToLong(bytes1[i - 1], 0, i));
	    assertEquals(msg + "2", longs2[i - 1],
		    SMPPIO.bytesToLong(bytes2[i - 1], 0, i));
	}
    }
}
