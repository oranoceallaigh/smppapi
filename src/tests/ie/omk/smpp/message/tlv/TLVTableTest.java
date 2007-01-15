package ie.omk.smpp.message.tlv;

import ie.omk.smpp.util.SMPPIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import junit.framework.TestCase;

public class TLVTableTest extends TestCase {
    public void testTLVTableAddParams() {
        TLVTable table = new TLVTable();

        try {
            assertFalse(table.isSet(Tag.DEST_ADDR_SUBUNIT));
            table.set(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
            assertTrue(table.isSet(Tag.DEST_ADDR_SUBUNIT));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 1");
        }

        try {
            assertFalse(table.isSet(Tag.DEST_TELEMATICS_ID));
            table.set(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
            assertTrue(table.isSet(Tag.DEST_TELEMATICS_ID));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 2");
        }

        try {
            assertFalse(table.isSet(Tag.QOS_TIME_TO_LIVE));
            table.set(Tag.QOS_TIME_TO_LIVE, new Long(0xe4e3e2e1L));
            assertTrue(table.isSet(Tag.QOS_TIME_TO_LIVE));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 4");
        }

        try {
            assertFalse(table.isSet(Tag.ADDITIONAL_STATUS_INFO_TEXT));
            table.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
            assertTrue(table.isSet(Tag.ADDITIONAL_STATUS_INFO_TEXT));
        } catch (Exception x) {
            fail("Failed to set StringValue.");
        }

        try {
            assertFalse(table.isSet(Tag.CALLBACK_NUM_ATAG));
            byte[] b = {0x67, 0x67, 0x67};
            table.set(Tag.CALLBACK_NUM_ATAG, b);
            assertTrue(table.isSet(Tag.CALLBACK_NUM_ATAG));
        } catch (Exception x) {
            fail("Failed to set OctetValue.");
        }
        try {
            assertFalse(table.isSet(Tag.MS_MSG_WAIT_FACILITIES));
            BitSet bitSet = new BitSet();
            table.set(Tag.MS_MSG_WAIT_FACILITIES, bitSet);
            assertTrue(table.isSet(Tag.MS_MSG_WAIT_FACILITIES));
            Tag newTag = Tag.defineTag(0xdead, BitSet.class, null, 2);
            assertFalse(table.isSet(newTag));
            table.set(newTag, bitSet);
            assertTrue(table.isSet(newTag));
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
                    + "666666666666666666666666666666666666666666666666666666666666666666666666666");
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
        byte[] b = {0x56, 0x67, 0x69};
        BitSet bitSet = new BitSet();
        bitSet.set(3);
        tab.set(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
        tab.set(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
        tab.set(Tag.QOS_TIME_TO_LIVE, new Long((long) Integer.MAX_VALUE));
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

        // The table must report the same length as it actually serializes to..
        if (tab.getLength() != serialized.length) {
            fail("Table getLength is different to actual encoded length");
        }

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
                    ((Number) tab.get(Tag.DEST_ADDR_SUBUNIT)).longValue(),
                    ((Number) tab1.get(Tag.DEST_ADDR_SUBUNIT)).longValue());
            assertEquals(msg,
                    ((Number) tab.get(Tag.DEST_TELEMATICS_ID)).longValue(),
                    ((Number) tab1.get(Tag.DEST_TELEMATICS_ID)).longValue());
            assertEquals(msg,
                    ((Number) tab.get(Tag.QOS_TIME_TO_LIVE)).longValue(),
                    ((Number) tab1.get(Tag.QOS_TIME_TO_LIVE)).longValue());
            assertEquals(msg,
                    tab.get(Tag.ADDITIONAL_STATUS_INFO_TEXT),
                    tab1.get(Tag.ADDITIONAL_STATUS_INFO_TEXT));
            assertTrue(msg,
                    Arrays.equals((byte[]) tab.get(Tag.CALLBACK_NUM_ATAG),
                            (byte[]) tab1.get(Tag.CALLBACK_NUM_ATAG)));
            assertEquals(msg, bitSet,
                    (BitSet) tab.get(Tag.MS_MSG_WAIT_FACILITIES));
        }
    }

    /**
     * This test creates a byte array representing a TLVTable which contains a
     * tag that the API does not know about. The API should be able to decode
     * any optional parameter that is well-formed - the fact that it doesn't
     * know about it beforehand should not cause an error in the API.
     */
    public void testTLVTableDeSerializeUnknown() {
        // Set up a byte array which contains 2 known optional parameters
        // followed by 2 unknowns.
        byte[] b = new byte[256];

        StringEncoder se = new StringEncoder();
        NumberEncoder ne = new NumberEncoder();

        int p = 0, length = 0;

        Integer i = new Integer(0xbcad);
        length = ne.getValueLength(Tag.DEST_TELEMATICS_ID, i);
        SMPPIO.intToBytes(Tag.DEST_TELEMATICS_ID.intValue(), 2, b, 0);
        SMPPIO.intToBytes(length, 2, b, 2);
        ne.writeTo(Tag.DEST_TELEMATICS_ID, i, b, 4);
        p += (4 + length);

        String v = "smppapi tlv tests";
        length = se.getValueLength(Tag.ADDITIONAL_STATUS_INFO_TEXT, v);
        SMPPIO.intToBytes(Tag.ADDITIONAL_STATUS_INFO_TEXT.intValue(), 2, b, p);
        SMPPIO.intToBytes(length, 2, b, p + 2);
        se.writeTo(Tag.ADDITIONAL_STATUS_INFO_TEXT, v, b, p + 4);
        p += (4 + length);

        // Tag '0xcafe', length 2.
        b[p++] = (byte) 0xca;
        b[p++] = (byte) 0xfe;
        b[p++] = (byte) 0x00;
        b[p++] = (byte) 0x02;
        b[p++] = (byte) 0xfe;
        b[p++] = (byte) 0xed;

        // Tag '0xbeef', length 5
        b[p++] = (byte) 0xbe;
        b[p++] = (byte) 0xef;
        b[p++] = (byte) 0x00;
        b[p++] = (byte) 0x05;
        b[p++] = (byte) 0xba;
        b[p++] = (byte) 0xbe;
        b[p++] = (byte) 0xde;
        b[p++] = (byte) 0xad;
        b[p++] = (byte) 0x99;

        try {
            // Run the test - attempt to deserialize the table.
            TLVTable tab = new TLVTable();
            tab.readFrom(b, 0, p);

            tab.parseAllOpts();

            assertEquals(tab.get(Tag.DEST_TELEMATICS_ID), i);
            assertEquals(tab.get(Tag.ADDITIONAL_STATUS_INFO_TEXT), v);

            b = (byte[]) tab.get(0xcafe);
            byte[] expectedValue = {(byte) 0xfe, (byte) 0xed};

            assertTrue(Arrays.equals(b, expectedValue));

            b = (byte[]) tab.get(0xbeef);
            expectedValue = new byte[] {(byte) 0xba, (byte) 0xbe, (byte) 0xde,
                    (byte) 0xad, (byte) 0x99, };

            assertTrue(Arrays.equals(b, expectedValue));

        } catch (Exception x) {
            x.printStackTrace(System.err);
            fail("Deserialize failed. " + x.getMessage());
        }
    }
}
