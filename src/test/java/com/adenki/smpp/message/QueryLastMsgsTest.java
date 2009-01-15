package com.adenki.smpp.message;

import com.adenki.smpp.Address;

import org.testng.annotations.Test;

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
