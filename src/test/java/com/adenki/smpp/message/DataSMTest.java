package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class DataSMTest extends PacketTests<DataSM> {

    protected Class<DataSM> getPacketType() {
        return DataSM.class;
    }
    
    @Override
    protected DataSM getInitialisedPacket() {
        DataSM packet = new DataSM();
        packet.setDataCoding(4);
        packet.setDestination(new Address(2, 2, "22222222"));
        packet.setEsmClass(32);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(1, 1, "111111111111"));
        packet.setRegistered(0);
        return packet;
    }
}
