package ie.omk.smpp.examples;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

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

    private Log logger = LogFactory.getLog(AsyncReceiver2.class);

    // time example started at
    private long start = 0;

    // time example ended at
    private long end = 0;

    // Number of deliver_sm packets received
    private int msgCount = 0;

    public AsyncReceiver2() {
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

            // Create the observer
            AsyncExampleObserver observer = new AsyncExampleObserver();

            // set the receiver to automatically acknowledge deliver_sm and
            // enquire_link requests from the SMSC.
            myConnection.autoAckLink(true);
            myConnection.autoAckMessages(true);

            // add this example to the list of observers on the receiver
            // connection
            myConnection.addObserver(observer);

            // bind to the SMSC as a receiver
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

                wait();
            }

            end = System.currentTimeMillis();
            
            // Close down the network connection.
            myConnection.closeLink();
        } catch (Exception x) {
            throw new BuildException("Exception running example: " + x.getMessage(), x);
        } finally {
            endReport();
        }
    }

    private class AsyncExampleObserver extends SMPPEventAdapter {

        public AsyncExampleObserver() {
        }

        // Handle message delivery. This method does not need to acknowledge the
        // deliver_sm message as we set the Connection object to
        // automatically acknowledge them.
        public void deliverSM(Connection source, DeliverSM dm) {
            int st = dm.getCommandStatus();

            if (st != 0) {
                logger.info("DeliverSM: !Error! status = " + st);
            } else {
                ++msgCount;
                logger.info("DeliverSM: \"" + dm.getMessageText() + "\"");
            }
        }

        // Called when a bind response packet is received.
        public void bindResponse(Connection source, BindResp br) {
            synchronized (AsyncReceiver2.this) {
                // on exiting this block, we're sure that
                // the main thread is now sitting in the wait
                // call, awaiting the unbind request.
                logger.info("Bind response received.");
           }
            if (br.getCommandStatus() == 0) {
                logger.info("Successfully bound. Awaiting messages..");
            } else {
                logger.info("Bind did not succeed!");
                try {
                    myConnection.closeLink();
               } catch (IOException x) {
                    logger.info("IOException closing link:\n" + x.toString());
               }
            }
        }

        // This method is called when the SMSC sends an unbind request to our
        // receiver. We must acknowledge it and terminate gracefully..
        public void unbind(Connection source, Unbind ubd) {
            logger.info("SMSC requested unbind. Acknowledging..");

            try {
                // SMSC requests unbind..
                UnbindResp ubr = new UnbindResp(ubd);
                myConnection.sendResponse(ubr);
            } catch (IOException x) {
                logger.info("IOException while acking unbind: " + x.toString());
            }
        }

        // This method is called when the SMSC responds to an unbind request we
        // sent
        // to it..it signals that we can shut down the network connection and
        // terminate our application..
        public void unbindResponse(Connection source, UnbindResp ubr) {
            int st = ubr.getCommandStatus();

            if (st != 0) {
                logger.info("Unbind response: !Error! status = " + st);
            } else {
                logger.info("Successfully unbound.");
            }
        }

        // this method is called when the receiver thread is exiting normally.
        public void receiverExit(Connection source, ReceiverExitEvent ev) {
            if (ev.getReason() == ReceiverExitEvent.BIND_TIMEOUT) {
                logger.info("Bind timed out waiting for response.");
            }

            logger.info("Receiver thread has exited.");
            synchronized (AsyncReceiver2.this) {
                AsyncReceiver2.this.notify();
            }
        }

        // this method is called when the receiver thread exits due to an
        // exception
        // in the thread...
        public void receiverExitException(Connection source,
                ReceiverExitEvent ev) {
            logger
                    .info("Receiver thread exited abnormally. The following"
                            + " exception was thrown:\n"
                            + ev.getException().toString());
            synchronized (AsyncReceiver2.this) {
                AsyncReceiver2.this.notify();
            }
        }

    }
}

