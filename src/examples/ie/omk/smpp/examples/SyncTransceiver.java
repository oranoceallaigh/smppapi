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
 * A synchronous transceiver example. Using sync mode for either a transceiver
 * or receiver connection is less useful than using async mode as your
 * application must now poll the connection continuously for incoming delivery
 * messages from the SMSC.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class SyncTransceiver extends SMPPAPIExample {

    private Log logger = LogFactory.getLog(SyncTransceiver.class);

    public SyncTransceiver() {
    }

    public void execute() throws BuildException {
        try {
            myConnection = new Connection(hostName, port);
            myConnection.autoAckLink(true);
            myConnection.autoAckMessages(true);
            
            logger.info("Binding to the SMSC");

            BindResp resp = myConnection.bind(
                    Connection.TRANSCEIVER,
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

            try {
                // Wait a while, see if the SMSC delivers anything to us...
                SMPPPacket p = myConnection.readNextPacket();
                logger.info("Received a packet!");
                logger.info(p.toString());

                // API should be automatically acking deliver_sm and
                // enquire_link packets...
            } catch (java.net.SocketTimeoutException x) {
                // ah well...
            }

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

