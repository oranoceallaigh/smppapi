package com.adenki.smpp.message;

import com.adenki.smpp.message.tlv.Tag;

import org.testng.annotations.Test;

@Test
public class BindReceiverRespTest extends PacketTests<BindReceiverResp> {

    protected Class<BindReceiverResp> getPacketType() {
        return BindReceiverResp.class;
    }
    
    @Override
    protected BindReceiverResp getInitialisedPacket() {
        BindReceiverResp packet = new BindReceiverResp();
        packet.setSystemId("systemId");
        packet.setTLV(Tag.SC_INTERFACE_VERSION, new Integer(0x50));
        return packet;
    }
}
