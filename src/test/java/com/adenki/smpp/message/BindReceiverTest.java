package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Npi;
import com.adenki.smpp.Ton;

@Test
public class BindReceiverTest extends PacketTests<BindReceiver> {

    protected Class<BindReceiver> getPacketType() {
        return BindReceiver.class;
    }
    
    @Override
    protected BindReceiver getInitialisedPacket() {
        BindReceiver packet = new BindReceiver();
        packet.setSystemId("systemId");
        packet.setPassword("password");
        packet.setSystemType("systemType");
        packet.setAddressTon(Ton.UNKNOWN);
        packet.setAddressNpi(Npi.PRIVATE);
        packet.setAddressRange("1234[2134]+");
        return packet;
    }
}
