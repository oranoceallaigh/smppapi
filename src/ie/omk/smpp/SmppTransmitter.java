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

/*
 * Transmitter implementation of the SMPP API
 */
package ie.omk.smpp;

import java.io.*;
import java.util.*;
import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
import ie.omk.debug.Debug;

/** smpp implementation of a Transmitter SmppConnection
  * @author Oran Kelly
  * @version 1.0
  */
public class SmppTransmitter
	extends ie.omk.smpp.SmppConnection
{
// File identifier string: used for debug output
	private static String FILE = "SmppTransmitter";

	/** Create a new smpp Transmitter connection
	  * @param link The network link to the Smsc
	  */
	public SmppTransmitter(SmscLink link)
	{
		super(link);
	}
	
	/** Get the last request sent to the Smsc by this transmitter
	  * @return null If there is no last packet.
	  */
	public SMPPRequest getLastRequest()
	{
		return (SMPPRequest)outTable.get(new Integer(seqNo-1));
	}
	
	/** Bind to the SMSC as a transmitter. This method will
	  * send a bind_transmitter packet to the SMSC.  If the network
	  * connection to the SMSC is not already open, it will be opened in
	  * this method.
	  * @exception SMPPException If already bound, or system Id, password
	  * and system type fields are not filled in correctly.
	  * @exception java.io.IOException If there is a network error
	  * @see SmppConnection#bind
	  * @see SmppReceiver#bind
	  */
	public boolean bind()
		throws java.io.IOException
	{
		// Make sure we're not already bound
		if(bound)
			throw new SMPPException("Already bound to SMSC as Transmitter.");

		// Check the required fields are filled in
		if(sysId == null)
			throw new SMPPException("Need a system Id to bind as.");
		if(password == null)
			throw new SMPPException("Need a password to authenticate.");
		if(sysType == null)
			throw new SMPPException("Need a system type to identify as.");

		if(!link.isConnected())
		{
			Debug.d(this, "bind", "Opening link connection", Debug.DBG_2);
			link.open();
		}
		in = link.getInputStream();
		out = link.getOutputStream();

		// Make sure the listener thread is running
		if(!rcvThread.isAlive())
			rcvThread.start();

		BindTransmitter t = new BindTransmitter(nextPacket());
		t.setSystemId(this.sysId);
		t.setPassword(this.password);
		t.setSystemType(this.sysType);
		t.setInterfaceVersion(INTERFACE_VERSION);
		t.setAddressTon(this.addrTon);
		t.setAddressNpi(this.addrNpi);
		t.setAddressRange(this.addrRange);
		
		sendRequest(t);
		Debug.d(this ,"bind", "Bind request sent", Debug.DBG_3);
		return true;
	}


	/** Submit a int message to an ESME
	  * @param msg The text of the message.  Must be less than 161 characters
	  * (may be null)
	  * @param flags Message flags information
	  * @param dst Destination ESME to send the message to (may be null)
	  * @exception java.io.IOException If a network error occurs
	  * @see SmeAddress
	  * @see SmppTransmitter#submitMulti
	  */
	public boolean submitMessage(String msg, MsgFlags flags, SmeAddress dst)
		throws java.io.IOException
	{
		return this.submitMessage(msg, flags, null, dst, null, null);
	}

	/** Submit a int message to an ESME
	  * @param msg The text of the message.  Must be less than 161 characters
	  * (may be null)
	  * @param flags Message flags information
	  * @param src Source ESME this message is from (may be null)
	  * @param dst Destination ESME to send the message to (may be null)
	  * @exception java.io.IOException If a network error occurs
	  * @see SmeAddress
	  * @see SmppTransmitter#submitMulti
	  */
	public boolean submitMessage(String msg, MsgFlags flags, SmeAddress src, 
		SmeAddress dst)
		throws java.io.IOException
	{
		return this.submitMessage(msg, flags, src, dst, null, null);
	}

	/** Submit a int message to an ESME
	  * @param msg The text of the message.  Must be less than 161 characters
	  * @param flags Message flags information
	  * @param src Source ESME this message is from (may be null)
	  * @param dst Destination ESME to send the message to (may be null)
	  * @param del Absolute delivery time of the message (may be null)
	  * @param valid The time of expiry of this message (may be null)
	  * @exception java.io.IOException If a network error occurs
	  * @see SmeAddress
	  * @see SmppTransmitter#submitMulti
	  */
	public boolean submitMessage(String msg, MsgFlags flags, SmeAddress src,
		SmeAddress dst, Date del, Date valid)
		throws java.io.IOException
	{
		SubmitSM s = new SubmitSM(nextPacket());
		s.setMessageFlags(flags);
		s.setMessageText(msg);
		s.setDestination(dst);

		if(src != null)
			s.setSource(src);

		sendRequest(s);
		Debug.d(this, "submitMessage", "Request send", Debug.DBG_3);
		return true;
	}

	/** Submit a int message to multiple destinations
	  * @param msg The text of the message.  Must be less than 161 characters
	  * (may be null)
	  * @param flags Message flags information
	  * @param src Source ESME this message is from (may be null)
	  * @param dst Table of SmeAddress structures to send the message to
	  * @param del The absolute delivery time of the message (may be null)
	  * @param valid The validity period of the message, after which it will
	  * @exception java.io.IOException If a network error occurs
	  * @see SmeAddress
	  * @see SmppTransmitter#sendMessage
	  */
	public boolean submitMulti(String msg, MsgFlags flags,
		SmeAddress src, SmeAddress dst[])
		throws java.io.IOException
	{
		return this.submitMulti(msg, flags, src, dst, null, null);
	}

	/** Submit a int message to multiple destinations
	  * @param msg The text of the message.  Must be less than 161 characters
	  * (may be null)
	  * @param flags Message flags information
	  * @param src Source ESME this message is from (may be null)
	  * @param dst Table of SmeAddress structures to send the message to
	  * @param del The absolute delivery time of the message (may be null)
	  * @param valid The validity period of the message, after which it will
	  * @exception java.io.IOException If a network error occurs
	  * @see SmeAddress
	  * @see SmppTransmitter#submitMulti
	  */
	public boolean submitMulti(String msg, MsgFlags flags,
		SmeAddress src, SmeAddress dst[], Date del, Date valid)
		throws java.io.IOException
	{
		int loop = 0;
		SubmitMulti s = new SubmitMulti(nextPacket());
		s.setMessageFlags(flags);
		s.setMessageText(msg);

		if(src != null)
			s.setSource(src);

		// Just to make sure a message doesn't get sent to 0 destinations
		if(dst == null || dst.length == 0)
			throw new SMPPException("Cannot submit multi to 0 destinations");

		// Add in all the destinations
		for(loop=0; loop<dst.length; loop++)
			s.addDestination(dst[loop]);

		// Cannot use replace-if-present to more than one destination
		if(dst.length > 1 && flags.replace_if_present)
			throw new SMPPException("Cannot set replace-if-present flag for massges to more than one destination ESME");

		sendRequest(s);
		Debug.d(this, "submitMulti", "Request send", Debug.DBG_3);
		return true;
	}

	/** Cancel an already submitted int message.
	  * @param s The service type the original message was submitted under
	  * @param m The Id of the original message
	  * @param d The Destination address of the original message
	  * @return true if the message has been cancelled, false otherwise
	  * @exception java.io.IOException If a network error occurs.
	  */
	public boolean cancelMessage(String s, int m, SmeAddress d)
		throws IOException
	{
		return this.cancelMessage(s, m, null, d);
	}

	/** Cancel an already submitted int message.
	  * @param st The service type the original message was submitted under
	  * @param msgId The Id of the original message
	  * @param dst The Destination address of the original message (may be null)
	  * @param src The source address of the original message (may be null)
	  * @return true if the message has been cancelled, false otherwise
	  * @exception java.io.IOException If a network error occurs.
	  */
	public boolean cancelMessage(String st, int msgId,
		SmeAddress src, SmeAddress dst)
		throws IOException
	{
		CancelSM s = new CancelSM(nextPacket());
		s.setServiceType(st);
		s.setMessageId(msgId);
		if(dst != null)
			s.setDestination(dst);
		if(src != null)
			s.setSource(src);

		sendRequest(s);
		Debug.d(this, "cancelMessage", "Request send", Debug.DBG_3);
		return true;
	}

	/** Replace an existing int message with a new one.
	  * @param msgId The Id of the original message.
	  * @param msg The text of the new message (may be null)
	  * @param flags The flags of the new message.  Only the registered delivery
	  * and the default message Id flags are relevant.
	  * @return true if the replacement is successful, false otherwise
	  * @exception java.io.IOException If a network error occurs
	  */
	public boolean replaceMessage(int msgId, String msg, MsgFlags flags)
		throws java.io.IOException
	{
		return this.replaceMessage(msgId, msg, flags, null, null, null);
	}

	/** Replace an existing int message with a new one.
	  * @param msgId The Id of the original message.
	  * @param msg The text of the new message (may be null)
	  * @param flags The flags of the new message.  Only the registered delivery
	  * @param src The Source address of the original message (may be null)
	  * and the default message Id flags are relevant.
	  * @return true if the replacement is successful, false otherwise
	  * @exception java.io.IOException If a network error occurs
	  */
	public boolean replaceMessage(int msgId, String msg, MsgFlags flags,
		SmeAddress src)
		throws java.io.IOException
	{
		return this.replaceMessage(msgId, msg, flags, src, null, null);
	}

	/** Replace an existing int message with a new one.
	  * @param msgId The Id of the original message.
	  * @param msg The text of the new message (may be null)
	  * @param flags The flags of the new message.  Only the registered delivery
	  * @param src The Source address of the original message (may be null)
	  * and the default message Id flags are relevant.
	  * @param del The absolute delivery time of the message (may be null)
	  * @param valid The validity period of the message, after which it will
	  * expire (may be null)
	  * @return true if the replacement is successful, false otherwise
	  * @exception java.io.IOException If a network error occurs
	  */
	public boolean replaceMessage(int msgId, String msg, MsgFlags flags,
		SmeAddress src, Date del, Date valid)
		throws java.io.IOException
	{
		ReplaceSM s = new ReplaceSM(nextPacket());
		s.setMessageId(msgId);
		s.setMessageFlags(flags);
		if(src != null)
			s.setSource(src);
		if(msg != null)
			s.setMessageText(msg);
		if(del != null)
			s.setDeliveryTime(del);
		if(valid != null)
			s.setExpiryTime(valid);

		sendRequest(s);
		Debug.d(this, "replaceMessage", "Request send", Debug.DBG_3);
		return true;
	}


	/** Get the value of a configurable parameter from the SMSC.
	  * @param name The name of the parameter to get
	  * @return true if the packet was sent successfully
	  * @exception java.io.IOException If a network error occurs.
	  */
	public boolean paramRetrieve(String name)
		throws java.io.IOException
	{
		ParamRetrieve s = new ParamRetrieve(nextPacket());
		s.setParamName(name);

		sendRequest(s);
		Debug.d(this, "paramRetrieve", "Request send", Debug.DBG_3);
		return true;
	}

	// ---------------- All the query operations below here ----------------

	/** Query the status of a int message.
	  * @param msgId The Id of the submitted message
	  * @return true if the packet was sent successfully
	  * @exception java.io.IOException If a network error occurs.
	  * @see MessageDetails
	  */
	public boolean queryMessage(int msgId)
		throws java.io.IOException
	{
		return this.queryMessage(msgId, null);
	}
	
	/** Query the status of a int message.
	  * @param msgId The Id of the submitted message
	  * @param src The source address of the original message (may be null)
	  * @return true if the packet was sent successfully
	  * @exception java.io.IOException If a network error occurs.
	  * @see MessageDetails
	  */
	public boolean queryMessage(int msgId, SmeAddress src)
		throws java.io.IOException
	{
		QuerySM s = new QuerySM(nextPacket());
		s.setMessageId(msgId);
		if(src != null)
			s.setSource(src);

		sendRequest(s);
		Debug.d(this, "queryMessage", "Request send", Debug.DBG_3);
		return true;
	}

	/** Query the last number of messages for an Sme.
	  * @param src The address of the Sme to query for.
	  * @param num The number of messages to query (0 < num <= 100).
	  * @return true if the packet is sent successfully
	  * @exception java.io.IOException If a network error occurs.
	  */
	public boolean queryLastMsgs(SmeAddress src, int num)
		throws java.io.IOException
	{
		if(src == null)
			throw new NullPointerException("Source address cannot be null.");

		if(num < 1) num = 1;
		if(num > 100) num = 100;

		QueryLastMsgs s = new QueryLastMsgs(nextPacket());
		s.setSource(src);
		s.setMsgCount(num);

		sendRequest(s);
		Debug.d(this, "queryLastMsgs", "Request send", Debug.DBG_3);
		return true;
	}


	/** Query the details of a submitted message.
	  * @param msgId The Id of the message to query.
	  * @param len The number of bytes of the message text to get.
	  * @return true if the packet is sent successfully
	  * @exception java.io.IOException If a network error occurs.
	  */
	public boolean queryMsgDetails(int msgId, int len)
		throws java.io.IOException
	{
		return this.queryMsgDetails(msgId, null, len);
	}

	/** Query the details of a submitted message.
	  * @param msgId The Id of the message to query.
	  * @param src The source address of the message (may be null)
	  * @param len The number of bytes of the message text to get.
	  * @return true if the packet is sent succesfully
	  * @exception java.io.IOException If a network error occurs.
	  */
	public boolean queryMsgDetails(int msgId, SmeAddress src, int len)
		throws java.io.IOException
	{
		/* Don't forget to do a long comparison on the high value...
		 * Java int's are only 32-bits!!
		 */
		if(msgId < 0 || msgId > 0xffffffffL)
			throw new NullPointerException("Message Id is out of valid range.");

		// Make sure the length requested is sane!
		if(len < 0) len = 0;
		if(len > 161) len = 161;

		QueryMsgDetails s = new QueryMsgDetails(nextPacket());
		s.setMessageId(msgId);
		if(src != null)
			s.setSource(src);
		s.setSmLength(len);

		sendRequest(s);
		Debug.d(this, "queryMsgDetails", "Request send", Debug.DBG_3);
		return true;
	}
}

