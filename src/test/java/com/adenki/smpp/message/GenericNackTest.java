package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class GenericNackTest extends PacketTests<GenericNack> {

    protected Class<GenericNack> getPacketType() {
        return GenericNack.class;
    }
    
    @Override
    protected GenericNack getInitialisedPacket() {
        return new GenericNack();
    }
}
