package com.adenki.smpp.message.tlv;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;

import org.testng.annotations.Test;

import com.adenki.smpp.message.param.BitmaskParamDescriptor;
import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class TLVTableImplTest {
    public void testTLVTableAddParams() {
        TLVTableImpl table = new TLVTableImpl();

        try {
            assertFalse(table.containsKey(Tag.DEST_ADDR_SUBUNIT));
            table.put(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
            assertTrue(table.containsKey(Tag.DEST_ADDR_SUBUNIT));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 1");
        }

        try {
            assertFalse(table.containsKey(Tag.DEST_TELEMATICS_ID));
            table.put(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
            assertTrue(table.containsKey(Tag.DEST_TELEMATICS_ID));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 2");
        }

        try {
            assertFalse(table.containsKey(Tag.QOS_TIME_TO_LIVE));
            table.put(Tag.QOS_TIME_TO_LIVE, new Long(0xe4e3e2e1L));
            assertTrue(table.containsKey(Tag.QOS_TIME_TO_LIVE));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 4");
        }

        try {
            assertFalse(table.containsKey(Tag.ADDITIONAL_STATUS_INFO_TEXT));
            table.put(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
            assertTrue(table.containsKey(Tag.ADDITIONAL_STATUS_INFO_TEXT));
        } catch (Exception x) {
            fail("Failed to set StringValue.");
        }

        try {
            assertFalse(table.containsKey(Tag.CALLBACK_NUM_ATAG));
            byte[] b = {0x67, 0x67, 0x67};
            table.put(Tag.CALLBACK_NUM_ATAG, b);
            assertTrue(table.containsKey(Tag.CALLBACK_NUM_ATAG));
        } catch (Exception x) {
            fail("Failed to set OctetValue.");
        }
        try {
            assertFalse(table.containsKey(Tag.MS_MSG_WAIT_FACILITIES));
            BitSet bitSet = new BitSet();
            table.put(Tag.MS_MSG_WAIT_FACILITIES, bitSet);
            assertTrue(table.containsKey(Tag.MS_MSG_WAIT_FACILITIES));
            Tag newTag = Tag.defineTag(0xdead, new BitmaskParamDescriptor(), 1);
            assertFalse(table.containsKey(newTag));
            table.put(newTag, bitSet);
            assertTrue(table.containsKey(newTag));
        } catch (Exception x) {
            fail("Failed to set Bitmask value");
        }
    }
    
    public void testTLVTableFailAddParams() {
        TLVTableImpl tab = new TLVTableImpl();

        try {
            // Try and set a string that's too long.
            String longString = new String(
                    "111111111111111111111111111111111111111111111111111111111111111111111111111"
                    + "222222222222222222222222222222222222222222222222222222222222222222222222222"
                    + "333333333333333333333333333333333333333333333333333333333333333333333333333"
                    + "444444444444444444444444444444444444444444444444444444444444444444444444444"
                    + "555555555555555555555555555555555555555555555555555555555555555555555555555"
                    + "666666666666666666666666666666666666666666666666666666666666666666666666666");
            tab.put(Tag.ADDITIONAL_STATUS_INFO_TEXT, longString);
            fail("Set a StringValue that was too long.");
        } catch (InvalidSizeForValueException x) {
        }

        try {
            // Try and set an OctetValue that's too short
            byte[] b = new byte[1];
            tab.put(Tag.SOURCE_SUBADDRESS, b);
            fail("Set an OctetValue that was too short.");
        } catch (InvalidSizeForValueException x) {
        }

        try {
            // Try and set an OctetValue that's too long
            byte[] b = new byte[70];
            tab.put(Tag.CALLBACK_NUM_ATAG, b);
            fail("Set an OctetValue that was too long.");
        } catch (InvalidSizeForValueException x) {
        }
    }

    public void testGetStringReturnsNullOnUnsetTag() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        assertNull(table.getString(Tag.RECEIPTED_MESSAGE_ID));
    }

    public void testGetBitmaskReturnsNullOnUnsetTag() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        assertNull(table.getBitmask(Tag.MS_MSG_WAIT_FACILITIES));
    }

    public void testGetBytesReturnsNullOnUnsetTag() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        assertNull(table.getBytes(Tag.DEST_SUBADDRESS));
    }

    public void testGetIntReturnsNegativeOneOnUnsetTag() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        assertEquals(table.getInt(Tag.DEST_TELEMATICS_ID), -1);
    }
    
    public void testGetLongReturnsNegativeOneOnUnsetTag() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        assertEquals(table.getLong(Tag.DEST_TELEMATICS_ID), -1L);
    }
    
    public void testGetIntAndGetLongSucceed() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        table.put(Tag.DEST_ADDR_SUBUNIT, new Integer(56));
        assertEquals(table.getInt(Tag.DEST_ADDR_SUBUNIT), 56);
        assertEquals(table.getLong(Tag.DEST_ADDR_SUBUNIT), 56L);
    }

    public void testGetIntThrowsExceptionOnIncorrectType() throws Exception {
        try {
            TLVTableImpl table = new TLVTableImpl();
            table.put(Tag.RECEIPTED_MESSAGE_ID, "messageID");
            table.getInt(Tag.RECEIPTED_MESSAGE_ID);
            fail("Expected a ClassCastException on call to getInt");
        } catch (ClassCastException x) {
        }
    }

    public void testGetLongThrowsExceptionOnIncorrectType() throws Exception {
        try {
            TLVTableImpl table = new TLVTableImpl();
            table.put(Tag.RECEIPTED_MESSAGE_ID, "messageID");
            table.getLong(Tag.RECEIPTED_MESSAGE_ID);
            fail("Expected a ClassCastException on call to getInt");
        } catch (ClassCastException x) {
        }
    }

    public void testGetStringSucceeds() throws Exception {
        TLVTableImpl table = new TLVTableImpl();
        table.put(Tag.RECEIPTED_MESSAGE_ID, "messageID");
        table.put(Tag.DEST_ADDR_SUBUNIT, new Integer(124));
        assertEquals(table.getString(Tag.RECEIPTED_MESSAGE_ID), "messageID");
        assertEquals(table.getString(Tag.DEST_ADDR_SUBUNIT), "124");
    }

    public void testGetBitmaskSucceeds() throws Exception {
        final BitSet bitSet = new BitSet();
        TLVTableImpl table = new TLVTableImpl();
        table.put(Tag.MS_MSG_WAIT_FACILITIES, bitSet);
        assertEquals(table.getBitmask(Tag.MS_MSG_WAIT_FACILITIES), bitSet);
    }

    public void testGetBitmaskThrowsExceptionOnIncorrectType() throws Exception {
        try {
            TLVTableImpl table = new TLVTableImpl();
            table.put(Tag.RECEIPTED_MESSAGE_ID, "messageID");
            table.getBitmask(Tag.RECEIPTED_MESSAGE_ID);
            fail("Expected a ClassCastException on call to getBitmask");
        } catch (ClassCastException x) {
        }
    }

    public void testGetBytesSucceeds() throws Exception {
        byte[] array = new byte[] {1, 2, 3, 4};
        TLVTableImpl table = new TLVTableImpl();
        table.put(Tag.DEST_SUBADDRESS, array);
        assertEquals(table.getBytes(Tag.DEST_SUBADDRESS), array);
    }

    public void testGetBytesThrowsExceptionOnIncorrectType() throws Exception {
        try {
            TLVTableImpl table = new TLVTableImpl();
            table.put(Tag.MS_MSG_WAIT_FACILITIES, new BitSet());
            table.getBytes(Tag.MS_MSG_WAIT_FACILITIES);
            fail("Expected a ClassCastException on call to getBytes");
        } catch (ClassCastException x) {
        }
    }
    
    public void testTLVTableSerialize() {
        // If testTLVTableAddParams fails, this will fail too...make sure it's
        // working first!
        // First, create a table with at least one parameter in it for
        // each type of encoder defined.
        TLVTableImpl origTable = new TLVTableImpl();
        byte[] b = {0x56, 0x67, 0x69};
        BitSet bitSet = new BitSet();
        bitSet.set(3);
        // 0x56 == 86 decimal
        origTable.put(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
        // 0xe2e1 == 58081 decimal
        origTable.put(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
        origTable.put(Tag.QOS_TIME_TO_LIVE, new Long((long) Integer.MAX_VALUE));
        origTable.put(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
        origTable.put(Tag.CALLBACK_NUM_ATAG, b);
        origTable.put(Tag.MS_MSG_WAIT_FACILITIES, bitSet);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        try {
            origTable.writeTo(encoder);
        } catch (IOException x) {
            fail("I/O Exception while writing to output stream.");
        }
        byte[] serialized = out.toByteArray();

        // The table must report the same length as it actually serializes to..
        if (origTable.getLength() != serialized.length) {
            fail("Table getLength is different to actual encoded length");
        }

        PacketDecoderImpl decoder = new PacketDecoderImpl(serialized);
        TLVTableImpl newTable = new TLVTableImpl();
        newTable.readFrom(decoder, serialized.length);
        doTableAssertions(origTable, newTable);
        assertEquals(decoder.getParsePosition(), serialized.length);

        decoder.setParsePosition(0);
        newTable = new TLVTableImpl();
        newTable.readFrom(decoder, serialized.length);
        doTableAssertions(origTable, newTable);
        assertEquals(decoder.getParsePosition(), serialized.length);
    }

    /**
     * This test creates a byte array representing a TLVTableImpl which contains a
     * tag that the API does not know about. The API should be able to decode
     * any optional parameter that is well-formed - the fact that it doesn't
     * know about it beforehand should not cause an error in the API.
     */
    // TODO dependencies
    public void testTLVTableDeSerializeUnknown() throws Exception {
        // Set up a byte array which contains 2 known optional parameters
        // followed by 2 unknowns.
        final Integer testIntValue = new Integer(0xbcad);
        final String testStringValue = "smppapi tlv tests";
        TLVTableImpl table = new TLVTableImpl();
        table.put(Tag.DEST_TELEMATICS_ID, testIntValue);
        table.put(Tag.ADDITIONAL_STATUS_INFO_TEXT, testStringValue);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        table.writeTo(encoder);
        // Tag '0xcafe', length 2.
        encoder.writeBytes(new byte[] {
                (byte) 0xca,
                (byte) 0xfe,
                (byte) 0x00,
                (byte) 0x02,
                (byte) 0xfe,
                (byte) 0xed,
        });
        // Tag '0xbeef', length 5
        encoder.writeBytes(new byte[] {
                (byte) 0xbe,
                (byte) 0xef,
                (byte) 0x00,
                (byte) 0x05,
                (byte) 0xba,
                (byte) 0xbe,
                (byte) 0xde,
                (byte) 0xad,
                (byte) 0x99,
        });

        byte[] b = out.toByteArray();
        try {
            // Run the test - attempt to deserialize the table.
            PacketDecoderImpl decoder = new PacketDecoderImpl(out.toByteArray());
            TLVTableImpl tab = new TLVTableImpl();
            tab.readFrom(decoder, b.length);
            assertEquals(decoder.getParsePosition(), b.length);
            assertEquals(tab.get(Tag.DEST_TELEMATICS_ID), testIntValue);
            assertEquals(tab.get(Tag.ADDITIONAL_STATUS_INFO_TEXT), testStringValue);

            b = (byte[]) tab.get(0xcafe);
            byte[] expectedValue = {(byte) 0xfe, (byte) 0xed};

            assertEquals(b, expectedValue);

            b = (byte[]) tab.get(0xbeef);
            expectedValue = new byte[] {(byte) 0xba, (byte) 0xbe, (byte) 0xde,
                    (byte) 0xad, (byte) 0x99, };

            assertEquals(b, expectedValue);

        } catch (Exception x) {
            x.printStackTrace(System.err);
            fail("Deserialize failed. " + x.getMessage());
        }
    }
    
    private void doTableAssertions(TLVTableImpl origTable, TLVTableImpl newTable) {
        assertEquals(((Number) origTable.get(Tag.DEST_ADDR_SUBUNIT)).longValue(),
                ((Number) newTable.get(Tag.DEST_ADDR_SUBUNIT)).longValue());
        assertEquals(((Number) origTable.get(Tag.DEST_TELEMATICS_ID)).longValue(),
                ((Number) newTable.get(Tag.DEST_TELEMATICS_ID)).longValue());
        assertEquals(((Number) origTable.get(Tag.QOS_TIME_TO_LIVE)).longValue(),
                ((Number) newTable.get(Tag.QOS_TIME_TO_LIVE)).longValue());
        assertEquals(origTable.get(Tag.ADDITIONAL_STATUS_INFO_TEXT),
                newTable.get(Tag.ADDITIONAL_STATUS_INFO_TEXT));
        assertEquals((byte[]) newTable.get(Tag.CALLBACK_NUM_ATAG),
                (byte[]) origTable.get(Tag.CALLBACK_NUM_ATAG));
        assertEquals((BitSet) origTable.get(Tag.MS_MSG_WAIT_FACILITIES),
                (BitSet) newTable.get(Tag.MS_MSG_WAIT_FACILITIES));
    }
}
