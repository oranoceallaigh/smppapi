package ie.omk.smpp;

import ie.omk.smpp.util.GSMConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class AddressTest extends TestCase {
    public AddressTest(String n) {
        super(n);
    }

    private void testSize(Address addr) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            addr.writeTo(out);
        } catch (IOException x) {
            fail("Serializing address caused I/O Exception:\n" + x.toString());
            return;
        }

        byte[] array = out.toByteArray();

        Address deserialized = new Address();
        deserialized.readFrom(array, 0);

        assertEquals("serialized. ", addr.getLength(), array.length);
        assertEquals("deserialized.", array.length, deserialized.getLength());
    }

    public void testEmptyFieldSize() {
        testSize(new Address());
    }

    public void testFilledFieldSize() {
        Address addr = new Address();
        addr.setTON(GSMConstants.GSM_TON_INTERNATIONAL);
        addr.setNPI(GSMConstants.GSM_NPI_ISDN);
        addr.setAddress("353851234567");
        testSize(addr);
    }

    public void testEquals() {
        Address a1 = new Address(GSMConstants.GSM_TON_NETWORK,
                GSMConstants.GSM_NPI_NATIONAL, "353851234567");
        Address a2 = new Address(GSMConstants.GSM_TON_NETWORK,
                GSMConstants.GSM_NPI_NATIONAL, "353851234567");
        Address a3 = new Address(GSMConstants.GSM_TON_NATIONAL,
                GSMConstants.GSM_NPI_NATIONAL, "441237654321");

        assertEquals(a1, a2);
        assertTrue(!(a1.equals(a3)));
    }
}

