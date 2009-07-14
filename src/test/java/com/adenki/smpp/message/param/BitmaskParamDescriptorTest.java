package com.adenki.smpp.message.param;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.BitSet;

import org.testng.annotations.Test;

import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class BitmaskParamDescriptorTest {
    
    private ParamDescriptor descriptor = new BitmaskParamDescriptor();

    public void testWriteObjectWithNoBitsSet() throws Exception {
        BitSet bitset = new BitSet();
        ByteBuffer buffer = ByteBuffer.allocate(16);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        descriptor.writeObject(bitset, encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteObject() throws Exception {
        BitSet bitset = new BitSet();
        bitset.set(3);
        bitset.set(4);
        bitset.set(6);
        bitset.set(7);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        descriptor.writeObject(bitset, encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0xd8);
    }
    
    public void testReadObjectWithNoBitsSet() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0);
        buffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(buffer);
        BitSet bitset = (BitSet) descriptor.readObject(decoder, 1);
        assertEquals(bitset.length(), 0);
        assertEquals(bitset.nextSetBit(0), -1);
        assertEquals(buffer.remaining(), 0);
    }
    
    public void testReadObjectWithSize1() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x77);
        buffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(buffer);
        BitSet bitset = (BitSet) descriptor.readObject(decoder, 1);
        assertEquals(bitset.nextSetBit(0), 0);
        assertEquals(bitset.nextSetBit(1), 1);
        assertEquals(bitset.nextSetBit(2), 2);
        assertEquals(bitset.nextSetBit(3), 4);
        assertEquals(bitset.nextSetBit(5), 5);
        assertEquals(bitset.nextSetBit(6), 6);
        assertEquals(bitset.nextSetBit(7), -1);
        assertEquals(buffer.remaining(), 0);
    }
    
    public void testReadBitmaskWithMultipleOctets() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0xb6);
        buffer.put((byte) 0x1a);
        buffer.put((byte) 0x53);
        buffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(buffer);
        BitSet bitset = (BitSet) descriptor.readObject(decoder, 3);
        assertEquals(bitset.length(), 24);
        assertTrue(bitset.get(0));
        assertTrue(bitset.get(1));
        assertFalse(bitset.get(2));
        assertFalse(bitset.get(3));
        assertTrue(bitset.get(4));
        assertFalse(bitset.get(5));
        assertTrue(bitset.get(6));
        assertFalse(bitset.get(7));
        assertFalse(bitset.get(8));
        assertTrue(bitset.get(9));
        assertFalse(bitset.get(10));
        assertTrue(bitset.get(11));
        assertTrue(bitset.get(12));
        assertFalse(bitset.get(13));
        assertFalse(bitset.get(14));
        assertFalse(bitset.get(15));
        assertFalse(bitset.get(16));
        assertTrue(bitset.get(17));
        assertTrue(bitset.get(18));
        assertFalse(bitset.get(19));
        assertTrue(bitset.get(20));
        assertTrue(bitset.get(21));
        assertFalse(bitset.get(22));
        assertTrue(bitset.get(23));
        assertEquals(buffer.remaining(), 0);
    }
    
    public void testWriteBitmaskIgnoresBitsOverIndex7() throws Exception {
        BitSet bitSet = new BitSet();
        bitSet.set(2);
        bitSet.set(4);
        bitSet.set(6);
        bitSet.set(8);
        bitSet.set(9);
        bitSet.set(16);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        assertEquals(descriptor.sizeOf(bitSet), 1);
        descriptor.writeObject(bitSet, encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0x54);
    }
}
