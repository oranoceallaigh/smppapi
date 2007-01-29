package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        ParsePosition position = new ParsePosition(0);
        byte[] array = new byte[testString.length() + 1];
        System.arraycopy(testString.getBytes("US-ASCII"), 0, array, 0, testString.length());
        array[array.length - 1] = (byte) 0;
        
        String string =
            (String) descriptor.readObject(new byte[] {0}, position, -1);
        assertNotNull(string);
        assertEquals("", string);
        assertEquals(1, position.getIndex());

        position = new ParsePosition(0);
        string = (String) descriptor.readObject(array, position, -1);
        assertNotNull(string);
        assertEquals(testString, string);
        assertEquals(testString.length() + 1, position.getIndex());
    }
}
