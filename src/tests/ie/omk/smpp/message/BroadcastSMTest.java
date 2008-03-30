package ie.omk.smpp.message;

import java.util.Calendar;

import ie.omk.smpp.Address;
import ie.omk.smpp.Npi;
import ie.omk.smpp.Ton;
import ie.omk.smpp.util.SMPPDate;

import org.testng.annotations.Test;

@Test
public class BroadcastSMTest extends PacketTests<BroadcastSM> {

    protected Class<BroadcastSM> getPacketType() {
        return BroadcastSM.class;
    }

    @Override
    protected BroadcastSM getInitialisedPacket() {
        Calendar calendar = Calendar.getInstance();
        BroadcastSM packet = new BroadcastSM();
        packet.setDataCoding(3);
        packet.setDefaultMsg(5);
        packet.setDeliveryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setExpiryTime(SMPPDate.getAbsoluteInstance(calendar));
        packet.setMessageId("messageId");
        packet.setPriority(1);
        packet.setReplaceIfPresent(1);
        packet.setServiceType("serviceType");
        packet.setSource(new Address(Ton.UNKNOWN, Npi.UNKNOWN, "54321"));
        return packet;
    }
}
