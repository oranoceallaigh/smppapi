package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.message.tlv.Tag;

@Test
public class BindTransmitterRespTest extends PacketTests<BindTransmitterResp> {

    protected Class<BindTransmitterResp> getPacketType() {
        return BindTransmitterResp.class;
    }
    
    @Override
    protected BindTransmitterResp getInitialisedPacket() {
        BindTransmitterResp packet = new BindTransmitterResp ();
        packet.setSystemId("systemId");
        packet.setTLV(Tag.SC_INTERFACE_VERSION, new Integer(0x50));
        return packet;
    }
}
