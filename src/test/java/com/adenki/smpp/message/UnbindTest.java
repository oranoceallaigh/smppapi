package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class UnbindTest extends PacketTests<Unbind> {

    protected Class<Unbind> getPacketType() {
        return Unbind.class;
    }
    
    @Override
    protected Unbind getInitialisedPacket() {
        return new Unbind();
    }
}
