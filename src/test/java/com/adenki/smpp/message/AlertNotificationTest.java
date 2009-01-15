package com.adenki.smpp.message;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.Npi;
import com.adenki.smpp.Ton;

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
