package com.adenki.smpp.message.param;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.nio.ByteBuffer;

import org.testng.annotations.Test;

import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class IntegerParamDescriptorTest {

    private IntegerParamDescriptor integer1 = new IntegerParamDescriptor(1);
    private IntegerParamDescriptor integer2 = new IntegerParamDescriptor(2);
    private IntegerParamDescriptor integer4 = new IntegerParamDescriptor(4);
    private IntegerParamDescriptor integer8 = new IntegerParamDescriptor(8);

    public void testSizeOf() throws Exception {
        doSizeOf(new IntegerParamDescriptor(1), 1);
        doSizeOf(new IntegerParamDescriptor(2), 2);
        doSizeOf(new IntegerParamDescriptor(4), 4);
        doSizeOf(new IntegerParamDescriptor(8), 8);
    }

    public void testWriteObject() throws Exception {
        doWriteObject(integer1, 0L);
        doWriteObject(integer1, 127L);
        doWriteObject(integer1, 255L);
        
        doWriteObject(integer2, 0L);
        doWriteObject(integer2, (long) Short.MAX_VALUE);
        doWriteObject(integer2, 65535L);
        
        doWriteObject(integer4, 0L);
        doWriteObject(integer4, (long) Integer.MAX_VALUE);
        doWriteObject(integer4, 4294967295L);
        
        doWriteObject(integer8, 0L);
        doWriteObject(integer8, Long.MAX_VALUE);
    }
    
    public void testReadObject() throws Exception {
        doReadObject(integer1, 0L);
        doReadObject(integer1, 127L);
        doReadObject(integer1, 255L);
        
        doReadObject(integer2, 0L);
        doReadObject(integer2, (long) Short.MAX_VALUE);
        doReadObject(integer2, 65535L);
        
        doReadObject(integer4, 0L);
        doReadObject(integer4, (long) Integer.MAX_VALUE);
        doReadObject(integer4, 4294967295L);
        
        doReadObject(integer8, 0L);
        doReadObject(integer8, Long.MAX_VALUE);
    }
    
    private void doSizeOf(IntegerParamDescriptor descriptor, int intSize) throws Exception {
        Integer value = new Integer(231);
        assertEquals(descriptor.sizeOf(value), intSize);
        assertEquals(descriptor.sizeOf(null), intSize);
    }
    
    private void doWriteObject(IntegerParamDescriptor descriptor, long value) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        PacketEncoderImpl encoder = new PacketEncoderImpl(buffer);
        descriptor.writeObject(value, encoder);
        buffer.flip();
        assertEquals(buffer.remaining(), descriptor.sizeOf(value));
        switch (descriptor.sizeOf(value)) {
        case 8:
            assertEquals(buffer.getLong(), value);
            break;
        case 4:
            assertEquals(buffer.getInt(), (int) value);
            break;
        case 2:
            assertEquals(buffer.getShort(), (short) value);
            break;
        default:
            assertEquals(buffer.get(), (byte) value);
            break;
        }
    }
    
    private void doReadObject(IntegerParamDescriptor descriptor, long value) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        int intSize = descriptor.sizeOf(value);
        switch (intSize) {
        case 8:
            buffer.putLong(value);
            break;
        case 4:
            buffer.putInt((int) value);
            break;
        case 2:
            buffer.putShort((short) value);
            break;
        default:
            buffer.put((byte) value);
            break;
        }
        buffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(buffer);
        Number number = (Number) descriptor.readObject(decoder, intSize);
        assertNotNull(number);
        assertEquals(number.longValue(), value);
        assertEquals(buffer.remaining(), 0);
    }
}
