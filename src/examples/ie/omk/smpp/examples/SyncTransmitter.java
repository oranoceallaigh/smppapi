package ie.omk.smpp.examples;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.UnbindResp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

/**
 * Example class to submit a message to a SMSC using synchronous communication.
 * This class simply binds to the server, submits a message, and then unbinds.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class SyncTransmitter extends SMPPAPIExample {

    private Log logger = LogFactory.getLog(SyncTransmitter.class);

    public SyncTransmitter() {
    }

    public void execute() throws BuildException {
        try {
            logger.info("Binding to the SMSC");

            myConnection = new Connection(hostName, port);
            myConnection.autoAckLink(true);
            myConnection.autoAckMessages(true);

            BindResp resp = myConnection.bind(
                    Connection.TRANSMITTER,
                    systemID,
                    password,
                    systemType,
                    sourceTON,
                    sourceNPI,
                    sourceAddress);

            if (resp.getCommandStatus() != 0) {
                logger.info("SMSC bind failed.");
                System.exit(1);
            }

            logger.info("Bind successful...submitting a message.");

            // Submit a simple message
            SubmitSM sm = (SubmitSM) myConnection.newInstance(SMPPPacket.SUBMIT_SM);
            sm.setDestination(new Address(0, 0, "3188332314"));
            sm.setMessageText("This is an example short message.");
            SubmitSMResp smr = (SubmitSMResp) myConnection.sendRequest(sm);

            logger.info("Submitted message ID: " + smr.getMessageId());

            // Unbind.
            UnbindResp ubr = myConnection.unbind();

            if (ubr.getCommandStatus() == 0) {
                logger.info("Successfully unbound from the SMSC");
            } else {
                logger.info("There was an error unbinding.");
            }
        } catch (Exception x) {
            logger.info("An exception occurred.");
            x.printStackTrace(System.err);
        }
    }
}

