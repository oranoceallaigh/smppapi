package ie.omk.smpp.message.param;

import ie.omk.smpp.util.SMPPIO;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class IntegerParamDescriptorTest extends TestCase {

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
        assertEquals(intSize, descriptor.sizeOf(value));
        assertEquals(intSize, descriptor.sizeOf(null));
    }
    
    private void doWriteObject(IntegerParamDescriptor descriptor, long value) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        descriptor.writeObject(value, out);
        byte[] array = out.toByteArray();
        assertEquals(descriptor.sizeOf(value), array.length);
        switch (descriptor.sizeOf(value)) {
        case 8:
            assertEquals(value, SMPPIO.bytesToLong(array, 0));
            break;
        case 4:
            assertEquals(value, SMPPIO.bytesToLongInt(array, 0));
            break;
        case 2:
            assertEquals((int) value, SMPPIO.bytesToShort(array, 0));
            break;
        default:
            assertEquals((int) value, (int) array[0] & 0xff);
            break;
        }
    }
    
    private void doReadObject(IntegerParamDescriptor descriptor, long value) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        switch (descriptor.sizeOf(value)) {
        case 8:
            SMPPIO.writeLong(value, out);
            break;
        case 4:
            SMPPIO.writeLongInt(value, out);
            break;
        case 2:
            SMPPIO.writeShort((int) value, out);
            break;
        default:
            SMPPIO.writeByte((int) value, out);
            break;
        }
        byte[] array = out.toByteArray();
        List<Object> list = new ArrayList<Object>();
        descriptor.readObject(list, array, 0);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0) instanceof Number);
        assertEquals(value, ((Number) list.get(0)).longValue());
    }
}
