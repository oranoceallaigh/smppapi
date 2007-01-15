package ie.omk.smpp.examples;

import ie.omk.smpp.Connection;
import ie.omk.smpp.ConnectionType;
import ie.omk.smpp.TextMessage;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.util.AutoResponder;

import java.io.IOException;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example SMPP receiver using asynchronous communications. This example
 * demonstrates asynchronous communications by implementing the
 * ConnectionObserver interface and directly handling all receiver events.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncReceiver extends SMPPAPIExample implements ConnectionObserver {

    private Logger logger = LoggerFactory.getLogger(AsyncReceiver.class);

    // Number of deliver_sm packets received during the session.
    private int msgCount;

    // Start time (once successfully bound).
    private long start;

    // End time (once unbound).
    private long end;

    // This is called when the connection receives a non-packet event
    // from the SMSC.
    public void update(Connection r, SMPPEvent ev) {
        switch (ev.getType()) {
        case SMPPEvent.RECEIVER_EXIT:
            receiverExit(r, (ReceiverExitEvent) ev);
            break;
        }
    }

    public void packetReceived(Connection myConnection, SMPPPacket pak) {
        switch (pak.getCommandId()) {

        // Bind receiver response. Check it's status for success...
        case SMPPPacket.BIND_RECEIVER_RESP:
            handleBindResponse((BindResp) pak);
            break;

        // Submit message response...
        case SMPPPacket.DELIVER_SM:
            handleDeliverSm((DeliverSM) pak);
            break;

        // Unbind request received from server..
        case SMPPPacket.UNBIND:
            handleUnbind((Unbind) pak);
            break;

        // Unbind response..
        case SMPPPacket.UNBIND_RESP:
            handleUnbindResponse((UnbindResp) pak);
            break;

        default:
            handleOtherPacket(pak);
        }
    }


    public void execute() throws BuildException {
        try {
            createConnection();

            // Automatically respond to enqure_link and deliver_sm requests
            // from the SMSC
            AutoResponder autoResponder = new AutoResponder();
            autoResponder.setAckDeliverSm(true);
            autoResponder.setAckEnquireLink(true);
            myConnection.addObserver(autoResponder);

            // Need to add myself to the list of listeners for this connection
            myConnection.addObserver(this);

            // Bind to the SMSC
            logger.info("Binding to the SMSC..");

            synchronized (this) {
                doBind(ConnectionType.RECEIVER);
                logger.info("Waiting for receiver thread to exit...");
                wait();
            }
        } catch (Exception x) {
            throw new BuildException("Exception occurred while running example: " + x.getMessage() , x);
        } finally {
            closeConnection();
            end = System.currentTimeMillis();
            endReport();
        }
    }

    private void handleBindResponse(BindResp bindResponse) {
        if (bindResponse.getCommandStatus() != 0) {
            logger.info("Error binding to the SMSC. Error = {}",
                    bindResponse.getCommandStatus());
            synchronized (this) {
                notify();
            }
        } else {
            this.start = System.currentTimeMillis();
            logger.info("Successfully bound. Waiting for message delivery..");

            synchronized (this) {
                // This code is here to prevent the main thread exiting,
                // that is all.
                new String("Dummy code to do nothing in particular").hashCode();
           }
        }
    }
    
    private void handleUnbind(Unbind unbind) {
        logger.info("SMSC has requested unbind! Responding..");
        try {
            UnbindResp response = new UnbindResp(unbind);
            myConnection.sendPacket(response);
            
            synchronized (this) {
                notify();
            }
        } catch (IOException x) {
            logger.error("Got an exception", x);
        }
    }
    
    private void handleUnbindResponse(UnbindResp unbindResponse) {
        if (unbindResponse.getCommandStatus() != 0) {
            logger.error("Got an unbind response with non-zero status: {}",
                    unbindResponse.getCommandStatus());
        } else {
            logger.info("Successfully unbound.");
            synchronized (this) {
                notify();
            }
        }
    }

    private void handleDeliverSm(DeliverSM deliverSm) {
        if (deliverSm.getCommandStatus() != 0) {
            logger.info("Deliver SM with an error! status code = {}",
                    deliverSm.getCommandStatus());

        } else {
            ++msgCount;
            TextMessage textMessage = new TextMessage(deliverSm);
            logger.info("deliver_sm: sequence = {}, text = \"{}\"",
                    deliverSm.getSequenceNum(), textMessage.getMessageText());
        }
    }

    private void handleOtherPacket(SMPPPacket packet) {
        logger.info("Received a packet, commandId = 0x{}, sequence = {}",
                Integer.toHexString(packet.getCommandId()),
                packet.getSequenceNum());
    }
    
    private void receiverExit(Connection myConnection, ReceiverExitEvent event) {
        if (event.getReason() != ReceiverExitEvent.EXCEPTION) {
            if (event.getReason() == ReceiverExitEvent.BIND_TIMEOUT) {
                logger.info("Bind timed out waiting for response.");
            }
            logger.info("Receiver thread has exited: " + event.getReason());
        } else {
            Throwable t = event.getException();
            logger.info("Receiver thread died due to exception:");
            logger.warn("Exception", t);
        }
        synchronized (this) {
            notify();
        }
    }

    // Print out a report
    private void endReport() {
        logger.info("deliver_sm's received: {}", msgCount);
        logger.info("Start time: {}", new Date(start));
        logger.info("End time: {}", new Date(end));
        logger.info("Elapsed: {} milliseconds.", (end - start));
    }
}
