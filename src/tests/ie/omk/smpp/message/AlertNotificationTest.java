package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.Npi;
import ie.omk.smpp.Ton;

import org.testng.annotations.Test;

@Test
public class AlertNotificationTest extends PacketTests<AlertNotification> {

    protected Class<AlertNotification> getPacketType() {
        return AlertNotification.class;
    }
    
    @Override
    protected AlertNotification getInitialisedPacket() {
        AlertNotification n = new AlertNotification();
        n.setSource(new Address(Ton.ALPHANUMERIC, Npi.IP, "10.10.10.1"));
        n.setDestination(new Address(Ton.ABBREVIATED, Npi.ERMES, "12878475"));
        return n;
    }
}
