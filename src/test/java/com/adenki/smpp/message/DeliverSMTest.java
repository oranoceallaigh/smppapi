package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class DeliverSMTest extends PacketTests<DeliverSM> {

    protected Class<DeliverSM> getPacketType() {
        return DeliverSM.class;
    }
    
    @Override
    protected DeliverSM getInitialisedPacket() {
        DeliverSM packet = new DeliverSM();
        packet.setDataCoding(0);
        packet.setDefaultMsg(7);
        packet.setDestination(new Address(0, 0, "27934876984"));
        packet.setEsmClass(10);
        packet.setPriority(1);
        packet.setProtocolID(1);
        packet.setRegistered(1);
        packet.setReplaceIfPresent(1);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(1, 1, "242357987876"));
        packet.setMessage(new byte[] {0x1, 0x2, 0x3, 0x4, 0x5, 0x6});
        return packet;
    }
}
