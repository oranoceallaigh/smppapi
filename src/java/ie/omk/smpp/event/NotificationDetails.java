package ie.omk.smpp.event;

import ie.omk.smpp.Session;
import ie.omk.smpp.message.SMPPPacket;

class NotificationDetails {
    private Session connection;
    private SMPPEvent event;
    private SMPPPacket packet;

    public NotificationDetails() {
    }
    
    public Session getConnection() {
        return connection;
    }


    public void setConnection(Session conn) {
        this.connection = conn;
    }


    public SMPPEvent getEvent() {
        return event;
    }


    public void setEvent(SMPPEvent event) {
        this.event = event;
    }


    public SMPPPacket getPacket() {
        return packet;
    }


    public void setPacket(SMPPPacket pak) {
        this.packet = pak;
    }


    public void setDetails(Session c, SMPPEvent e, SMPPPacket p) {
        connection = c;
        event = e;
        packet = p;
    }
    
    public boolean hasEvent() {
        return event != null;
    }
}
