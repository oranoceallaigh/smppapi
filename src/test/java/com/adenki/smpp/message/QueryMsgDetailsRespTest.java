package com.adenki.smpp.message;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.SMPPDate;

@Test
public class QueryMsgDetailsRespTest extends PacketTests<QueryMsgDetailsResp> {

    protected Class<QueryMsgDetailsResp> getPacketType() {
        return QueryMsgDetailsResp.class;
    }
    
    @Override
    protected QueryMsgDetailsResp getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        QueryMsgDetailsResp packet = new QueryMsgDetailsResp();
        packet.setDataCoding(2);
        packet.setDeliveryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setErrorCode(4);
        packet.setExpiryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setFinalDate(SMPPDate.getAbsoluteInstance(calendar));
        packet.setMessage(new byte[] {5, 4, 3, 2, 1});
        packet.setMessageId("messageId");
        packet.setMessageStatus(MessageState.EN_ROUTE);
        packet.setPriority(1);
        packet.setProtocolID(1);
        packet.setRegistered(1);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(6, 6, "55555555"));
        packet.addDestination(new Address(1, 1, "11111111"));
        packet.addDestination(new Address(2, 2, "22222222"));
        packet.addDestination(new Address(3, 3, "33333333"));
        packet.addDestination("distList1");
        packet.addDestination("distList2");
        return packet;
    }
}
