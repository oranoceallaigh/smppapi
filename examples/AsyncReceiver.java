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
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import java.util.Date;
import java.util.Properties;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.SmppConnectionDropPacket;
import ie.omk.smpp.SmppReceiver;
import ie.omk.smpp.SmppEvent;
import ie.omk.smpp.net.TcpLink;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SmeAddress;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.util.GSMConstants;

/** Example class to submit a message to a SMSC.
  * This class simply binds to the server, submits a message and then unbinds.
  */
public class AsyncReceiver
    implements java.util.Observer
{
    // Default properties file to read..
    public static final String PROPS_FILE = "smpp.properties";

    private static Properties props = null;

    private static int msgCount = 0;

    // Start time (once successfully bound).
    private long start = 0;

    // End time (either send an unbind or an unbind received).
    private long end = 0;

    // Set to true to display each message received.
    private boolean showMsgs = false;


    // This is called when the connection receives a packet from the SMSC.
    public void update(Observable o, Object arg)
    {
	SmppReceiver trans = (SmppReceiver)o;
	SmppEvent ev = (SmppEvent)arg;
	SMPPPacket pak = ev.getPacket();

	switch (pak.getCommandId()) {

	// Bind transmitter response. Check it's status for success...
	case SMPPPacket.ESME_BNDRCV_RESP:
	    if (pak.getCommandStatus() != 0) {
		System.out.println("Error binding to the SMSC. Error = "
			+ pak.getCommandStatus());
	    } else {
		this.start = System.currentTimeMillis();
		System.out.println("Successfully bound. Waiting for message"
			+ " delivery..");
		System.out.println("(Each dot printed is 500 deliver_sm's!)");
	    }
	    break;

	// Submit message response...
	case SMPPPacket.SMSC_DELIVER_SM:
	    if (pak.getCommandStatus() != 0) {
		System.out.println("Deliver SM with an error! "
			+ pak.getCommandStatus());

	    } else {
		++msgCount;
		if (showMsgs) {
		    System.out.println(Integer.toString(pak.getSequenceNum())
				+ ": \"" + ((DeliverSM)pak).getMessageText()
				+ "\"");
		} else if ((msgCount % 500) == 0) {
		    System.out.print("."); // Give some feedback
		}
	    }
	    break;

	// Unbind request received from server..
	case SMPPPacket.ESME_UBD:
	    this.end = System.currentTimeMillis();
	    System.out.println("\nSMSC has requested unbind! Responding..");
	    try {
		UnbindResp ubr = new UnbindResp((Unbind)pak);
		trans.sendResponse(ubr);
	    } catch (SMPPException x) {
		x.printStackTrace(System.err);
	    } catch (IOException x) {
		x.printStackTrace(System.err);
	    } finally {
		endReport();
	    }
	    break;

	// Unbind response..
	case SMPPPacket.ESME_UBD_RESP:
	    this.end = System.currentTimeMillis();
	    System.out.println("\nUnbound.");
	    endReport();
	    break;

	case SmppConnectionDropPacket.CONNECTION_DROP:
	    this.end = System.currentTimeMillis();
	    System.out.println("\nNetwork connection dropped!");
	    endReport();
	    break;

	default:
	    System.out.println("\nUnexpected packet received! Id = "
		    + Integer.toHexString(pak.getCommandId()));
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

    public static void main(String[] args)
    {
	try {
	    AsyncReceiver ex = new AsyncReceiver();

	    if (args.length > 0 && "-s".equals(args[0]))
		ex.showMsgs = true;

	    FileInputStream in = new FileInputStream(PROPS_FILE);
	    ex.props = new Properties();
	    ex.props.load(new BufferedInputStream(in));

	    String server = props.getProperty("smsc.name", "localhost");
	    String p = props.getProperty("smsc.port", "5432");
	    int port = Integer.parseInt(p);

	    // Open a network link to the SMSC..
	    TcpLink link = new TcpLink(server, port);

	    // Create an SmppReceiver object (we won't bind just yet..)
	    SmppReceiver recv = new SmppReceiver(link, true);

	    // Need to add myself to the list of listeners for this connection
	    recv.addObserver(ex);

	    // Automatically respond to ENQUIRE_LINK requests from the SMSC
	    recv.autoAckLink(true);
	    recv.autoAckMessages(true);

	    // Set our authorisation information
	    String sysType = props.getProperty("esme.system_type");
	    String sysID = props.getProperty("esme.system_id");
	    String password = props.getProperty("esme.password");

	    SmeAddress source = new SmeAddress(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    props.getProperty("esme.destination"));

	    // Bind to the SMSC
	    recv.bind(sysID, password, sysType, source);

	    System.out.println("Hit a key to issue an unbind..");
	    System.in.read();

	    if (recv.getState() == recv.BOUND) {
		System.out.println("Sending unbind request..");
		recv.unbind();
	    }
	} catch (IOException x) {
	    x.printStackTrace(System.err);
	} catch (NumberFormatException x) {
	    System.err.println("Bad port number in properties file.");
	    x.printStackTrace(System.err);
	} catch (SMPPException x) {
	    System.err.println("SMPP exception: " + x.getMessage());
	    x.printStackTrace(System.err);
	}
    }
}
