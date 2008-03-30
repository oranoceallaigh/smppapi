package ie.omk.smpp.message;

import ie.omk.smpp.Address;

import org.testng.annotations.Test;

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
