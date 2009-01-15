package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class BroadcastSMRespTest extends PacketTests<BroadcastSMResp> {

    protected Class<BroadcastSMResp> getPacketType() {
        return BroadcastSMResp.class;
    }

    @Override
    protected BroadcastSMResp getInitialisedPacket() {
        BroadcastSMResp packet = new BroadcastSMResp();
        packet.setMessageId("messageId");
        return packet;
    }
}
