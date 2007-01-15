package ie.omk.smpp.examples;

import ie.omk.smpp.Address;
import ie.omk.smpp.ConnectionType;
import ie.omk.smpp.TextMessage;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.util.AutoResponder;
import ie.omk.smpp.util.SyncWrapper;

import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example class to submit a message to a SMSC using synchronous communication.
 * This class simply binds to the server, submits a message, and then unbinds.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class SyncTransmitter extends SMPPAPIExample {

    private Logger logger = LoggerFactory.getLogger(SyncTransmitter.class);

    public SyncTransmitter() {
    }

    public void execute() throws BuildException {
        try {
            createConnection();
            
            // Automatically respond to enqure_link and deliver_sm requests
            // from the SMSC
            AutoResponder autoResponder = new AutoResponder();
            autoResponder.setAckEnquireLink(true);
            autoResponder.setAckDeliverSm(true);
            myConnection.addObserver(autoResponder);

            // SyncWrapper provides the synchronous communication functionality. 
            SyncWrapper syncWrapper = new SyncWrapper(myConnection);
            myConnection.addObserver(syncWrapper);

            logger.info("Binding to the SMSC");
            BindResp resp = syncWrapper.bind(
                    ConnectionType.TRANSMITTER,
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
            TextMessage textMessage = new TextMessage("Example short message.");
            SubmitSM submitSM = textMessage.getSubmitSM(true);
            submitSM.setDestination(new Address(0, 0, "3188332314"));
            SubmitSMResp response =
                (SubmitSMResp) syncWrapper.sendPacket(submitSM);

            logger.info("Submitted message ID: " + response.getMessageId());

            // Unbind.
            UnbindResp unbindResponse = syncWrapper.unbind();

            if (unbindResponse.getCommandStatus() == 0) {
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

