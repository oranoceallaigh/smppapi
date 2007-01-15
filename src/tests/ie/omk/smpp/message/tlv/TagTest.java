package ie.omk.smpp.message.tlv;

import java.util.Arrays;
import java.util.BitSet;

import junit.framework.TestCase;

public class TagTest extends TestCase {
    private BitSet bitSet = new BitSet();
    private byte[] bitSetExpected;
    
    public void setUp() {
        // Set up a BitSet to use.
        bitSet.set(2);
        bitSet.set(6);
        bitSet.set(5);
        bitSet.set(7);
        bitSet.set(12);
        bitSet.set(14);
        bitSetExpected = new byte[] {(byte) 0xe4, (byte) 0x50};
    }

    public void testTag() {
        Tag testTag = Tag.SMS_SIGNAL;
        int testTagVal = 0x1203;

        assertEquals(testTagVal, testTag.intValue());
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
        BitmaskEncoder enc = new BitmaskEncoder();

        byte[] b = new byte[1];
        byte[] expected = {bitSetExpected[0]};
        enc.writeTo(Tag.MS_MSG_WAIT_FACILITIES, bitSet, b, 0);
        assertTrue(Arrays.equals(expected, b));

        Tag newTag = Tag.defineTag(0xdeaf, BitSet.class, null, 2);
        b = new byte[2];
        enc.writeTo(newTag, bitSet, b, 0);
        assertTrue(Arrays.equals(bitSetExpected, b));

        BitSet deser = (BitSet) enc.readFrom(newTag, b, 0, newTag.getLength());
        System.out.println(bitSet);
        System.out.println(deser);
        assertEquals(bitSet, deser);
    }

    public void testDefineAndUndefine() throws Exception {
        final int TAG_VALUE = 9000;
        assertFalse(Tag.isTagDefined(TAG_VALUE));
        Tag.defineTag(TAG_VALUE, String.class, null, 30);
        assertTrue(Tag.isTagDefined(TAG_VALUE));
        Tag tag = Tag.getTag(TAG_VALUE);
        assertEquals(String.class, tag.getType());
        assertEquals(TAG_VALUE, tag.intValue());
        assertEquals(30, tag.getMaxLength());
        assertEquals(30, tag.getMinLength());
        assertEquals(30, tag.getLength());
        Tag.undefineTag(tag);
        assertFalse(Tag.isTagDefined(TAG_VALUE));
    }
}
