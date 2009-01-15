package com.adenki.smpp.message.param;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;

import org.testng.annotations.Test;

import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class BitmaskParamDescriptorTest {
    
    private ParamDescriptor descriptor = new BitmaskParamDescriptor();

    public void testWriteObjectWithNoBitsSet() throws Exception {
        BitSet bitset = new BitSet();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        descriptor.writeObject(bitset, encoder);
        byte[] actual = out.toByteArray();
        assertEquals(actual.length, 1);
        assertEquals(actual, new byte[] { 0 });
    }
    
    public void testWriteObject() throws Exception {
        BitSet bitset = new BitSet();
        bitset.set(3);
        bitset.set(4);
        bitset.set(6);
        bitset.set(7);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        descriptor.writeObject(bitset, encoder);
        byte[] actual = out.toByteArray();
        assertEquals(actual.length, 1);
        assertEquals(actual, new byte[] { (byte) 0xd8 });
    }
    
    public void testReadObjectWithNoBitsSet() throws Exception {
        PacketDecoderImpl decoder = new PacketDecoderImpl(new byte[] {0});
        BitSet bitset = (BitSet) descriptor.readObject(decoder, 1);
        assertEquals(bitset.length(), 0);
        assertEquals(bitset.nextSetBit(0), -1);
        assertEquals(decoder.getParsePosition(), 1);
    }
    
    public void testReadObjectWithSize1() throws Exception {
        // We're reading from an offset here, the first byte should be ignored.
        PacketDecoderImpl decoder =
            new PacketDecoderImpl(new byte[] {0, 0x7f, 0x7f, 0x77}, 3);
        BitSet bitset = (BitSet) descriptor.readObject(decoder, 1);
        assertEquals(bitset.nextSetBit(0), 0);
        assertEquals(bitset.nextSetBit(1), 1);
        assertEquals(bitset.nextSetBit(2), 2);
        assertEquals(bitset.nextSetBit(3), 4);
        assertEquals(bitset.nextSetBit(5), 5);
        assertEquals(bitset.nextSetBit(6), 6);
        assertEquals(bitset.nextSetBit(7), -1);
        assertEquals(decoder.getParsePosition(), 4);
    }
}
