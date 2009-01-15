package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class UnbindRespTest extends PacketTests<UnbindResp> {

    protected Class<UnbindResp> getPacketType() {
        return UnbindResp.class;
    }
    
    @Override
    protected UnbindResp getInitialisedPacket() {
        return new UnbindResp();
    }
}
