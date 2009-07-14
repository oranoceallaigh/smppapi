package com.adenki.smpp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.nio.ByteBuffer;

import org.testng.annotations.Test;

import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

@Test
public class AddressTest {
    /**
     * Encode and then decode <tt>addr</tt>. Ensure that:
     * <ol>
     * <li><tt>addr.getLength() == encodedLength</tt></li>
     * <li><tt>decodedAddr.getLength() == encodedLength</tt></li>
     * </ol>
     * @param addr
     * @throws Exception
     */
    private void testSize(Address addr) throws Exception {
        ByteBuffer encodeBuffer = ByteBuffer.allocate(128);
        PacketEncoderImpl encoder = new PacketEncoderImpl(encodeBuffer);
        addr.writeTo(encoder);
        
        ByteBuffer decodeBuffer = encodeBuffer.duplicate();
        decodeBuffer.flip();
        PacketDecoderImpl decoder = new PacketDecoderImpl(decodeBuffer);
        Address decodedAddr = new Address();
        decodedAddr.readFrom(decoder);
        
        encodeBuffer.flip();
        assertEquals(encodeBuffer.remaining(), addr.getLength());
        assertEquals(decodedAddr.getLength(), encodeBuffer.remaining());
    }

    public void testEmptyFieldSize() throws Exception {
        testSize(new Address());
    }

    public void testFilledFieldSize() throws Exception {
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
        assertEquals(a1, a2);
        assertFalse(a1.equals(a3));
    }
}

