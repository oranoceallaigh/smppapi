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
 * Java SMPP API author: oran.kelly@ireland.com
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */

import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import java.util.Properties;
import java.util.Observable;
import java.util.Observer;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.SmppReceiver;
import ie.omk.smpp.SmppEvent;
import ie.omk.smpp.net.TcpLink;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SmeAddress;
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
		System.out.println("Successfully bound. Waiting for message"
			+ "delivery..");
	    }
	    break;

	// Submit message response...
	case SMPPPacket.SMSC_DELIVER_SM:
	    if (pak.getCommandStatus() != 0) {
		System.out.println("Deliver SM with an error! "
			+ pak.getCommandStatus());

	    } else {
		DeliverSM del = (DeliverSM)pak;
		System.out.println("Message received:\n\""
			+ del.getMessageText() + "\"");
	    }
	    break;

	// Unbind response..
	case SMPPPacket.ESME_UBD_RESP:
	    System.out.println("Unbound.");
	    break;

	default:
	    System.out.println("Unknown response received! Id = "
		    + pak.getCommandId());
	}
    }

    public static void main(String[] args)
    {
	try {
	    AsyncReceiver ex = new AsyncReceiver();

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
	    recv.setSystemType(props.getProperty("esme.system_type"));
	    recv.setSystemId(props.getProperty("esme.system_id"));
	    recv.setPassword(props.getProperty("esme.password"));

	    recv.setSourceAddress(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    props.getProperty("esme.destination"));

	    // Bind to the SMSC (as a transmitter)
	    recv.bind();

	    Thread.sleep(500);

	    System.out.println("Hit Enter to unbind...");
	    System.in.read();

	    recv.unbind();
	} catch (IOException x) {
	    x.printStackTrace(System.err);
	} catch (NumberFormatException x) {
	    System.err.println("Bad port number in properties file.");
	    x.printStackTrace(System.err);
	} catch (SMPPException x) {
	    System.err.println("SMPP exception: " + x.getMessage());
	    x.printStackTrace(System.err);
	} catch (InterruptedException x) {
	    x.printStackTrace(System.err);
	}
    }
}
