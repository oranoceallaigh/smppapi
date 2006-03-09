package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;

class NotificationDetails {
    public Connection conn = null;

    public SMPPEvent event = null;

    public SMPPPacket pak = null;

    public NotificationDetails() {
    }

    public void setDetails(Connection c, SMPPEvent e, SMPPPacket p) {
        conn = c;
        event = e;
        pak = p;
    }
}
