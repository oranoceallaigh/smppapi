/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 * $Id$
 */
package ie.omk.smpp.examples;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.util.GSMConstants;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Example class to submit a message to a SMSC using asynchronous communication.
 * This class simply binds to the server, submits a message, waits for the
 * submit response from the server and unbinds.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncTransmitter implements ConnectionObserver {

    private Object blocker = new Object();

    private Connection myConnection = null;

    private HashMap myArgs = new HashMap();

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
            String message = new String("Test Short Message. :-)");
            Address destination = new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "87654321");
            SubmitSM sm = (SubmitSM) myConnection
                    .newInstance(SMPPPacket.SUBMIT_SM);
            sm.setDestination(destination);
            sm.setMessageText(message);
            myConnection.sendRequest(sm);
        } catch (IOException x) {
            logger.warn("I/O Exception", x);
        } catch (SMPPException x) {
            logger.warn("SMPP Exception", x);
        }
    }

    private void init(String[] args) {
        try {
            myArgs = ParseArgs.parse(args);

            int port = Integer.parseInt((String) myArgs.get(ParseArgs.PORT));

            myConnection = new Connection((String) myArgs
                    .get(ParseArgs.HOSTNAME), port, true);
        } catch (Exception x) {
            System.err.println("Bad command line arguments.");
        }
    }

    private void run() {
        try {
            // Need to add myself to the list of listeners for this connection
            myConnection.addObserver(this);

            // Automatically respond to ENQUIRE_LINK requests from the SMSC
            myConnection.autoAckLink(true);

            // Bind to the SMSC (as a transmitter)
            logger.info("Binding to the SMSC..");
            BindResp resp = myConnection.bind(Connection.TRANSMITTER,
                    (String) myArgs.get(ParseArgs.SYSTEM_ID), (String) myArgs
                            .get(ParseArgs.PASSWORD), (String) myArgs
                            .get(ParseArgs.SYSTEM_TYPE), Integer
                            .parseInt((String) myArgs
                                    .get(ParseArgs.ADDRESS_TON)), Integer
                            .parseInt((String) myArgs
                                    .get(ParseArgs.ADDRESS_NPI)),
                    (String) myArgs.get(ParseArgs.ADDRESS_RANGE));

            synchronized (blocker) {
                blocker.wait();
            }
        } catch (IOException x) {
            logger.warn("I/O Exception", x);
        } catch (InterruptedException x) {
            logger.warn("Interrupted exception", x);
        }
    }

    public static void main(String[] clargs) {
        AsyncTransmitter at = new AsyncTransmitter();
        at.init(clargs);
        at.run();
    }
}