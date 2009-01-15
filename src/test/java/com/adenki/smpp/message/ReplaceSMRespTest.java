package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class ReplaceSMRespTest extends PacketTests<ReplaceSMResp> {

    protected Class<ReplaceSMResp> getPacketType() {
        return ReplaceSMResp.class;
    }
    
    @Override
    protected ReplaceSMResp getInitialisedPacket() {
        return new ReplaceSMResp();
    }
}
