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

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

/** Example SMPP receiver using asynchronous communications.
 * This example demonstrates asynchronous communications by
 * implementing the ConnectionObserver interface and directly
 * handling all receiver events.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running
 * this class.
 */
public class AsyncReceiver implements ConnectionObserver {

    private Logger logger = Logger.getLogger("ie.omk.smpp.examples");

    private static int msgCount = 0;

    // Start time (once successfully bound).
    private long start = 0;

    // End time (either send an unbind or an unbind received).
    private long end = 0;

    private Connection myConnection = null;

    private java.util.HashMap myArgs = null;


    // This is called when the connection receives a packet from the SMSC.
    public void update(Connection r, SMPPEvent ev)
    {
	switch (ev.getType()) {
	case SMPPEvent.RECEIVER_EXIT:
	    receiverExit(r, (ReceiverExitEvent)ev);
	    break;
	}
    }

    public void packetReceived(Connection myConnection, SMPPPacket pak)
    {
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
	    }
	    break;

	// Submit message response...
	case SMPPPacket.DELIVER_SM:
	    if (pak.getCommandStatus() != 0) {
		logger.info("Deliver SM with an error! "
			+ pak.getCommandStatus());

	    } else {
		++msgCount;
		logger.info("deliver_sm: " + Integer.toString(pak.getSequenceNum())
			    + ": \"" + ((DeliverSM)pak).getMessageText()
			    + "\"");
	    }
	    break;

	// Unbind request received from server..
	case SMPPPacket.UNBIND:
	    this.end = System.currentTimeMillis();
	    logger.info("\nSMSC has requested unbind! Responding..");
	    try {
		UnbindResp ubr = new UnbindResp((Unbind)pak);
		myConnection.sendResponse(ubr);
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
	    endReport();
	    break;

	default:
	    logger.info("\nUnexpected packet received! Id = 0x"
		    + Integer.toHexString(pak.getCommandId()));
	}
    }

    private void receiverExit(Connection myConnection, ReceiverExitEvent ev)
    {
	if (ev.getReason() != ReceiverExitEvent.EXCEPTION) {
        if (ev.getReason() == ReceiverExitEvent.BIND_TIMEOUT) {
            logger.info("Bind timed out waiting for response.");
        }
	    logger.info("Receiver thread has exited.");
	} else {
	    Throwable t = ev.getException();
	    logger.info("Receiver thread died due to exception:");
	    logger.warn("Exception", t);
	    endReport();
	}
    }

    // Print out a report
    private void endReport()
    {
	logger.info("deliver_sm's received: " + msgCount);
	logger.info("Start time: " + new Date(start).toString());
	logger.info("End time: " + new Date(end).toString());
	logger.info("Elapsed: " + (end - start) + " milliseconds.");
    }

    private void init(String[] args) {
	try {
	    myArgs = ParseArgs.parse(args);

	    int port = Integer.parseInt((String)myArgs.get(ParseArgs.PORT));

	    myConnection = new Connection((String)myArgs.get(ParseArgs.HOSTNAME), port, true);
	} catch (Exception x) {
	    logger.info("Bad command line arguments.");
	}
    }

    private void run() {
	try {
	    // Need to add myself to the list of listeners for this connection
	    myConnection.addObserver(this);

	    // Automatically respond to ENQUIRE_LINK requests from the SMSC
	    myConnection.autoAckLink(true);
	    myConnection.autoAckMessages(true);

	    // Bind to the SMSC
	    logger.info("Binding to the SMSC..");
	    BindResp resp = myConnection.bind(Connection.RECEIVER,
		    (String)myArgs.get(ParseArgs.SYSTEM_ID),
		    (String)myArgs.get(ParseArgs.PASSWORD),
		    (String)myArgs.get(ParseArgs.SYSTEM_TYPE),
		    Integer.parseInt((String)myArgs.get(ParseArgs.ADDRESS_TON)),
		    Integer.parseInt((String)myArgs.get(ParseArgs.ADDRESS_NPI)),
		    (String)myArgs.get(ParseArgs.ADDRESS_RANGE));

	    logger.info("Hit a key to issue an unbind..");
	    System.in.read();

	    if (myConnection.getState() == Connection.BOUND) {
		logger.info("Sending unbind request..");
		myConnection.unbind();
	    }

	    Thread.sleep(2000);

	    myConnection.closeLink();
	} catch (IOException x) {
	    logger.warn("Exception", x);
	} catch (InterruptedException x) {
	}
    }

    public static void main(String[] clargs)
    {
	AsyncReceiver ex = new AsyncReceiver();
	ex.init(clargs);
	ex.run();
    }
}
