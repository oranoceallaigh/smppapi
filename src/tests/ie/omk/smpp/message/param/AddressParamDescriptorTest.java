package ie.omk.smpp.message.param;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.ParsePosition;

import java.io.ByteArrayOutputStream;

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
        ParsePosition position = new ParsePosition(0);
        byte[] array = new byte[] {0, 0, 0};
        Address address = (Address) descriptor.readObject(array, position, -1);
        assertNotNull(address);
        assertEquals(0, address.getTON());
        assertEquals(0, address.getNPI());
        assertEquals("", address.getAddress());
        assertEquals(3, position.getIndex());
    }
}
