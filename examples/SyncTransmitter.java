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
 * Java SMPP API author: oran.kelly@ireland.com
 */

import java.util.Properties;
import java.io.FileInputStream;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.SmppTransmitter;
import ie.omk.smpp.net.TcpLink;
import ie.omk.smpp.message.*;
import ie.omk.smpp.util.GSMConstants;

public class SyncTransmitter
{
    public static void main(String[] args)
    {
	try {
	    TcpLink link = new TcpLink("localhost", 5432);
	    SmppTransmitter trans = new SmppTransmitter(link);

	    Properties props = new Properties();
	    props.load(new FileInputStream("smpp.properties"));

	    String sysType = props.getProperty("esme.system_type");
	    String sysID = props.getProperty("esme.system_id");
	    String password = props.getProperty("esme.password");

	    SmeAddress source = new SmeAddress(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    props.getProperty("esme.destination"));
	    BindTransmitterResp btr = (BindTransmitterResp)trans.bind(sysID,
		    password, sysType, source);

	    if (btr.getCommandStatus() != 0) {
		System.err.println("Failed to bind to SMSC.");
		return;
	    }

	    System.out.println("Successfully bound to SMSC \"" +
		    btr.getSystemId() + "\"");

	    SmeAddress destination = new SmeAddress(
		    GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    "353861234567");

	    SubmitSMResp smr = trans.submitMessage(
		    "This is a short message",
		    new MsgFlags(),
		    destination);

	    if (smr.getCommandStatus() != 0) {
		System.err.println("Error submitting message.");
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
