package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;

class NotificationDetails {
    private Connection connection = null;
    private SMPPEvent event = null;
    private SMPPPacket packet = null;

    public NotificationDetails() {
    }
    
    public Connection getConnection() {
        return connection;
    }


    public void setConnection(Connection conn) {
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


    public void setDetails(Connection c, SMPPEvent e, SMPPPacket p) {
        connection = c;
        event = e;
        packet = p;
    }
    
    public boolean hasEvent() {
        return event != null;
    }
}
