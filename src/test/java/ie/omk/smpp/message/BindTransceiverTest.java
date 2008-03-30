package ie.omk.smpp.message;

import ie.omk.smpp.Npi;
import ie.omk.smpp.Ton;

import org.testng.annotations.Test;

@Test
public class BindTransceiverTest extends PacketTests<BindTransceiver> {

    protected Class<BindTransceiver> getPacketType() {
        return BindTransceiver.class;
    }
    
    @Override
    protected BindTransceiver getInitialisedPacket() {
        BindTransceiver packet = new BindTransceiver();
        packet.setSystemId("systemId");
        packet.setPassword("password");
        packet.setSystemType("systemType");
        packet.setAddressTon(Ton.UNKNOWN);
        packet.setAddressNpi(Npi.PRIVATE);
        packet.setAddressRange("1234[2134]+");
        return packet;
    }
}
