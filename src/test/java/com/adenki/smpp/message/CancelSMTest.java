package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class CancelSMTest extends PacketTests<CancelSM> {

    protected Class<CancelSM> getPacketType() {
        return CancelSM.class;
    }
    
    @Override
    protected CancelSM getInitialisedPacket() {
        CancelSM packet = new CancelSM();
        packet.setSource(new Address(1, 2, "34567"));
        packet.setDestination(new Address(0, 0, "67890"));
        packet.setMessageId("messageId");
        packet.setServiceType("serviceType");
        return packet;
    }
}
