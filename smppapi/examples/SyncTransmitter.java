/*
 * Java implementation of the SMPP v3.3 API
 * Copyright (C) 1998 - 2000 by Oran Kelly
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
 */

import java.util.Properties;
import java.io.FileInputStream;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.message.*;

import ie.omk.smpp.net.TcpLink;

import ie.omk.smpp.util.*;


public class SyncTransmitter
{
    public static void main(String[] clargs)
    {
	try {
	    Args args = new Args(clargs);
	    TcpLink link = new TcpLink(args.hostName, args.port);
	    Connection trans = new Connection(link);

	    Address range = null;

	    BindTransmitterResp btr = (BindTransmitterResp)trans.bind(
		    Connection.TRANSMITTER,
		    args.sysID,
		    args.password,
		    args.sysType);

	    if (btr.getCommandStatus() != 0) {
		System.err.println("Failed to bind to SMSC.");
		return;
	    }

	    System.out.println("Successfully bound to SMSC \"" +
		    btr.getSystemId() + "\"");

	    Address destination = new Address(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    "353861234567");

	    SubmitSM sm = new SubmitSM();
	    sm.setDestination(destination);
	    sm.setMessageText("This is a short message");
	    SubmitSMResp smr = (SubmitSMResp)trans.sendRequest(sm);

	    if (smr.getCommandStatus() != 0) {
		System.err.println("Error submitting message.");
	    } else {
		System.out.println("Message submitted. ID is \""
			+ smr.getMessageId() + "\"");
	    }

	    // Send a UCS2 encoded message...
	    sm = new SubmitSM();
	    sm.setDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"434325425"));
	    sm.setMessageText("¡My ìntérñætîõnäl message!",
		    new UCS2Encoding());
	    smr = (SubmitSMResp)trans.sendRequest(sm);
	    if (smr.getCommandStatus() != 0) {
		System.err.println("Error submitting UCS2 message");
	    } else {
		System.out.println("Message submitted. ID is \""
			+ smr.getMessageId() + "\"");
	    }

	    // Send a Binary encoded message...
	    sm = new SubmitSM();
	    sm.setDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"434325425"));
	    byte[] msg = {
		(byte)0x45,
		(byte)0x32,
		(byte)0x22,
		(byte)0x57,
		(byte)0x12
	    };
	    sm.setMessage(msg, new BinaryEncoding());
	    smr = (SubmitSMResp)trans.sendRequest(sm);
	    if (smr.getCommandStatus() != 0) {
		System.err.println("Error submitting Binary message");
	    } else {
		System.out.println("Message submitted. ID is \""
			+ smr.getMessageId() + "\"");
	    }

	    System.out.println("Unbinding from SMSC...");

	    UnbindResp ubr = trans.unbind();
	    if (ubr.getCommandStatus() != 0) {
		System.err.println("Error occurred while unbinding from SMSC."
			+ " Code is " + ubr.getCommandStatus());
	    } else {
		System.out.println("Unbound. Closing network connection.");
	    }

	    link.close();
	} catch (SMPPException x) {
	    x.printStackTrace(System.err);
	} catch (java.io.IOException x) {
	    x.printStackTrace(System.err);
	}
    }
}
