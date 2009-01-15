package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Npi;
import com.adenki.smpp.Ton;

@Test
public class BindTransmitterTest extends PacketTests<BindTransmitter> {

    protected Class<BindTransmitter> getPacketType() {
        return BindTransmitter.class;
    }
    
    @Override
    protected BindTransmitter getInitialisedPacket() {
        BindTransmitter packet = new BindTransmitter();
        packet.setSystemId("systemId");
        packet.setPassword("password");
        packet.setSystemType("systemType");
        packet.setAddressTon(Ton.UNKNOWN);
        packet.setAddressNpi(Npi.PRIVATE);
        packet.setAddressRange("1234[2134]+");
        return packet;
    }
}
