package ie.omk.smpp.message;

import java.util.Calendar;

import ie.omk.smpp.util.SMPPDate;

import org.testng.annotations.Test;

@Test
public class QuerySMRespTest extends PacketTests<QuerySMResp> {

    protected Class<QuerySMResp> getPacketType() {
        return QuerySMResp.class;
    }
    
    @Override
    protected QuerySMResp getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        QuerySMResp packet = new QuerySMResp();
        packet.setErrorCode(2);
        packet.setFinalDate(SMPPDate.getAbsoluteInstance(calendar));
        packet.setMessageId("messageId");
        packet.setMessageState(MessageState.DELIVERED);
        return packet;
    }
}
