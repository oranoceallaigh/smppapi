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
package ie.omk.smpp.message.tlv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import junit.framework.TestCase;

import ie.omk.TestUtils;

public class TLVTests extends TestCase {

    BitSet bitSet = new BitSet();
    byte[] bitSetExpected;

    public TLVTests() {
	super ("TLV Tests");
    }

    public TLVTests(String n) {
	super (n);
    }

    public void setUp() {
	// Set up a BitSet to use.
	bitSet.set(2);
	bitSet.set(6);
	bitSet.set(5);
	bitSet.set(7);
	bitSet.set(12);
	bitSet.set(14);

	bitSetExpected = new byte[] {
	    (byte)0xe4, (byte)0x50
	};
    }

    public void testTag() {
	Tag testTag = Tag.SMS_SIGNAL;
	int testTagVal = 0x1203;

	assertEquals(testTagVal, testTag.getTag());
	assertEquals(testTagVal, testTag.intValue());
	
	assertSame(testTag, Tag.getTag(testTagVal));
	assertEquals(testTag, Tag.getTag(testTagVal));
	assertTrue(testTag.equals(testTagVal));

	assertEquals(new Integer(testTagVal).hashCode(), testTag.hashCode());

	//
	// Define a new Tag type
	//
	int newTagVal = 0x1456;
	Tag newTag = Tag.defineTag(0x1456, Integer.class, null, 4);

	assertTrue(newTag.equals(newTagVal));
	assertSame(newTag, Tag.getTag(newTagVal));
    }

    public void testBitmaskSerialization() {
	BitmaskEncoder enc = BitmaskEncoder.getInstance();

	byte[] b = new byte[1];
	byte[] expected = {
	    bitSetExpected[0]
	};
	enc.writeTo(Tag.MS_MSG_WAIT_FACILITIES, bitSet, b, 0);
	assertTrue(Arrays.equals(expected, b));

	Tag newTag = Tag.defineTag(0xdeaf, BitSet.class, null, 2);
	b = new byte[2];
	enc.writeTo(newTag, bitSet, b, 0);
	assertTrue(Arrays.equals(bitSetExpected, b));

	BitSet deser = (BitSet)enc.readFrom(newTag, b, 0, 2);
    }

    public void testTLVTableAddParams() {
	TLVTable tab = new TLVTable();

	try {
	    tab.set(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
	} catch (Exception x) {
	    fail("Failed to set IntegerValue size 1");
	}

	try {
	    tab.set(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
	} catch (Exception x) {
	    fail("Failed to set IntegerValue size 2");
	}

	try {
	    tab.set(Tag.QOS_TIME_TO_LIVE, new Long(0xe4e3e2e1L));
	} catch (Exception x) {
	    fail("Failed to set IntegerValue size 4");
	}

	try {
	    tab.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
	} catch (Exception x) {
	    fail("Failed to set StringValue.");
	}

	try {
	    byte[] b = { 0x67, 0x67, 0x67 };
	    tab.set(Tag.CALLBACK_NUM_ATAG, b);
	} catch (Exception x) {
	    fail("Failed to set OctetValue.");
	}
	try {
	    tab.set(Tag.MS_MSG_WAIT_FACILITIES, bitSet);
	    Tag newTag = Tag.defineTag(0xdead, BitSet.class, null, 2);
	    tab.set(newTag, bitSet);
	} catch (Exception x) {
	    fail("Failed to set Bitmask value");
	}
    }


    public void testTLVTableFailAddParams() {
	TLVTable tab = new TLVTable();

	try {
	    // Try and set a string that's too long.
	    String longString = new String(
  "111111111111111111111111111111111111111111111111111111111111111111111111111"
+ "222222222222222222222222222222222222222222222222222222222222222222222222222"
+ "333333333333333333333333333333333333333333333333333333333333333333333333333"
+ "444444444444444444444444444444444444444444444444444444444444444444444444444"
+ "555555555555555555555555555555555555555555555555555555555555555555555555555"
+ "666666666666666666666666666666666666666666666666666666666666666666666666666"
		    );
	    tab.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, longString);
	    fail("Set a StringValue that was too long.");
	} catch (InvalidSizeForValueException x) {
	}

	try {
	    // Try and set an OctetValue that's too short
	    byte[] b = new byte[1];
	    tab.set(Tag.SOURCE_SUBADDRESS, b);
	    fail("Set an OctetValue that was too short.");
	} catch (InvalidSizeForValueException x) {
	}

	try {
	    // Try and set an OctetValue that's too long
	    byte[] b = new byte[70];
	    tab.set(Tag.CALLBACK_NUM_ATAG, b);
	    fail("Set an OctetValue that was too long.");
	} catch (InvalidSizeForValueException x) {
	}
    }

    public void testTLVTableSerialize() {
	//
	// If testTLVTableAddParams fails, this will fail too...make sure it's
	// working first!
	//
	TLVTable tab = new TLVTable();
	byte[] b = { 0x56, 0x67, 0x69 };
	tab.set(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
	tab.set(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
	tab.set(Tag.QOS_TIME_TO_LIVE, new Long((long)Integer.MAX_VALUE));
	tab.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
	tab.set(Tag.CALLBACK_NUM_ATAG, b);
	tab.set(Tag.MS_MSG_WAIT_FACILITIES, bitSet);



	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
	    tab.writeTo(out);
	} catch (IOException x) {
	    fail("I/O Exception while writing to output stream.");
	}
	byte[] serialized = out.toByteArray();

	for (int i = 0; i < 2; i++) {
	    String msg;
	    TLVTable tab1 = new TLVTable();
	    tab1.readFrom(serialized, 0, serialized.length);

	    if (i == 0) {
		msg = "Using getValueFromBytes";
	    } else {
		msg = "Using parseAllOpts.";
		tab1.parseAllOpts();
	    }


	    assertEquals(msg,
		    ((Number)tab.get(Tag.DEST_ADDR_SUBUNIT)).longValue(),
		    ((Number)tab1.get(Tag.DEST_ADDR_SUBUNIT)).longValue());
	    assertEquals(msg,
		    ((Number)tab.get(Tag.DEST_TELEMATICS_ID)).longValue(),
		    ((Number)tab1.get(Tag.DEST_TELEMATICS_ID)).longValue());
	    assertEquals(msg,
		    ((Number)tab.get(Tag.QOS_TIME_TO_LIVE)).longValue(),
		    ((Number)tab1.get(Tag.QOS_TIME_TO_LIVE)).longValue());
	    assertEquals(msg,
		    tab.get(Tag.ADDITIONAL_STATUS_INFO_TEXT),
		    tab1.get(Tag.ADDITIONAL_STATUS_INFO_TEXT));
	    assertTrue(msg,
		    Arrays.equals(
			(byte[])tab.get(Tag.CALLBACK_NUM_ATAG),
			(byte[])tab1.get(Tag.CALLBACK_NUM_ATAG)));
	    assertEquals(msg,
		    bitSet, (BitSet)tab.get(Tag.MS_MSG_WAIT_FACILITIES));
	}
    }
}
