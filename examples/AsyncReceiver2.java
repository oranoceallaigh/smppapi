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

import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.message.SmeAddress;

import ie.omk.smpp.net.TcpLink;

import ie.omk.smpp.Connection;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.event.ReceiverExitEvent;


/** Example asynchronous receiver using the SMPPEventAdapter class.
 */
public class AsyncReceiver2 extends SMPPExample
{
    // Object to block the main thread on.
    private Object blocker = new Object();

    // Our receiver connection
    private Connection recv = null;

    // Network link to the SMSC.
    private TcpLink link = null;

    // time example started at
    private long start = 0;

    // time example ended at
    private long end = 0;

    // Number of deliver_sm packets received
    private int msgCount = 0;

    // Set to true to show the message text for each deliver_sm received
    private boolean showMsgs = false;


    public AsyncReceiver2()
    {
    }

    // Print out a report
    private void endReport()
    {
	logger.info("deliver_sm's received: " + msgCount);
	logger.info("Start time: " + new Date(start).toString());
	logger.info("End time: " + new Date(end).toString());
	logger.info("Elapsed: " + (end - start) + " milliseconds.");
    }

    private void doit(String[] clargs)
    {
	try {
	    parseArgs(clargs);

	    // Create the observer
	    AsyncExampleObserver observer = new AsyncExampleObserver();

	    // create the TcpLink (no network connection is established yet)
	    link = new TcpLink(hostName, port);

	    // create the receiver
	    recv = new Connection(link, true);

	    // set the receiver to automatically acknowledge deliver_sm and
	    // enquire_link requests from the SMSC.
	    recv.autoAckLink(true);
	    recv.autoAckMessages(true);

	    // add this example to the list of observers on the receiver
	    // connection
	    recv.addObserver(observer);

	    // create an SmeAddress representing the source address for the bind
	    // operation
	    SmeAddress range = null;
	    if (sourceRange != null)
		range = new SmeAddress(ton, npi, sourceRange);

	    // bind to the SMSC as a receiver
	    logger.info("Binding to the SMSC..");
	    recv.bind(Connection.RECEIVER,
		    sysID,
		    password,
		    sysType);

	    // block until we're unbound from the SMSC..
	    synchronized (blocker) {
		blocker.wait();
	    }

	    endReport();
	} catch (Exception x) {
	    logger.warn("Exception", x);
	}
    }

    private class AsyncExampleObserver extends SMPPEventAdapter {

	public AsyncExampleObserver() {
	}

	// Handle message delivery. This method does not need to acknowledge the
	// deliver_sm message as we set the Connection object to
	// automatically acknowledge them.
	public void deliverSM(Connection source, DeliverSM dm)
	{
	    int st = dm.getCommandStatus();

	    if (st != 0) {
		logger.info("DeliverSM: !Error! status = " + st);
	    } else {
		++msgCount;
		if (showMsgs) {
		    logger.info("DeliverSM: \"" + dm.getMessageText()
			    + "\"");
		} else if ((msgCount % 500) == 0) {
		    System.out.print(".");
		}
	    }
	}

	// Called when a bind response packet is received.
	public void bindResponse(Connection source, BindResp br)
	{
	    if (br.getCommandStatus() == 0)
		logger.info("Successfully bound. Awaiting messages..");
	    else {
		logger.info("Bind did not succeed!");
		try {
		    link.close();
		} catch (IOException x) {
		    logger.info("IOException closing link:\n"
			    + x.toString());
		}
		synchronized (blocker) {
		    blocker.notify();
		}
	    }
	}

	// This method is called when the SMSC sends an unbind request to our
	// receiver. We must acknowledge it and terminate gracefully..
	public void unbind(Connection source, Unbind ubd)
	{
	    logger.info("SMSC requested unbind. Acknowledging..");

	    try {
		// SMSC requests unbind..
		UnbindResp ubr = new UnbindResp(ubd);
		recv.sendResponse(ubr);
	    } catch (IOException x) {
		logger.info("IOException while acking unbind.\n"
			+ x.toString());
	    }
	}

	// This method is called when the SMSC responds to an unbind request we sent
	// to it..it signals that we can shut down the network connection and
	// terminate our application..
	public void unbindResponse(Connection source, UnbindResp ubr)
	{
	    int st = ubr.getCommandStatus();

	    if (st != 0) {
		logger.info("Unbind response: !Error! status = " + st);
	    } else {
		logger.info("Successfully unbound.");
	    }
	}

	// this method is called when the receiver thread is exiting normally.
	public void receiverExit(Connection source, ReceiverExitEvent ev)
	{
	    logger.info("Receiver thread has exited normally.");
	    synchronized (blocker) {
		blocker.notify();
	    }
	}

	// this method is called when the receiver thread exits due to an exception
	// in the thread...
	public void receiverExitException(Connection source,
		ReceiverExitEvent ev)
	{
	    logger.info("Receiver thread exited abnormally. The following"
		    + " exception was thrown:\n" + ev.getException().toString());
	    synchronized (blocker) {
		blocker.notify();
	    }
	}

    }

    public static void main(String[] clargs)
    {
	AsyncReceiver2 a2 = new AsyncReceiver2();
	a2.doit(clargs);

	System.exit(0);
    }
}
