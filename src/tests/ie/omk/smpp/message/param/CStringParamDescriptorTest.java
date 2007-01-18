package ie.omk.smpp.message.param;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class CStringParamDescriptorTest extends TestCase {

    private final String testString = new String("A test string");
    private CStringParamDescriptor descriptor = new CStringParamDescriptor();
    
    public void testSizeOf() {
        assertEquals(1, descriptor.sizeOf(null));
        assertEquals(1, descriptor.sizeOf(""));
        assertEquals(testString.length() + 1, descriptor.sizeOf(testString));
    }
    
    public void testWriteObject() throws IOException {
        ByteArrayOutputStream out;
        byte[] array;
        
        out = new ByteArrayOutputStream();
        descriptor.writeObject(null, out);
        array = out.toByteArray();
        assertEquals(1, array.length);
        assertEquals((byte) 0, array[0]);

        out = new ByteArrayOutputStream();
        descriptor.writeObject("", out);
        array = out.toByteArray();
        assertEquals(1, array.length);
        assertEquals((byte) 0, array[0]);

        out = new ByteArrayOutputStream();
        descriptor.writeObject(testString, out);
        array = out.toByteArray();
        assertEquals(testString.length() + 1, array.length);
        assertEquals((byte) 0, array[array.length - 1]);
        assertEquals(testString, new String(array, 0, array.length - 1, "US-ASCII"));
    }
    
    public void testReadObject() throws Exception {
        List<Object> list = new ArrayList<Object>();
        byte[] array = new byte[testString.length() + 1];
        System.arraycopy(testString.getBytes("US-ASCII"), 0, array, 0, testString.length());
        array[array.length - 1] = (byte) 0;
        
        descriptor.readObject(list, new byte[] {0}, 0);
        assertEquals(1, list.size());
        assertEquals("", (String) list.get(0));
        
        list.clear();
        descriptor.readObject(list, array, 0);
        assertEquals(1, list.size());
        assertEquals(testString, (String) list.get(0));
    }
}
