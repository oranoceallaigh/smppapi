package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class SubmitSMRespTest extends PacketTests<SubmitSMResp> {

    protected Class<SubmitSMResp> getPacketType() {
        return SubmitSMResp.class;
    }
    
    @Override
    protected SubmitSMResp getInitialisedPacket() {
        SubmitSMResp packet = new SubmitSMResp();
        packet.setMessageId("messageId");
        return packet;
    }
}
