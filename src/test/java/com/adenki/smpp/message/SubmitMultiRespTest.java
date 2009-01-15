package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.ErrorAddress;

@Test
public class SubmitMultiRespTest extends PacketTests<SubmitMultiResp> {

    protected Class<SubmitMultiResp> getPacketType() {
        return SubmitMultiResp.class;
    }
    
    @Override
    protected SubmitMultiResp getInitialisedPacket() {
        SubmitMultiResp packet = new SubmitMultiResp();
        packet.setMessageId("messageId");
        packet.add(new ErrorAddress(1, 1, "11111"));
        packet.add(new ErrorAddress(2, 2, "22222"));
        packet.add(new ErrorAddress(3, 3, "33333"));
        return packet;
    }
}
