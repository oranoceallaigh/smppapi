package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.BitSet;

import junit.framework.TestCase;

public class BitmaskParamDescriptorTest extends TestCase {
    
    private ParamDescriptor descriptor = new BitmaskParamDescriptor();

    public void testWriteObjectWithNoBitsSet() throws Exception {
        BitSet bitset = new BitSet();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        descriptor.writeObject(bitset, out);
        byte[] actual = out.toByteArray();
        assertEquals(1, actual.length);
        assertTrue(Arrays.equals(new byte[] { 0 }, actual));
    }
    
    public void testWriteObject() throws Exception {
        BitSet bitset = new BitSet();
        bitset.set(3);
        bitset.set(4);
        bitset.set(6);
        bitset.set(7);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        descriptor.writeObject(bitset, out);
        byte[] actual = out.toByteArray();
        assertEquals(1, actual.length);
        assertTrue(Arrays.equals(new byte[] { (byte) 0xd8 }, actual));
    }
    
    public void testReadObjectWithNoBitsSet() throws Exception {
        ParsePosition position = new ParsePosition(0);
        BitSet bitset = (BitSet) descriptor.readObject(
                new byte[] { 0 }, position, 1);
        assertEquals(0, bitset.length());
        assertEquals(-1, bitset.nextSetBit(0));
        assertEquals(1, position.getIndex());
    }
    
    public void testReadObjectWithSize1() throws Exception {
        // We're reading from an offset here, the first byte should be ignored.
        ParsePosition position = new ParsePosition(3);
        BitSet bitset = (BitSet) descriptor.readObject(
                new byte[] { 0, 0x7f, 0x7f, 0x77 }, position, 1);
        assertEquals(0, bitset.nextSetBit(0));
        assertEquals(1, bitset.nextSetBit(1));
        assertEquals(2, bitset.nextSetBit(2));
        assertEquals(4, bitset.nextSetBit(3));
        assertEquals(5, bitset.nextSetBit(5));
        assertEquals(6, bitset.nextSetBit(6));
        assertEquals(-1, bitset.nextSetBit(7));
        assertEquals(4, position.getIndex());
    }
}
