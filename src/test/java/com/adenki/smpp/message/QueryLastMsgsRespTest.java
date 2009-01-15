package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class QueryLastMsgsRespTest extends PacketTests<QueryLastMsgsResp> {

    protected Class<QueryLastMsgsResp> getPacketType() {
        return QueryLastMsgsResp.class;
    }
    
    @Override
    protected QueryLastMsgsResp getInitialisedPacket() {
        QueryLastMsgsResp packet = new QueryLastMsgsResp();
        packet.addMessageId("messageId1");
        packet.addMessageId("messageId2");
        packet.addMessageId("messageId3");
        packet.addMessageId("messageId4");
        packet.addMessageId("messageId5");
        return packet;
    }
}
