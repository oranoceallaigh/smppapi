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

package ie.omk.smpp;

import java.io.*;
import java.util.*;

import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.debug.Debug;

/** Transmitter implementation of the SMPP Connection.
  * @author Oran Kelly
  * @version 1.0
  */
public class SmppTransmitter
    extends ie.omk.smpp.SmppConnection
{
    /** Create a new smpp Transmitter connection
      * @param link The network link to the Smsc
      */
    public SmppTransmitter(SmscLink link)
    {
	super(link);
    }

    /** Create a new Smpp transmitter specifying the type of communications
      * desired.
      * @param link The network link object to the Smsc (cannot be null)
      * @param async true for asyncronous communication, false for synchronous.
      * @exception java.lang.NullPointerException If the link is null
      */
    public SmppTransmitter(SmscLink link, boolean async)
    {
	super(link, async);
    }

    /** Get the last request sent to the Smsc by this transmitter
      * @return null If there is no last packet.
      */
    public SMPPRequest getLastRequest()
    {
	return (SMPPRequest)outTable.get(new Integer(seqNum - 1));
    }

    /** Bind to the SMSC as a transmitter. This method will
      * send a bind_transmitter packet to the SMSC.  If the network
      * connection to the SMSC is not already open, it will be opened in
      * this method.
      * @return The bind response, or null if asynchronous communication is
      * used.
      * @exception SMPPException If already bound, or system Id, password
      * and system type fields are not filled in correctly.
      * @exception java.io.IOException If there is a network error
      * @see SmppReceiver#bind
      */
    public SMPPResponse bind()
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	// Make sure we're not already bound
	if(getState() != UNBOUND)
	    throw new SMPPException("Already bound to SMSC");

	// Check the required fields are filled in
	if(sysId == null)
	    throw new SMPPException("Need a system Id to bind as.");
	if(password == null)
	    throw new SMPPException("Need a password to authenticate.");
	if(sysType == null)
	    throw new SMPPException("Need a system type to identify as.");

	// Open the network connection if necessary.
	if(!link.isConnected()) {
	    Debug.d(this, "bind", "Opening link connection", Debug.DBG_2);
	    link.open();
	}

	// Make sure the listener thread is running
	setState(BINDING);
	if(asyncComms) {
	    if (rcvThread == null)
		rcvThread = new Thread(this);

	    if (!rcvThread.isAlive())
		rcvThread.start();
	}

	BindTransmitter t = new BindTransmitter(nextPacket());
	t.setSystemId(this.sysId);
	t.setPassword(this.password);
	t.setSystemType(this.sysType);
	t.setInterfaceVersion(INTERFACE_VERSION);
	t.setAddressTon(this.addrTon);
	t.setAddressNpi(this.addrNpi);
	t.setAddressRange(this.addrRange);

	Debug.d(this ,"bind", "Bind request sent", Debug.DBG_3);

	SMPPResponse resp = sendRequest(t);
	if (!asyncComms) {
	    if (resp.getCommandId() == SMPPPacket.ESME_BNDTRN_RESP
		    && resp.getCommandStatus() == 0)
		setState(BOUND);
	}
	return ((SMPPResponse)resp);
    }


    /** Submit a message to an ESME
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param dst Destination ESME to send the message to (may be null)
      * @return The submit message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      * @see SmeAddress
      * @see SmppTransmitter#submitMulti
      */
    public SubmitSMResp submitMessage(String msg, MsgFlags flags,
	    SmeAddress dst)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.submitMessage(msg, flags, null, dst, null, null));
    }

    /** Submit a message to an ESME
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param src Source ESME this message is from (may be null)
      * @param dst Destination ESME to send the message to (may be null)
      * @return The submit message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      * @see SmeAddress
      * @see SmppTransmitter#submitMulti
      */
    public SubmitSMResp submitMessage(String msg, MsgFlags flags,
	    SmeAddress src, SmeAddress dst)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.submitMessage(msg, flags, src, dst, null, null));
    }

    /** Submit a message to an ESME
      * @param msg The text of the message.  Must be less than 161 characters
      * @param flags Message flags information
      * @param src Source ESME this message is from (may be null)
      * @param dst Destination ESME to send the message to (may be null)
      * @param del Absolute delivery time of the message (may be null)
      * @param valid The time of expiry of this message (may be null)
      * @return The submit message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      * @see SmeAddress
      * @see SmppTransmitter#submitMulti
      */
    public SubmitSMResp submitMessage(String msg, MsgFlags flags,
	    SmeAddress src, SmeAddress dst, SMPPDate del, SMPPDate valid)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SubmitSM s = new SubmitSM(nextPacket());
	s.setMessageFlags(flags);
	s.setMessageText(msg);
	s.setDestination(dst);

	if(src != null)
	    s.setSource(src);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "submitMessage", "Request send", Debug.DBG_3);
	return ((SubmitSMResp)resp);
    }

    /** Submit a message to multiple destinations
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param src Source ESME this message is from (may be null)
      * @param dst Table of SmeAddress structures to send the message to
      * @param del The absolute delivery time of the message (may be null)
      * @param valid The validity period of the message, after which it will
      * @return The submit multi response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      * @see SmeAddress
      */
    public SubmitMultiResp submitMulti(String msg, MsgFlags flags,
	    SmeAddress src, SmeAddress dst[])
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.submitMulti(msg, flags, src, dst, null, null));
    }

    /** Submit a message to multiple destinations
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param src Source ESME this message is from (may be null)
      * @param dst Table of SmeAddress structures to send the message to
      * @param del The absolute delivery time of the message (may be null)
      * @param valid The validity period of the message, after which it will
      * @return The submit multi response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      * @see SmeAddress
      * @see SmppTransmitter#submitMulti
      */
    public SubmitMultiResp submitMulti(String msg, MsgFlags flags,
	    SmeAddress src, SmeAddress dst[], SMPPDate del, SMPPDate valid)
	throws java.io.IOException, ie.omk.smpp.SMPPException
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
	    throw new SMPPException("Cannot set replace-if-present flag for "
		    + "messges to more than one destination ESME");

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "submitMulti", "Request send", Debug.DBG_3);
	return ((SubmitMultiResp)resp);
    }

    /** Cancel an already submitted  message.
      * @param s The service type the original message was submitted under
      * @param m The Id of the original message
      * @param d The Destination address of the original message
      * @return The cancel message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      */
    public CancelSMResp cancelMessage(String s, int m, SmeAddress d)
	throws IOException, ie.omk.smpp.SMPPException
    {
	return (this.cancelMessage(s, m, null, d));
    }

    /** Cancel an already submitted  message.
      * @param st The service type the original message was submitted under
      * @param msgId The Id of the original message
      * @param dst The Destination address of the original message (may be null)
      * @param src The source address of the original message (may be null)
      * @return The cancel message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      */
    public CancelSMResp cancelMessage(String st, int msgId,
	    SmeAddress src, SmeAddress dst)
	throws IOException, ie.omk.smpp.SMPPException
    {
	CancelSM s = new CancelSM(nextPacket());
	s.setServiceType(st);
	s.setMessageId(msgId);
	if(dst != null)
	    s.setDestination(dst);
	if(src != null)
	    s.setSource(src);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "cancelMessage", "Request send", Debug.DBG_3);
	return ((CancelSMResp)resp);
    }

    /** Replace an existing  message with a new one.
      * @param msgId The Id of the original message.
      * @param msg The text of the new message (may be null)
      * @param flags The flags of the new message.  Only the registered delivery
      * and the default message Id flags are relevant.
      * @return The replace message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      */
    public ReplaceSMResp replaceMessage(int msgId, String msg, MsgFlags flags)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.replaceMessage(msgId, msg, flags, null, null, null));
    }

    /** Replace an existing  message with a new one.
      * @param msgId The Id of the original message.
      * @param msg The text of the new message (may be null)
      * @param flags The flags of the new message.  Only the registered delivery
      * @param src The Source address of the original message (may be null)
      * and the default message Id flags are relevant.
      * @return The replace message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs
      */
    public ReplaceSMResp replaceMessage(int msgId, String msg, MsgFlags flags,
	    SmeAddress src)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.replaceMessage(msgId, msg, flags, src, null, null));
    }

    /** Replace an existing  message with a new one.
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
    public ReplaceSMResp replaceMessage(int msgId, String msg, MsgFlags flags,
	    SmeAddress src, SMPPDate del, SMPPDate valid)
	throws java.io.IOException, ie.omk.smpp.SMPPException
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

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "replaceMessage", "Request send", Debug.DBG_3);
	return ((ReplaceSMResp)resp);
    }


    /** Get the value of a configurable parameter from the SMSC.
      * @param name The name of the parameter to get
      * @return The param retrieve response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      */
    public ParamRetrieveResp paramRetrieve(String name)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	ParamRetrieve s = new ParamRetrieve(nextPacket());
	s.setParamName(name);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "paramRetrieve", "Request send", Debug.DBG_3);
	return ((ParamRetrieveResp)resp);
    }

    // ---------------- All the query operations below here ----------------

    /** Query the status of a  message.
      * @param msgId The Id of the submitted message
      * @return The query message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      * @see MessageDetails
      */
    public QuerySMResp queryMessage(int msgId)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.queryMessage(msgId, null));
    }

    /** Query the status of a  message.
      * @param msgId The Id of the submitted message
      * @param src The source address of the original message (may be null)
      * @return The query message response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      * @see MessageDetails
      */
    public QuerySMResp queryMessage(int msgId, SmeAddress src)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	QuerySM s = new QuerySM(nextPacket());
	s.setMessageId(msgId);
	if(src != null)
	    s.setSource(src);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "queryMessage", "Request send", Debug.DBG_3);
	return ((QuerySMResp)resp);
    }

    /** Query the last number of messages for an Sme.
      * @param src The address of the Sme to query for.
      * @param num The number of messages to query (0 < num <= 100).
      * @return The query last messages response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      */
    public QueryLastMsgsResp queryLastMsgs(SmeAddress src, int num)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if(src == null)
	    throw new NullPointerException("Source address cannot be null.");

	if(num < 1) num = 1;
	if(num > 100) num = 100;

	QueryLastMsgs s = new QueryLastMsgs(nextPacket());
	s.setSource(src);
	s.setMsgCount(num);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "queryLastMsgs", "Request send", Debug.DBG_3);
	return ((QueryLastMsgsResp)resp);
    }


    /** Query the details of a submitted message.
      * @param msgId The Id of the message to query.
      * @param len The number of bytes of the message text to get.
      * @return The query details response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      */
    public QueryMsgDetailsResp queryMsgDetails(int msgId, int len)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (this.queryMsgDetails(msgId, null, len));
    }

    /** Query the details of a submitted message.
      * @param msgId The Id of the message to query.
      * @param src The source address of the message (may be null)
      * @param len The number of bytes of the message text to get.
      * @return The query details response, or null if asynchronous
      * communication is used.
      * @exception java.io.IOException If a network error occurs.
      */
    public QueryMsgDetailsResp queryMsgDetails(int msgId, SmeAddress src,
	    int len)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	/* Don't forget to do a long comparison on the high value...
	 * Java int's are only 32-bits!!
	 */
	if(msgId < 0 || msgId > 0xffffffffL)
	    throw new NullPointerException("Message Id is out of valid range.");

	// Make sure the length requested is sane!
	if(len < 0)
	    len = 0;
	if(len > 161)
	    len = 161;

	QueryMsgDetails s = new QueryMsgDetails(nextPacket());
	s.setMessageId(msgId);
	if(src != null)
	    s.setSource(src);
	s.setSmLength(len);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "queryMsgDetails", "Request send", Debug.DBG_3);
	return ((QueryMsgDetailsResp)resp);
    }
}
