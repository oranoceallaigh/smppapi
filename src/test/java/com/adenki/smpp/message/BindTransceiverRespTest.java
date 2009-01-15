package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.message.tlv.Tag;

@Test
public class BindTransceiverRespTest extends PacketTests<BindTransceiverResp> {

    protected Class<BindTransceiverResp> getPacketType() {
        return BindTransceiverResp.class;
    }

    @Override
    protected BindTransceiverResp getInitialisedPacket() {
        BindTransceiverResp packet = new BindTransceiverResp();
        packet.setSystemId("systemId");
        packet.setTLV(Tag.SC_INTERFACE_VERSION, new Integer(0x50));
        return packet;
    }
}
