package ie.omk.smpp.message.param;

import ie.omk.smpp.Address;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class AddressParamDescriptorTest extends TestCase {

    private AddressParamDescriptor descriptor = new AddressParamDescriptor();
    
    public void testSizeOf() {
        Address address = new Address(0, 0, "1234");
        assertEquals(3, descriptor.sizeOf(null));
        assertEquals(3, descriptor.sizeOf(new Address()));
        assertEquals(7, descriptor.sizeOf(address));
    }
    
    public void testWriteObject() throws Exception {
        ByteArrayOutputStream out;
        byte[] array;
        
        out = new ByteArrayOutputStream();
        descriptor.writeObject(null, out);
        array = out.toByteArray();
        assertEquals(3, array.length);
        assertEquals((byte) 0, array[0]);
        assertEquals((byte) 0, array[1]);
        assertEquals((byte) 0, array[2]);
        
        Address address = new Address(3, 4, "1234");
        out = new ByteArrayOutputStream();
        descriptor.writeObject(address, out);
        array = out.toByteArray();
        assertEquals(7, array.length);
    }
    
    public void testReadObject() throws Exception {
        List<Object> list = new ArrayList<Object>();
        byte[] array = new byte[] {0, 0, 0};
        descriptor.readObject(list, array, 0);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof Address);
    }
}
