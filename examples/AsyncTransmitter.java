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
import ie.omk.smpp.SmppTransmitter;
import ie.omk.smpp.SmppEvent;
import ie.omk.smpp.net.TcpLink;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.MsgFlags;
import ie.omk.smpp.message.SmeAddress;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.util.GSMConstants;

/** Example class to submit a message to a SMSC.
  * This class simply binds to the server, submits a message and then unbinds.
  */
public class AsyncTransmitter
    implements java.util.Observer
{
    // Default properties file to read..
    public static final String PROPS_FILE = "smpp.properties";

    private static Properties props = null;

    // This is called when the connection receives a packet from the SMSC.
    public void update(Observable o, Object arg)
    {
	SmppTransmitter trans = (SmppTransmitter)o;
	SmppEvent ev = (SmppEvent)arg;
	SMPPPacket pak = ev.getPacket();

	System.out.println("Packet received: Id = "
		+ Integer.toHexString(pak.getCommandId()));
	switch (pak.getCommandId()) {

	// Bind transmitter response. Check it's status for success...
	case SMPPPacket.ESME_BNDTRN_RESP:
	    if (pak.getCommandStatus() != 0) {
		System.out.println("Error binding to the SMSC. Error = "
			+ pak.getCommandStatus());
	    } else {
		System.out.println("\tSuccessfully bound to SMSC \""
			+ ((BindTransmitterResp)pak).getSystemId()
			+ "\".\n\tSubmitting message...");
		send(trans);
	    }
	    break;

	// Submit message response...
	case SMPPPacket.ESME_SUB_SM_RESP:
	    if (pak.getCommandStatus() != 0) {
		System.out.println("\tMessage was not submitted. Error code: "
			+ pak.getCommandStatus());
	    } else {
		System.out.println("\tMessage Submitted! Id = "
			    + ((SubmitSMResp)pak).getMessageId());
	    }

	    // Unbind. The Connection's listener thread will stop itself..
	    try {
		trans.unbind();
	    } catch (IOException x) {
		System.err.println("\tUnbind error. Closing network "
			+ "connection.");
		x.printStackTrace(System.err);
	    } catch (SMPPException x) {
		x.printStackTrace(System.err);
	    }
	    break;

	// Unbind response..
	case SMPPPacket.ESME_UBD_RESP:
	    System.out.println("\tUnbound.");
	    break;

	default:
	    System.out.println("\tUnknown response received! Id = "
		    + pak.getCommandId());
	}
    }

    // Send a short message to the SMSC
    public void send(SmppTransmitter trans)
    {
	try {
	    String message = new String("Test Short Message. :-)");
	    SmeAddress destination = new SmeAddress(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    props.getProperty("esme.destination"));
	    MsgFlags flags = new MsgFlags();
	    trans.submitMessage(message, flags, destination);
	} catch (IOException x) {
	    x.printStackTrace(System.err);
	} catch (SMPPException x) {
	    x.printStackTrace(System.err);
	}
    }

    public static void main(String[] args)
    {
	try {
	    AsyncTransmitter ex = new AsyncTransmitter();

	    FileInputStream in = new FileInputStream(PROPS_FILE);
	    ex.props = new Properties();
	    ex.props.load(new BufferedInputStream(in));

	    String server = props.getProperty("smsc.hostname", "localhost");
	    String p = props.getProperty("smsc.port", "5432");
	    int port = Integer.parseInt(p);

	    // Open a network link to the SMSC..
	    TcpLink link = new TcpLink(server, port);

	    // Create an SmppTransmitter object (we won't bind just yet..)
	    SmppTransmitter trans = new SmppTransmitter(link, true);

	    // Need to add myself to the list of listeners for this connection
	    trans.addObserver(ex);

	    // Automatically respond to ENQUIRE_LINK requests from the SMSC
	    trans.autoAckLink(true);

	    // Set our authorisation information
	    trans.setSystemType(props.getProperty("esme.system_type"));
	    trans.setSystemId(props.getProperty("esme.system_id"));
	    trans.setPassword(props.getProperty("esme.password"));

	    // Bind to the SMSC (as a transmitter)
	    trans.bind();
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
