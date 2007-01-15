package ie.omk.smpp.examples;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.ConnectionState;
import ie.omk.smpp.ConnectionType;
import ie.omk.smpp.TextMessage;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.util.AutoResponder;
import ie.omk.smpp.util.GSMConstants;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example class to submit a message to a SMSC using asynchronous communication.
 * This class simply binds to the server, submits a message, waits for the
 * submit response from the server and unbinds.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncTransmitter extends SMPPAPIExample implements ConnectionObserver {

    private Logger logger = LoggerFactory.getLogger(AsyncTransmitter.class);

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
            handleBindResponse((BindResp) pak);
            break;

        // Submit message response...
        case SMPPPacket.SUBMIT_SM_RESP:
            handleSubmitSmResponse((SubmitSMResp) pak);
            break;

        // Unbind response..
        case SMPPPacket.UNBIND_RESP:
            handleUnbindResponse((UnbindResp) pak);
            break;

        default:
            handleOtherPacket(pak);
        }
    }

    private void handleBindResponse(BindResp bindResponse) {
        if (bindResponse.getCommandStatus() != 0) {
            logger.info("Error binding to the SMSC. Status = {}",
                    bindResponse.getCommandStatus());
        } else {
            logger.info("Successfully bound to SMSC, identified itself as \"{}\"",
                    bindResponse.getSystemId());
        }
        synchronized (this) {
            notify();
        }
    }
    
    private void handleSubmitSmResponse(SubmitSMResp submitSMResp) {
        if (submitSMResp.getCommandStatus() != 0) {
            logger.info("Message was not submitted. Error code: {}",
                    submitSMResp.getCommandStatus());
        } else {
            logger.info("Message Submitted! message id = {}",
                    submitSMResp.getMessageId());
        }
        synchronized (this) {
            notify();
        }
    }
    
    private void handleUnbindResponse(UnbindResp unbindResponse) {
        if (unbindResponse.getCommandStatus() != 0) {
            logger.error("Got an unbind response with non-zero status: {}",
                    unbindResponse.getCommandStatus());
        } else {
            logger.info("Successfully unbound.");
        }
        synchronized (this) {
            notify();
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

    // Send a short message to the SMSC
    private void sendMessage() throws IOException {
        Address destination = new Address(GSMConstants.GSM_TON_UNKNOWN,
                GSMConstants.GSM_NPI_UNKNOWN, "87654321");

        TextMessage textMessage = new TextMessage("Test short message. :)");
        SubmitSM submitSm = textMessage.getSubmitSM(true);
        submitSm.setDestination(destination);
        myConnection.sendPacket(submitSm);
    }

    private void unbind() throws IOException {
        Unbind unbind = new Unbind();
        myConnection.sendPacket(unbind);
    }
    
    public void execute() throws BuildException {
        try {
            createConnection();
            
            // Automatically respond to ENQUIRE_LINK requests from the SMSC
            AutoResponder autoResponder = new AutoResponder();
            autoResponder.setAckEnquireLink(true);
            myConnection.addObserver(autoResponder);

            // Need to add myself to the list of listeners for this connection
            myConnection.addObserver(this);

            synchronized (this) {
                // Bind to the SMSC (as a transmitter)
                logger.info("Binding to the SMSC..");
                doBind(ConnectionType.TRANSMITTER);
                wait();
                if (myConnection.getState() == ConnectionState.BOUND) {
                    logger.info("Sending a message..");
                    sendMessage();
                    logger.info("Waiting for message response..");
                    wait();
                    logger.info("Unbinding..");
                    unbind();
                    logger.info("Waiting on unbind response..");
                    wait();
                }
            }
        } catch (Exception x) {
            throw new BuildException("Exception running example: " + x.getMessage(), x);
        } finally {
            closeConnection();
        }
    }
}

