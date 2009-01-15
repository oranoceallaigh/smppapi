package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class CancelBroadcastSMRespTest extends PacketTests<CancelBroadcastSMResp> {

    protected Class<CancelBroadcastSMResp> getPacketType() {
        return CancelBroadcastSMResp.class;
    }
    
    @Override
    protected CancelBroadcastSMResp getInitialisedPacket() {
        return new CancelBroadcastSMResp();
    }
}
