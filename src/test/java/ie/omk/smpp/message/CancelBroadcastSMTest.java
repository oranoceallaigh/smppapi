package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.Npi;
import ie.omk.smpp.Ton;

import org.testng.annotations.Test;

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
