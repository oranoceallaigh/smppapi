package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class QueryMsgDetailsTest extends PacketTests<QueryMsgDetails> {

    protected Class<QueryMsgDetails> getPacketType() {
        return QueryMsgDetails.class;
    }
    
    @Override
    protected QueryMsgDetails getInitialisedPacket() {
        QueryMsgDetails packet = new QueryMsgDetails();
        packet.setMessageId("messageId");
        packet.setSource(new Address(0, 0, "000000000"));
        packet.setSmLength(100);
        return packet;
    }
}
