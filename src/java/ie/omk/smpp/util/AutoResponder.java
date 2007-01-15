package ie.omk.smpp.util;

import java.io.IOException;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.SMPPPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A connection observer that can automatically respond to some of the basic
 * packet types. An instance of this class can be added as an observer to
 * a {@link ie.omk.smpp.Connection} and configured to send response packets
 * to any of unbind, deliver_sm, data_sm or enquire_link requests.
 * <p>
 * After construction, the default configuration for an <code>AutoResponder
 * </code> is not to respond to anything. Each setting must be explicitly
 * enabled by the caller. As an example, in order to respond to enquire_link
 * and deliver_sm packets but to ignore bind and data_sm packets, code like
 * the following could be used:
 * <pre>
 * Connection connection = new Connection(...);
 * AutoResponder responder = new AutoResponder();
 * responder.setAckDeliverSm(true);
 * responder.setAckEnqureLink(true);
 * connection.addObserver(responder);
 * </pre>
 * </p>
 * @version $Id:$
 */
public class AutoResponder implements ConnectionObserver {
    private static final Logger LOG = LoggerFactory.getLogger(AutoResponder.class);
    
    private boolean ackUnbind;
    private boolean ackDeliverSm;
    private boolean ackDataSm;
    private boolean ackEnquireLink;
    
    public boolean isAckDataSm() {
        return ackDataSm;
    }

    public void setAckDataSm(boolean ackDataSm) {
        this.ackDataSm = ackDataSm;
    }

    public boolean isAckDeliverSm() {
        return ackDeliverSm;
    }

    public void setAckDeliverSm(boolean ackDeliverSm) {
        this.ackDeliverSm = ackDeliverSm;
    }

    public boolean isAckEnquireLink() {
        return ackEnquireLink;
    }

    public void setAckEnquireLink(boolean ackEnquireLink) {
        this.ackEnquireLink = ackEnquireLink;
    }

    public boolean isAckUnbind() {
        return ackUnbind;
    }

    public void setAckUnbind(boolean ackUnbind) {
        this.ackUnbind = ackUnbind;
    }

    public void packetReceived(Connection source, SMPPPacket packet) {
        switch (packet.getCommandId()) {
        case SMPPPacket.DELIVER_SM:
            if (ackDeliverSm) {
                respond(source, packet);
            }
            break;
        case SMPPPacket.DATA_SM:
            if (ackDataSm) {
                respond(source, packet);
            }
            break;
        case SMPPPacket.ENQUIRE_LINK:
            if (ackEnquireLink) {
                respond(source, packet);
            }
            break;
        case SMPPPacket.UNBIND:
            if (ackUnbind) {
                respond(source, packet);
            }
            break;
        }
    }

    public void update(Connection source, SMPPEvent event) {
    }
    
    private void respond(Connection connection, SMPPPacket packet) {
        try {
            SMPPPacket response = PacketFactory.newResponse(packet);
            connection.sendPacket(response);
        } catch (IOException x) {
            LOG.error("IOException while trying to respond to packet {}: {}",
                    packet, x.getMessage());
            LOG.debug("Stack trace", x);
        }
    }
}
