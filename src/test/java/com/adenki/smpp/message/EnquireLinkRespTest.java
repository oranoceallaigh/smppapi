package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class EnquireLinkRespTest extends PacketTests<EnquireLinkResp> {

    protected Class<EnquireLinkResp> getPacketType() {
        return EnquireLinkResp.class;
    }
    
    @Override
    protected EnquireLinkResp getInitialisedPacket() {
        return new EnquireLinkResp();
    }
}
