package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.Npi;
import com.adenki.smpp.Ton;

@Test
public class CancelBroadcastSMTest extends PacketTests<CancelBroadcastSM> {

    protected Class<CancelBroadcastSM> getPacketType() {
        return CancelBroadcastSM.class;
    }
    
    @Override
    protected CancelBroadcastSM getInitialisedPacket() {
        CancelBroadcastSM packet = new CancelBroadcastSM();
        packet.setMessageId("messageId");
        packet.setServiceType("serviceType");
        packet.setSource(
                new Address(Ton.INTERNATIONAL, Npi.LAND_MOBILE, "878756568"));
        return packet;
    }
}
