package com.adenki.smpp.message.param;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        descriptor.writeObject(null, encoder);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 1);
        assertEquals(array[0], (byte) 0);
    }
    
    public void testWriteEmptyString() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        descriptor.writeObject("", encoder);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 1);
        assertEquals(array[0], (byte) 0);
    }

    public void testWriteString() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        descriptor.writeObject(testString, encoder);
        byte[] array = out.toByteArray();
        assertEquals(array.length, testString.length() + 1);
        assertEquals(array[array.length - 1], (byte) 0);
        assertEquals(new String(array, 0, array.length - 1, "US-ASCII"), testString);
    }

    public void testReadEmptyString() throws Exception {
        PacketDecoderImpl decoder = new PacketDecoderImpl(new byte[] {0});
        String string = (String) descriptor.readObject(decoder, 0);
        assertEquals(string, "");
    }

    public void testReadString() throws Exception {
        byte[] array = new byte[testString.length() + 1];
        System.arraycopy(testString.getBytes("US-ASCII"), 0, array, 0, testString.length());
        array[array.length - 1] = (byte) 0;
        PacketDecoderImpl decoder = new PacketDecoderImpl(array);
        String string = (String) descriptor.readObject(decoder, 0);
        assertEquals(string, testString);
        assertEquals(decoder.getParsePosition(), testString.length() + 1);
    }
}
