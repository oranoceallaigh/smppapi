package ie.omk.smpp.examples;

import ie.omk.smpp.Connection;
import ie.omk.smpp.ConnectionType;
import ie.omk.smpp.TextMessage;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
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
 * demonstrates asynchronous communications using the SMPPEventAdapter. The
 * SMPPEventAdapter is a utility class which implements the ConnectionObserver
 * interface for you and delivers received events to appropriate methods in the
 * adapter implementation.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncReceiver2 extends SMPPAPIExample {

    private Logger logger = LoggerFactory.getLogger(AsyncReceiver2.class);

    // Number of deliver_sm packets received during the session.
    private int msgCount;

    // Start time (once successfully bound).
    private long start;

    // End time (once unbound).
    private long end;

    public AsyncReceiver2() {
    }

    public void execute() throws BuildException {
        try {
            createConnection();

            // Create the observer
            AsyncExampleObserver observer = new AsyncExampleObserver();

            // Automatically respond to enqure_link and deliver_sm requests
            // from the SMSC
            AutoResponder autoResponder = new AutoResponder();
            autoResponder.setAckDeliverSm(true);
            autoResponder.setAckEnquireLink(true);
            myConnection.addObserver(autoResponder);

            // add this example to the list of observers on the receiver
            // connection
            myConnection.addObserver(observer);

            // bind to the SMSC as a receiver
            logger.info("Binding to the SMSC..");

            synchronized (this) {
                doBind(ConnectionType.RECEIVER);
                wait();
            }
        } catch (Exception x) {
            throw new BuildException("Exception running example: " + x.getMessage(), x);
        } finally {
            closeConnection();
            end = System.currentTimeMillis();
            endReport();
        }
    }

    // Print out a report
    private void endReport() {
        logger.info("deliver_sm's received: {}", msgCount);
        logger.info("Start time: {}", new Date(start));
        logger.info("End time: {}", new Date(end));
        logger.info("Elapsed: {} milliseconds.", (end - start));
    }

    private class AsyncExampleObserver extends SMPPEventAdapter {

        public AsyncExampleObserver() {
        }

        // Handle message delivery. We don't need to acknowledge the deliver_sm
        // since we have registered an auto-responder to do that for us.
        public void deliverSM(Connection source, DeliverSM deliverSm) {
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

        // Called when a bind response packet is received.
        public void bindResponse(Connection source, BindResp bindResponse) {
            if (bindResponse.getCommandStatus() != 0) {
                logger.info("Error binding to the SMSC. Error = {}",
                        bindResponse.getCommandStatus());
                synchronized (this) {
                    notify();
                }
            } else {
                start = System.currentTimeMillis();
                logger.info("Successfully bound. Waiting for message delivery..");

                synchronized (AsyncReceiver2.this) {
                    // This code is here to prevent the main thread exiting,
                    // that is all.
                    new String("Dummy code to do nothing in particular").hashCode();
               }
            }
        }

        // This method is called when the SMSC sends an unbind request to our
        // receiver. We must acknowledge it and terminate gracefully..
        public void unbind(Connection source, Unbind unbind) {
            logger.info("SMSC has requested unbind! Responding..");
            try {
                UnbindResp response = new UnbindResp(unbind);
                source.sendPacket(response);
                
                synchronized (AsyncReceiver2.this) {
                    AsyncReceiver2.this.notify();
                }
            } catch (IOException x) {
                logger.error("Got an exception", x);
            }
        }

        // This method is called when the SMSC responds to an unbind request we
        // sent
        // to it..it signals that we can shut down the network connection and
        // terminate our application..
        public void unbindResponse(Connection source, UnbindResp unbindResponse) {
            if (unbindResponse.getCommandStatus() != 0) {
                logger.error("Got an unbind response with non-zero status: {}",
                        unbindResponse.getCommandStatus());
            } else {
                logger.info("Successfully unbound.");
                synchronized (AsyncReceiver2.this) {
                    AsyncReceiver2.this.notify();
                }
            }
        }

        // this method is called when the receiver thread is exiting normally.
        public void receiverExit(Connection source, ReceiverExitEvent event) {
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
            synchronized (AsyncReceiver2.this) {
                AsyncReceiver2.this.notify();
            }
        }

        // this method is called when the receiver thread exits due to an
        // exception...
        public void receiverExitException(Connection source, ReceiverExitEvent event) {
            logger.info("Receiver thread exited abnormally. The following exception was thrown");
            logger.info("", event.getException());
            synchronized (AsyncReceiver2.this) {
                AsyncReceiver2.this.notify();
            }
        }

    }
}
