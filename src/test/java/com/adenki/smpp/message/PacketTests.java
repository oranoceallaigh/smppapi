package com.adenki.smpp.message;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.adenki.smpp.message.tlv.Tag;
import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoderImpl;

/**
 * Test that the value reported by <code>getLength</code> matches the actual
 * length a packet serializes to and deserializes from.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public abstract class PacketTests<T extends SMPPPacket> {

    private Random random;
    
    @Test
    public void testEqualsIsFalseForNullObject() throws Exception {
        T packet = getPacketType().newInstance();
        assertFalse(packet.equals(null));
    }

    @Test
    public void testEqualsIsFalseForWrongPacketType() throws Exception {
        T packet = getPacketType().newInstance();
        SMPPPacket other;
        if (packet instanceof BindReceiver) {
            other = new BindTransceiver();
        } else {
            other = new BindReceiver();
        }
        assertFalse(packet.equals(other));
    }
    
    @Test
    public void testEqualsIsTrueForClonedPacket() throws Exception {
        T packet = getPacketType().newInstance();
        Object cloned = packet.clone();
        assertNotNull(cloned);
        assertEquals(cloned, packet);
    }
    
    @Test
    public void testSizesMatchWithSerializationWithDefaultInitalisation() throws Exception {
        T packet = getPacketType().newInstance();
        testGetLengthMatchesReality(packet);
    }
    
    @Test
    public void testSizesMatchWithSerializationWithFieldsSet() throws Exception {
        T packet = getInitialisedPacket();
        setCommonFields(packet);
        testGetLengthMatchesReality(packet);
    }
    
    /**
     * Test that after serializing and deserializing a packet, the mandatory
     * parameters all match.
     * @throws Exception
     */
    @Test
    public void testDeserializedFieldsMatchOriginalPacket() throws Exception {
        T original = getInitialisedPacket();
        setCommonFields(original);
        T decodedPacket = getPacketType().newInstance();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        original.writeTo(encoder);
        PacketDecoderImpl decoder = new PacketDecoderImpl(out.toByteArray());
        decodedPacket.readFrom(decoder);
        assertEquals(decodedPacket, original);
    }

    @Test
    public void testPacketWithNonZeroStatusIsOnlyAHeader() throws Exception {
        T original = getInitialisedPacket();
        setCommonFields(original);
        T decodedPacket = getPacketType().newInstance();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        original.writeTo(encoder);
        PacketDecoderImpl decoder = new PacketDecoderImpl(
                out.toByteArray());
        decodedPacket.readFrom(decoder);
        assertEquals(original, decodedPacket);
    }
    
    protected abstract Class<T> getPacketType();
    
    protected abstract T getInitialisedPacket();
    
    @BeforeClass
    protected void initRandom() {
        random = new Random();
    }
    
    private void setCommonFields(SMPPPacket packet) {
        packet.setSequenceNum((long) random.nextInt(Integer.MAX_VALUE));
        packet.setCommandStatus(0);
        // Add a couple of TLVs
        packet.setTLV(Tag.DEST_ADDR_SUBUNIT, new Integer(234));
        packet.setTLV(Tag.ITS_SESSION_INFO, new byte[] {1, 2});
    }
    
    /**
     * Test an individual packet. This method serializes the packet to a byte
     * array and then deserializes a second packet from that byte array. It then
     * asserts that <code>getLength</code> on the original packet matches the
     * length of the byte array and that the length of the byte array matches
     * the value returned from <code>getLength</code> on the deserialized
     * packet.
     */
    private void testGetLengthMatchesReality(SMPPPacket packet) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        packet.writeTo(encoder);
        byte[] array = out.toByteArray();
        assertEquals(array.length, packet.getLength());
        SMPPPacket deserialized = getPacketType().newInstance();
        PacketDecoderImpl decoder = new PacketDecoderImpl(array);
        deserialized.readFrom(decoder);
        assertEquals(packet.getLength(), array.length, packet.getClass().getName());
        assertEquals(array.length, deserialized.getLength(), packet.getClass().getName());
    }
}
