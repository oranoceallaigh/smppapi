package com.adenki.smpp.net;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.message.BindTransceiver;
import com.adenki.smpp.message.BindTransceiverResp;
import com.adenki.smpp.message.BindTransmitter;
import com.adenki.smpp.message.DeliverSM;
import com.adenki.smpp.message.DeliverSMResp;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.SubmitSM;
import com.adenki.smpp.message.SubmitSMResp;
import com.adenki.smpp.message.Unbind;
import com.adenki.smpp.message.UnbindResp;
import com.adenki.smpp.message.tlv.Tag;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.PacketEncoderImpl;
import com.adenki.smpp.version.SMPPVersion;

public class ReplayLinkTest {

    private ByteArrayInputStream inbound;
    private ByteArrayInputStream outbound;
    private List<SMPPPacket> session = new ArrayList<SMPPPacket>();

    @Test(expectedExceptions = IllegalStateException.class)
    public void testReadThrowsExceptionInUnconnectedState() throws Exception {
        ReplayLink link = new ReplayLink(inbound, outbound);
        assertFalse(link.isConnected());
        link.read();
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testWriteThrowsExceptionInUnconnectedState() throws Exception {
        ReplayLink link = new ReplayLink(inbound, outbound);
        assertFalse(link.isConnected());
        link.write(new BindTransmitter(), true);
    }
    
    @Test(expectedExceptions = ReadTimeoutException.class)
    public void testReadThrowsExceptionAfterTimeout() throws Exception {
        ReplayLink link = new ReplayLink(inbound, outbound);
        link.setTimeout(1000);
        assertFalse(link.isConnected());
        link.connect();
        assertTrue(link.isConnected());
        link.read();
    }

    @Test
    public void testSession() throws Exception {
        ReplayLink link = new ReplayLink(inbound, outbound);
        link.connect();
        SMPPPacket packet;
        packet = doRequest(link, BindTransceiver.class, 0);
        link.write(packet, true);
        doResponse(link, BindTransceiverResp.class, 1);
        packet = doRequest(link, SubmitSM.class, 2);
        link.write(packet, true);
        doResponse(link, SubmitSMResp.class, 3);
        doResponse(link, DeliverSM.class, 4);
        packet = doRequest(link, DeliverSMResp.class, 5);
        link.write(packet, true);
        packet = doRequest(link, Unbind.class, 6);
        link.write(packet, true);
        doResponse(link, UnbindResp.class, 7);
    }

    @Test
    public void testPacketIsNotDiscardedWhenReadTimeoutOccurs() throws Exception {
        ReplayLink link = new ReplayLink(inbound, outbound);
        link.setTimeout(500);
        link.connect();
        try {
            link.read();
            fail("Should have thrown ReadTimeoutException.");
        } catch (ReadTimeoutException x) {
        }
        link.write(link.getNextOutbound(), true);
        SMPPPacket packet = link.read();
        assertPacket(packet, BindTransceiverResp.class, 1);
    }
    
    @BeforeMethod
    void setupStreams() throws Exception {
        ByteArrayOutputStream outPackets = new ByteArrayOutputStream();
        ByteArrayOutputStream inPackets = new ByteArrayOutputStream();
        PacketEncoder outEncoder = new PacketEncoderImpl(outPackets);
        PacketEncoder inEncoder = new PacketEncoderImpl(inPackets);
        addBindPackets(outEncoder, inEncoder);
        addSubmitPackets(outEncoder, inEncoder);
        addDeliverPackets(outEncoder, inEncoder);
        addUnbindPackets(outEncoder, inEncoder);
        inbound = new ByteArrayInputStream(inPackets.toByteArray());
        outbound = new ByteArrayInputStream(outPackets.toByteArray());
    }

    private SMPPPacket doRequest(ReplayLink link, Class<?> type, int sessionIndex) throws Exception {
        SMPPPacket packet = link.getNextOutbound();
        assertPacket(packet, type, sessionIndex);
        return packet;
    }
    
    private void doResponse(ReplayLink link, Class<?> type, int sessionIndex) throws Exception {
        assertPacket(link.read(), type, sessionIndex);
    }
    
    private void assertPacket(SMPPPacket packet, Class<?> type, int sessionIndex) {
        assertNotNull(packet);
        assertEquals(packet.getClass(), type);
        assertEquals(packet, session.get(sessionIndex));
    }
    
    private void addBindPackets(PacketEncoder outEncoder, PacketEncoder inEncoder) throws IOException {
        BindTransceiver bind = new BindTransceiver();
        bind.setSequenceNum(1L);
        bind.setSystemId("systemId");
        bind.setPassword("password");
        bind.setSystemType("systemType");
        bind.setAddressTon(1);
        bind.setAddressNpi(1);
        bind.setAddressRange("12345[67]");
        BindTransceiverResp bindResp = new BindTransceiverResp(bind);
        bindResp.setSystemId("smsc");
        bindResp.setTLV(Tag.SC_INTERFACE_VERSION,
                new Integer(SMPPVersion.VERSION_5_0.getVersionID()));
        bind.writeTo(outEncoder);
        bindResp.writeTo(inEncoder);
        session.add(bind);
        session.add(bindResp);
    }

    private void addSubmitPackets(PacketEncoder outEncoder, PacketEncoder inEncoder) throws IOException {
        SubmitSM submit = new SubmitSM();
        submit.setSequenceNum(2L);
        submit.setDestination(new Address(1, 1, "54321"));
        submit.setMessage("Message Text".getBytes("US-ASCII"));
        SubmitSMResp submitResp = new SubmitSMResp(submit);
        submitResp.setMessageId("message-1");
        submit.writeTo(outEncoder);
        submitResp.writeTo(inEncoder);
        session.add(submit);
        session.add(submitResp);
    }

    private void addDeliverPackets(PacketEncoder outEncoder, PacketEncoder inEncoder) throws IOException {
        DeliverSM deliver = new DeliverSM();
        deliver.setSequenceNum(1L);
        deliver.setDestination(new Address(1, 1, "123456"));
        deliver.setMessage("Another Message".getBytes("US-ASCII"));
        DeliverSMResp deliverResp = new DeliverSMResp(deliver);
        deliverResp.writeTo(outEncoder);
        deliver.writeTo(inEncoder);
        session.add(deliver);
        session.add(deliverResp);
    }
    
    private void addUnbindPackets(PacketEncoder outEncoder, PacketEncoder inEncoder) throws IOException {
        Unbind unbind = new Unbind();
        unbind.setSequenceNum(3L);
        UnbindResp unbindResp = new UnbindResp(unbind);
        unbind.writeTo(outEncoder);
        unbindResp.writeTo(inEncoder);
        session.add(unbind);
        session.add(unbindResp);
    }
}
