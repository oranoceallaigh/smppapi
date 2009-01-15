package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class OutbindTest extends PacketTests<Outbind> {

    protected Class<Outbind> getPacketType() {
        return Outbind.class;
    }
    
    @Override
    protected Outbind getInitialisedPacket() {
        Outbind packet = new Outbind();
        packet.setSystemId("systemId");
        packet.setPassword("password");
        return packet;
    }
}
