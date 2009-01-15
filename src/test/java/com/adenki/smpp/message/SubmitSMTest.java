package com.adenki.smpp.message;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.SMPPDate;

@Test
public class SubmitSMTest extends PacketTests<SubmitSM> {

    protected Class<SubmitSM> getPacketType() {
        return SubmitSM.class;
    }
    
    @Override
    protected SubmitSM getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        SubmitSM packet = new SubmitSM();
        packet.setDataCoding(1);
        packet.setDefaultMsg(2);
        packet.setDeliveryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setDestination(new Address(0, 0, "8748746987"));
        packet.setEsmClass(2);
        packet.setExpiryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setPriority(1);
        packet.setProtocolID(1);
        packet.setRegistered(1);
        packet.setReplaceIfPresent(1);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(2, 2, "32873487"));
        packet.setMessage(new byte[] {1, 2, 3, 4, 5, 6});
        return packet;
    }
}
