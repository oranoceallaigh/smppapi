package com.adenki.smpp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.testng.annotations.Test;

import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class AddressTest {
    private void testSize(Address addr) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        try {
            addr.writeTo(encoder);
        } catch (IOException x) {
            fail("Serializing address caused I/O Exception:\n" + x.toString());
            return;
        }
        byte[] array = out.toByteArray();
        PacketDecoderImpl decoder = new PacketDecoderImpl(array);
        Address deserialized = decoder.readAddress();
        assertEquals(decoder.getParsePosition(), array.length);
        assertEquals(addr.getLength(), array.length, "serialized. ");
        assertEquals(array.length, deserialized.getLength(), "deserialized.");
    }

    public void testEmptyFieldSize() {
        testSize(new Address());
    }

    public void testFilledFieldSize() {
        Address addr = new Address();
        addr.setTON(Ton.INTERNATIONAL);
        addr.setNPI(Npi.ISDN);
        addr.setAddress("353851234567");
        testSize(addr);
    }

    public void testEquals() {
        Address a1 = new Address(Ton.NETWORK,
                Npi.NATIONAL, "353851234567");
        Address a2 = new Address(Ton.NETWORK,
                Npi.NATIONAL, "353851234567");
        Address a3 = new Address(Ton.NATIONAL,
                Npi.NATIONAL, "441237654321");

        assertEquals(a2, a1);
        assertTrue(!(a1.equals(a3)));
    }
}

