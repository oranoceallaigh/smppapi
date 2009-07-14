package com.adenki.smpp.message.param;

import static org.testng.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.testng.annotations.Test;

import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class CStringParamDescriptorTest {

    private final String testString = new String("A test string");
    private CStringParamDescriptor descriptor = new CStringParamDescriptor();
    
    public void testSizeOf() {
        assertEquals(descriptor.sizeOf(null), 1);
        assertEquals(descriptor.sizeOf(""), 1);
        assertEquals(descriptor.sizeOf(testString), testString.length() + 1);
    }
    
    public void testWriteNullString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        descriptor.writeObject(null, encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteEmptyString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        descriptor.writeObject("", encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0);
    }

    public void testWriteString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        descriptor.writeObject(testString, encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), testString.length() + 1);
        assertEquals(buffer.get(buffer.remaining() - 1), (byte) 0);
        byte[] bytes = new byte[buffer.remaining() - 1];
        buffer.get(bytes);
        assertEquals(new String(bytes, "US-ASCII"), testString);
    }

    public void testReadEmptyString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.put((byte) 0);
        buffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(buffer);
        String string = (String) descriptor.readObject(decoder, 0);
        assertEquals(string, "");
    }

    public void testReadString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.put(testString.getBytes("US-ASCII"));
        buffer.put((byte) 0);
        buffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(buffer);
        String string = (String) descriptor.readObject(decoder, 0);
        assertEquals(string, testString);
    }
}
