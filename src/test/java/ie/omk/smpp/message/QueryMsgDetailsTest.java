package ie.omk.smpp.message;

import ie.omk.smpp.Address;

import org.testng.annotations.Test;

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
