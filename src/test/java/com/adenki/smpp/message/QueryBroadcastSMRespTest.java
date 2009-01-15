package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class QueryBroadcastSMRespTest extends PacketTests<QueryBroadcastSMResp> {

    protected Class<QueryBroadcastSMResp> getPacketType() {
        return QueryBroadcastSMResp.class;
    }
    
    @Override
    protected QueryBroadcastSMResp getInitialisedPacket() {
        QueryBroadcastSMResp packet = new QueryBroadcastSMResp();
        packet.setMessageId("messageId");
        return packet;
    }
}
