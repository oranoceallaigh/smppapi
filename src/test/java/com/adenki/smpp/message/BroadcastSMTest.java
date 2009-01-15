package com.adenki.smpp.message;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.Npi;
import com.adenki.smpp.Ton;
import com.adenki.smpp.util.SMPPDate;

@Test
public class BroadcastSMTest extends PacketTests<BroadcastSM> {

    protected Class<BroadcastSM> getPacketType() {
        return BroadcastSM.class;
    }

    @Override
    protected BroadcastSM getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        BroadcastSM packet = new BroadcastSM();
        packet.setDataCoding(3);
        packet.setDefaultMsg(5);
        packet.setDeliveryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setExpiryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setMessageId("messageId");
        packet.setPriority(1);
        packet.setReplaceIfPresent(1);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(Ton.UNKNOWN, Npi.UNKNOWN, "54321"));
        return packet;
    }
}
