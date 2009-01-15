package com.adenki.smpp.message;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.SMPPDate;

@Test
public class ReplaceSMTest extends PacketTests<ReplaceSM> {

    protected Class<ReplaceSM> getPacketType() {
        return ReplaceSM.class;
    }
    
    @Override
    protected ReplaceSM getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        ReplaceSM packet = new ReplaceSM();
        packet.setMessageId("messageId");
        packet.setDefaultMsg(2);
        packet.setDeliveryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setExpiryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setRegistered(1);
        packet.setSource(new Address(2, 2, "32873487"));
        packet.setMessage(new byte[] {1, 2, 3, 4, 5, 6});
        return packet;
    }
}
