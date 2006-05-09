package ie.omk.smpp.examples;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

/**
 * Example SMPP receiver using asynchronous communications. This example
 * demonstrates asynchronous communications by implementing the
 * ConnectionObserver interface and directly handling all receiver events.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncReceiver extends SMPPAPIExample implements ConnectionObserver {

    private Log logger = LogFactory.getLog(AsyncReceiver.class);

    private static int msgCount = 0;

    // Start time (once successfully bound).
    private long start = 0;

    // End time (either send an unbind or an unbind received).
    private long end = 0;

    // This is called when the connection receives a packet from the SMSC.
    public void update(Connection r, SMPPEvent ev) {
        switch (ev.getType()) {
        case SMPPEvent.RECEIVER_EXIT:
            receiverExit(r, (ReceiverExitEvent) ev);
            break;
        }
    }

    public void packetReceived(Connection myConnection, SMPPPacket pak) {
        switch (pak.getCommandId()) {

        // Bind transmitter response. Check it's status for success...
        case SMPPPacket.BIND_RECEIVER_RESP:
            if (pak.getCommandStatus() != 0) {
                logger.info("Error binding to the SMSC. Error = "
                        + pak.getCommandStatus());
            } else {
                this.start = System.currentTimeMillis();
                logger.info("Successfully bound. Waiting for message"
                        + " delivery..");

                synchronized (this) {
                    // on exiting this block, we're sure that
                    // the main thread is now sitting in the wait
                    // call, awaiting the unbind request.
               }
            }
            break;

        // Submit message response...
        case SMPPPacket.DELIVER_SM:
            if (pak.getCommandStatus() != 0) {
                logger.info("Deliver SM with an error! "
                        + pak.getCommandStatus());

            } else {
                ++msgCount;
                logger.info("deliver_sm: "
                        + Integer.toString(pak.getSequenceNum()) + ": \""
                        + ((DeliverSM) pak).getMessageText() + "\"");
            }
            break;

        // Unbind request received from server..
        case SMPPPacket.UNBIND:
            this.end = System.currentTimeMillis();
            logger.info("\nSMSC has requested unbind! Responding..");
            try {
                UnbindResp ubr = new UnbindResp((Unbind) pak);
                myConnection.sendResponse(ubr);
                
                synchronized (this) {
                    notify();
               }
            } catch (IOException x) {
                logger.warn("Exception", x);
            } finally {
                endReport();
            }
            break;

        // Unbind response..
        case SMPPPacket.UNBIND_RESP:
            this.end = System.currentTimeMillis();
            logger.info("\nUnbound.");
            synchronized (this) {
                notify();
            }
            endReport();
            break;

        default:
            logger.info("\nUnexpected packet received! Id = 0x"
                    + Integer.toHexString(pak.getCommandId()));
        }
    }

    private void receiverExit(Connection myConnection, ReceiverExitEvent ev) {
        if (ev.getReason() != ReceiverExitEvent.EXCEPTION) {
            if (ev.getReason() == ReceiverExitEvent.BIND_TIMEOUT) {
                logger.info("Bind timed out waiting for response.");
            }
            logger.info("Receiver thread has exited: " + ev.getReason());
        } else {
            Throwable t = ev.getException();
            logger.info("Receiver thread died due to exception:");
            logger.warn("Exception", t);
            endReport();
        }
        synchronized (this) {
            notify();
        }
    }

    // Print out a report
    private void endReport() {
        logger.info("deliver_sm's received: " + msgCount);
        logger.info("Start time: " + new Date(start).toString());
        logger.info("End time: " + new Date(end).toString());
        logger.info("Elapsed: " + (end - start) + " milliseconds.");
    }

    public void execute() throws BuildException {
        try {
            myConnection = new Connection(hostName, port, true);
            
            // Need to add myself to the list of listeners for this connection
            myConnection.addObserver(this);

            // Automatically respond to ENQUIRE_LINK requests from the SMSC
            myConnection.autoAckLink(true);
            myConnection.autoAckMessages(true);

            // Bind to the SMSC
            logger.info("Binding to the SMSC..");

            synchronized (this) {
                myConnection.bind(
                        Connection.RECEIVER,
                        systemID,
                        password,
                        systemType,
                        sourceTON,
                        sourceNPI,
                        sourceAddress);
    
                logger.info("Waiting for unbind...");
                wait();
            }

            myConnection.closeLink();
        } catch (Exception x) {
            throw new BuildException("Exception occurred while running example: " + x.getMessage() , x);
        }
    }
}

