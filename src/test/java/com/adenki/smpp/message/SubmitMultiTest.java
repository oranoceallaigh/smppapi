package com.adenki.smpp.message;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.SMPPDate;

@Test
public class SubmitMultiTest extends PacketTests<SubmitMulti> {

    protected Class<SubmitMulti> getPacketType() {
        return SubmitMulti.class;
    }
    
    @Override
    protected SubmitMulti getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        SubmitMulti packet = new SubmitMulti();
        packet.setDataCoding(1);
        packet.setDefaultMsg(2);
        packet.setDeliveryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setEsmClass(2);
        packet.setExpiryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setMessage(new byte[] {9, 8, 7, 6, 5, 4, 3, 2, 1});
        packet.setPriority(1);
        packet.setProtocolID(2);
        packet.setRegistered(1);
        packet.setReplaceIfPresent(1);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(1, 2, "345678"));
        packet.addDestination(new Address(1, 1, "11111"));
        packet.addDestination(new Address(2, 2, "22222"));
        packet.addDestination("distList1");
        packet.addDestination("distList2");
        return packet;
    }
}
