package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class QuerySMTest extends PacketTests<QuerySM> {

    protected Class<QuerySM> getPacketType() {
        return QuerySM.class;
    }
    
    @Override
    protected QuerySM getInitialisedPacket() {
        QuerySM packet = new QuerySM();
        packet.setMessageId("messageId");
        packet.setSource(new Address(5, 6, "789101112"));
        return packet;
    }
}
