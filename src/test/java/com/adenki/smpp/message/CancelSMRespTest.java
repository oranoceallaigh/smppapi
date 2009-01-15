package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class CancelSMRespTest extends PacketTests<CancelSMResp> {

    protected Class<CancelSMResp> getPacketType() {
        return CancelSMResp.class;
    }
    
    @Override
    protected CancelSMResp getInitialisedPacket() {
        return new CancelSMResp();
    }
}
