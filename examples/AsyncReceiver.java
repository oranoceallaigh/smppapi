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

import java.io.IOException;

import java.util.Date;
import java.util.Vector;

import ie.omk.smpp.Connection;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.ReceiverExitEvent;

import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SmeAddress;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import ie.omk.smpp.net.TcpLink;

import ie.omk.smpp.util.GSMConstants;

/** Example class to submit a message to a SMSC.
  * This class simply binds to the server, submits a message and then unbinds.
  */
public class AsyncReceiver extends SMPPExample
    implements ConnectionObserver
{
    private static int msgCount = 0;

    // Start time (once successfully bound).
    private long start = 0;

    // End time (either send an unbind or an unbind received).
    private long end = 0;

    // Set to true to display each message received.
    private boolean showMsgs = false;


    // This is called when the connection receives a packet from the SMSC.
    public void update(Connection r, SMPPEvent ev)
    {
	switch (ev.getType()) {
	case SMPPEvent.RECEIVER_EXIT:
	    receiverExit(r, (ReceiverExitEvent)ev);
	    break;
	}
    }

    public void packetReceived(Connection recv, SMPPPacket pak)
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
		logger.info("(Each dot printed is 500 deliver_sm's!)");
	    }
	    break;

	// Submit message response...
	case SMPPPacket.DELIVER_SM:
	    if (pak.getCommandStatus() != 0) {
		logger.info("Deliver SM with an error! "
			+ pak.getCommandStatus());

	    } else {
		++msgCount;
		if (showMsgs) {
		    logger.info(Integer.toString(pak.getSequenceNum())
				+ ": \"" + ((DeliverSM)pak).getMessageText()
				+ "\"");
		} else if ((msgCount % 500) == 0) {
		    System.out.print("."); // Give some feedback
		}
	    }
	    break;

	// Unbind request received from server..
	case SMPPPacket.UNBIND:
	    this.end = System.currentTimeMillis();
	    logger.info("\nSMSC has requested unbind! Responding..");
	    try {
		UnbindResp ubr = new UnbindResp((Unbind)pak);
		recv.sendResponse(ubr);
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
	    logger.info("\nUnexpected packet received! Id = "
		    + Integer.toHexString(pak.getCommandId()));
	}
    }

    private void receiverExit(Connection recv, ReceiverExitEvent ev)
    {
	if (!ev.isException()) {
	    logger.info("Receiver thread has exited normally.");
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

    public static void main(String[] clargs)
    {
	try {
	    AsyncReceiver ex = new AsyncReceiver();
	    parseArgs(clargs);

	    // Open a network link to the SMSC..
	    TcpLink link = new TcpLink(hostName, port);

	    // Create a Connection object (we won't bind just yet..)
	    Connection recv = new Connection(link, true);

	    // Need to add myself to the list of listeners for this connection
	    recv.addObserver(ex);

	    // Automatically respond to ENQUIRE_LINK requests from the SMSC
	    recv.autoAckLink(true);
	    recv.autoAckMessages(true);

	    SmeAddress range = null;
	    if (sourceRange != null)
		new SmeAddress(ton, npi, sourceRange);

	    // Bind to the SMSC
	    recv.bind(Connection.RECEIVER,
		    sysID,
		    password,
		    sysType);

	    logger.info("Hit a key to issue an unbind..");
	    System.in.read();

	    if (recv.getState() == recv.BOUND) {
		logger.info("Sending unbind request..");
		recv.unbind();
	    }

	    Thread.sleep(2000);
	} catch (IOException x) {
	    logger.warn("Exception", x);
	} catch (SMPPException x) {
	    logger.warn("Exception", x);
	} catch (InterruptedException x) {
	}
    }
}
