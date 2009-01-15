package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class QueryLastMsgsTest extends PacketTests<QueryLastMsgs> {

    protected Class<QueryLastMsgs> getPacketType() {
        return QueryLastMsgs.class;
    }
    
    @Override
    protected QueryLastMsgs getInitialisedPacket() {
        QueryLastMsgs packet = new QueryLastMsgs();
        packet.setSource(new Address(9, 9, "999999999"));
        packet.setMsgCount(4);
        return packet;
    }
}
