package ie.omk.smpp.examples;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.util.GSMConstants;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

/**
 * Example class to submit a message to a SMSC using asynchronous communication.
 * This class simply binds to the server, submits a message, waits for the
 * submit response from the server and unbinds.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncTransmitter extends SMPPAPIExample implements ConnectionObserver {

    private Object blocker = new Object();

    private Log logger = LogFactory.getLog(AsyncTransmitter.class);

    // This is called when the connection receives a packet from the SMSC.
    public void update(Connection t, SMPPEvent ev) {
        switch (ev.getType()) {
        case SMPPEvent.RECEIVER_EXIT:
            receiverExit(t, (ReceiverExitEvent) ev);
            break;
        }
    }

    public void packetReceived(Connection myConnection, SMPPPacket pak) {
        logger.info("Packet received: Id = "
                + Integer.toHexString(pak.getCommandId()));
        switch (pak.getCommandId()) {

        // Bind transmitter response. Check it's status for success...
        case SMPPPacket.BIND_TRANSMITTER_RESP:
            if (pak.getCommandStatus() != 0) {
                logger.info("Error binding to the SMSC. Error = "
                        + pak.getCommandStatus());

                synchronized (blocker) {
                    blocker.notify();
               }
            } else {
                logger.info("Successfully bound to SMSC \""
                        + ((BindTransmitterResp) pak).getSystemId()
                        + "\".\n\tSubmitting message...");
                send(myConnection);
            }
            break;

        // Submit message response...
        case SMPPPacket.SUBMIT_SM_RESP:
            if (pak.getCommandStatus() != 0) {
                logger.info("Message was not submitted. Error code: "
                        + pak.getCommandStatus());
            } else {
                logger.info("Message Submitted! Id = "
                        + ((SubmitSMResp) pak).getMessageId());
            }

            try {
                // Unbind. The Connection's listener thread will stop itself..
                myConnection.unbind();
            } catch (IOException x) {
                logger.info("IO exception" + x.toString());
                synchronized (blocker) {
                    blocker.notify();
               }
            }
            break;

        // Unbind response..
        case SMPPPacket.UNBIND_RESP:
            logger.info("Unbound.");
            synchronized (blocker) {
                blocker.notify();
            }
            break;

        default:
            logger
                    .info("Unknown response received! Id = "
                            + pak.getCommandId());
        }
    }

    private void receiverExit(Connection myConnection, ReceiverExitEvent ev) {
        if (ev.getReason() != ReceiverExitEvent.EXCEPTION) {
            if (ev.getReason() == ReceiverExitEvent.BIND_TIMEOUT) {
                logger.info("Bind timed out waiting for response.");
            }
            logger.info("Receiver thread has exited normally.");
        } else {
            Throwable t = ev.getException();
            logger.info("Receiver thread died due to exception:");
            logger.warn("Exception", t);
        }

        synchronized (blocker) {
            blocker.notify();
        }
    }

    // Send a short message to the SMSC
    public void send(Connection myConnection) {
        try {
            Address destination = new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "87654321");

            SubmitSM sm = (SubmitSM) myConnection.newInstance(SMPPPacket.SUBMIT_SM);
            sm.setDestination(destination);
            sm.setMessageText("Test Short Message. :-)");

            myConnection.sendRequest(sm);
        } catch (IOException x) {
            logger.warn("I/O Exception", x);
        } catch (SMPPException x) {
            logger.warn("SMPP Exception", x);
        }
    }

    public void execute() throws BuildException {
        try {
            myConnection = new Connection(hostName, port, true);
            
            // Need to add myself to the list of listeners for this connection
            myConnection.addObserver(this);

            // Automatically respond to ENQUIRE_LINK requests from the SMSC
            myConnection.autoAckLink(true);

            // Bind to the SMSC (as a transmitter)
            logger.info("Binding to the SMSC..");
            myConnection.bind(
                    Connection.TRANSMITTER,
                    systemID,
                    password,
                    systemType,
                    sourceTON,
                    sourceNPI,
                    sourceAddress);

            synchronized (blocker) {
                blocker.wait();
            }
        } catch (Exception x) {
            throw new BuildException("Exception running example: " + x.getMessage(), x);
        }
    }
}

