package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class DeliverSMRespTest extends PacketTests<DeliverSMResp> {

    protected Class<DeliverSMResp> getPacketType() {
        return DeliverSMResp.class;
    }
    
    @Override
    protected DeliverSMResp getInitialisedPacket() {
        DeliverSMResp packet = new DeliverSMResp();
        packet.setMessageId("messageId");
        return packet;
    }
}
