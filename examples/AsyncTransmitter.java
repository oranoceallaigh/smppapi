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

import ie.omk.smpp.net.TcpLink;

import ie.omk.smpp.util.GSMConstants;

/** Example class to submit a message to a SMSC.
  * This class simply binds to the server, submits a message and then unbinds.
  */
public class AsyncTransmitter extends SMPPExample
    implements ConnectionObserver
{
    private Object blocker = new Object();

    // This is called when the connection receives a packet from the SMSC.
    public void update(Connection t, SMPPEvent ev)
    {
	switch (ev.getType()) {
	case SMPPEvent.RECEIVER_EXIT:
	    receiverExit(t, (ReceiverExitEvent)ev);
	    break;
	}
    }

    public void packetReceived(Connection trans, SMPPPacket pak)
    {
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
		logger.info("\tSuccessfully bound to SMSC \""
			+ ((BindTransmitterResp)pak).getSystemId()
			+ "\".\n\tSubmitting message...");
		send(trans);
	    }
	    break;

	// Submit message response...
	case SMPPPacket.SUBMIT_SM_RESP:
	    if (pak.getCommandStatus() != 0) {
		logger.info("\tMessage was not submitted. Error code: "
			+ pak.getCommandStatus());
	    } else {
		logger.info("\tMessage Submitted! Id = "
			    + ((SubmitSMResp)pak).getMessageId());
	    }

	    // Unbind. The Connection's listener thread will stop itself..
	    try {
		trans.unbind();
	    } catch (IOException x) {
		logger.info("\tUnbind error. Closing network "
			+ "connection.");
		logger.warn("Exception", x);
		synchronized (blocker) {
		    blocker.notify();
		}
	    }
	    break;

	// Unbind response..
	case SMPPPacket.UNBIND_RESP:
	    logger.info("\tUnbound.");
	    break;

	default:
	    logger.info("\tUnknown response received! Id = "
		    + pak.getCommandId());
	}
    }

    private void receiverExit(Connection trans, ReceiverExitEvent ev)
    {
	if (!ev.isException()) {
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
    public void send(Connection trans)
    {
	try {
	    String message = new String("Test Short Message. :-)");
	    Address destination = new Address(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    "87654321");
	    SubmitSM sm = new SubmitSM();
	    sm.setDestination(destination);
	    sm.setMessageText(message);
	    trans.sendRequest(sm);
	} catch (IOException x) {
	    logger.warn("I/O Exception", x);
	} catch (SMPPException x) {
	    logger.warn("SMPP Exception", x);
	}
    }

    private void doit(String[] clargs)
    {
	try {
	    parseArgs(clargs);

	    // Open a network link to the SMSC..
	    TcpLink link = new TcpLink(hostName, port);

	    // Create a Connection object (we won't bind just yet..)
	    Connection trans = new Connection(link, true);

	    // Need to add myself to the list of listeners for this connection
	    trans.addObserver(this);

	    // Automatically respond to ENQUIRE_LINK requests from the SMSC
	    trans.autoAckLink(true);

	    Address range = null;
	    if (sourceRange != null)
		range = new Address(ton, npi, sourceRange);

	    // Bind to the SMSC (as a transmitter)
	    logger.info("Binding to the SMSC..");
	    trans.bind(Connection.TRANSMITTER,
		    sysID,
		    password,
		    sysType);

	    synchronized (blocker) {
		blocker.wait();
	    }
	} catch (IOException x) {
	    logger.warn("I/O Exception", x);
	} catch (SMPPException x) {
	    logger.warn("SMPP exception", x);
	} catch (InterruptedException x) {
	    logger.warn("Interrupted exception", x);
	}
    }

    public static void main(String[] clargs)
    {
	AsyncTransmitter at = new AsyncTransmitter();
	at.doit(clargs);
	System.exit(0);
    }
}
