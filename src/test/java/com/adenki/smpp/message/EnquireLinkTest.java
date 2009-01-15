package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class EnquireLinkTest extends PacketTests<EnquireLink> {

    protected Class<EnquireLink> getPacketType() {
        return EnquireLink.class;
    }
    
    @Override
    protected EnquireLink getInitialisedPacket() {
        return new EnquireLink();
    }
}
