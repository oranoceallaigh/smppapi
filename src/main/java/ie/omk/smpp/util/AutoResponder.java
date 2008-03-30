package ie.omk.smpp.util;

import ie.omk.smpp.Session;
import ie.omk.smpp.event.SessionObserver;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.CommandId;
import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DataSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A connection observer that can automatically respond to some of the basic
 * packet types. An instance of this class can be added as an observer to
 * a {@link ie.omk.smpp.Session} and configured to send response packets
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
public class AutoResponder implements SessionObserver {
    private static final Logger LOG = LoggerFactory.getLogger(AutoResponder.class);
    
    private boolean ackUnbind;
    private boolean ackDeliverSm;
    private boolean ackDataSm;
    private boolean ackEnquireLink;

    /**
     * Constructor that will initialise with all 'ack' properties initially set
     * to false.
     */
    public AutoResponder() {
    }
    
    /**
     * Constructor that will initialise with all 'ack' properties initially set
     * to <code>respond</code>.
     * @param respond The value to assign to all of this class' ack properties.
     */
    public AutoResponder(boolean respond) {
        ackUnbind = ackDeliverSm = ackDataSm = ackEnquireLink = respond;
    }
    
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

    public void packetReceived(Session source, SMPPPacket packet) {
        switch (packet.getCommandId()) {
        case CommandId.DELIVER_SM:
            if (ackDeliverSm) {
                respond(source, new DeliverSMResp((DeliverSM) packet));
            }
            break;
        case CommandId.DATA_SM:
            if (ackDataSm) {
                respond(source, new DataSMResp((DataSM) packet));
            }
            break;
        case CommandId.ENQUIRE_LINK:
            if (ackEnquireLink) {
                respond(source, new EnquireLinkResp((EnquireLink) packet));
            }
            break;
        case CommandId.UNBIND:
            if (ackUnbind) {
                respond(source, new UnbindResp((Unbind) packet));
            }
            break;
        }
    }

    public void update(Session source, SMPPEvent event) {
    }
    
    private void respond(Session connection, SMPPPacket response) {
        try {
            connection.sendPacket(response);
        } catch (IOException x) {
            LOG.error("IOException while trying to send packet {}: {}",
                    response, x.getMessage());
            LOG.debug("Stack trace", x);
        }
    }
}
