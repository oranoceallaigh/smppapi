/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
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
 */

import java.io.IOException;

import java.util.Date;

import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.message.SmeAddress;

import ie.omk.smpp.net.TcpLink;

import ie.omk.smpp.SmppConnection;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.SmppReceiver;

import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.event.ReceiverExitEvent;


/** Example asynchronous receiver using the SMPPEventAdapter class.
 */
public class AsyncReceiver2 extends SMPPEventAdapter
{
    // Object to block the main thread on.
    private Object blocker = new Object();

    // Command line argument parser
    private Args args = null;

    // Our receiver connection
    private SmppReceiver recv = null;

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

    // Handle message delivery. This method does not need to acknowledge the
    // deliver_sm message as we set the SmppReceiver connection object to
    // automatically acknowledge them.
    public void deliverSM(SmppConnection source, DeliverSM dm)
    {
	int st = dm.getCommandStatus();

	if (st != 0) {
	    System.out.println("DeliverSM: !Error! status = " + st);
	} else {
	    ++msgCount;
	    if (showMsgs) {
		System.out.println("DeliverSM: \"" + dm.getMessageText()
			+ "\"");
	    } else if ((msgCount % 500) == 0) {
		System.out.print(".");
	    }
	}
    }

    // Called when a bind response packet is received.
    public void bindResponse(SmppConnection source, BindResp br)
    {
	if (br.getCommandStatus() == 0)
	    System.out.println("Successfully bound. Awaiting messages..");
	else {
	    System.out.println("Bind did not succeed!");
	    try {
		link.close();
	    } catch (IOException x) {
		System.err.println("IOException closing link:\n"
			+ x.toString());
	    }
	    synchronized (blocker) {
		blocker.notify();
	    }
	}
    }

    // This method is called when the SMSC sends an unbind request to our
    // receiver. We must acknowledge it and terminate gracefully..
    public void unbind(SmppConnection source, Unbind ubd)
    {
	System.out.println("SMSC requested unbind. Acknowledging..");

	try {
	    // SMSC requests unbind..
	    UnbindResp ubr = new UnbindResp(ubd);
	    recv.sendResponse(ubr);
	} catch (IOException x) {
	    System.err.println("IOException while acking unbind.\n"
		    + x.toString());
	} catch (SMPPException x) {
	    System.err.println("SMPPException while acking unbind.\n"
		    + x.toString());
	}
    }

    // This method is called when the SMSC responds to an unbind request we sent
    // to it..it signals that we can shut down the network connection and
    // terminate our application..
    public void unbindResponse(SmppConnection source, UnbindResp ubr)
    {
	int st = ubr.getCommandStatus();

	if (st != 0) {
	    System.out.println("Unbind response: !Error! status = " + st);
	} else {
	    System.out.println("Successfully unbound.");
	}
    }

    // this method is called when the receiver thread is exiting normally.
    public void receiverExit(SmppConnection source, ReceiverExitEvent ev)
    {
	System.out.println("Receiver thread has exited normally.");
	synchronized (blocker) {
	    blocker.notify();
	}
    }

    // this method is called when the receiver thread exits due to an exception
    // in the thread...
    public void receiverExitException(SmppConnection source,
	    ReceiverExitEvent ev)
    {
	System.out.println("Receiver thread exited abnormally. The following"
		+ " exception was thrown:\n" + ev.getException().toString());
	synchronized (blocker) {
	    blocker.notify();
	}
    }

    // Print out a report
    private void endReport()
    {
	System.out.println("deliver_sm's received: " + msgCount);
	System.out.println("Start time: " + new Date(start).toString());
	System.out.println("End time: " + new Date(end).toString());
	System.out.println("Elapsed: " + (end - start) + " milliseconds.");
    }

    private void doit(String[] clargs)
    {
	try {
	    args = new Args(clargs);

	    // create the TcpLink (no network connection is established yet)
	    link = new TcpLink(args.hostName, args.port);

	    // create the receiver
	    recv = new SmppReceiver(link, true);

	    // set the receiver to automatically acknowledge deliver_sm and
	    // enquire_link requests from the SMSC.
	    recv.autoAckLink(true);
	    recv.autoAckMessages(true);

	    // add this example to the list of observers on the receiver
	    // connection
	    recv.addObserver(this);

	    // create an SmeAddress representing the source address for the bind
	    // operation
	    SmeAddress range = null;
	    if (args.sourceRange != null)
		range = new SmeAddress(args.ton, args.npi,
			args.sourceRange);

	    // bind to the SMSC as a receiver
	    System.out.println("Binding to the SMSC..");
	    recv.bind(args.sysID, args.password, args.sysType, range);

	    // block until we're unbound from the SMSC..
	    synchronized (blocker) {
		blocker.wait();
	    }

	    endReport();
	} catch (Exception x) {
	    x.printStackTrace(System.err);
	}
    }

    public static void main(String[] clargs)
    {
	AsyncReceiver2 a2 = new AsyncReceiver2();
	a2.doit(clargs);

	System.exit(0);
    }
}
