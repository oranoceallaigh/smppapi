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

package ie.omk.smpp;

import java.io.IOException;

import ie.omk.smpp.Address;

import ie.omk.smpp.message.AlertNotification;
import ie.omk.smpp.message.Bind;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.BindReceiverResp;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.BindTransceiver;
import ie.omk.smpp.message.BindTransceiverResp;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.message.CancelSM;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DataSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.GenericNack;
import ie.omk.smpp.message.MsgFlags;
import ie.omk.smpp.message.ParamRetrieve;
import ie.omk.smpp.message.ParamRetrieveResp;
import ie.omk.smpp.message.QueryLastMsgs;
import ie.omk.smpp.message.QueryLastMsgsResp;
import ie.omk.smpp.message.QueryMsgDetails;
import ie.omk.smpp.message.QueryMsgDetailsResp;
import ie.omk.smpp.message.QuerySM;
import ie.omk.smpp.message.QuerySMResp;
import ie.omk.smpp.message.ReplaceSM;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPRequest;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitMulti;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import ie.omk.smpp.net.SmscLink;

import ie.omk.smpp.util.SMPPDate;

import org.apache.log4j.Logger;

/** Transmitter implementation of the SMPP Connection.
  * @author Oran Kelly
  * @version 1.0
  * @deprecated Use the {@link Connection} parent class instead.
  */
public class SmppTransmitter
    extends ie.omk.smpp.Connection
{
    /** Create a new smpp Transmitter connection
      * @param link The network link to the Smsc
      */
    public SmppTransmitter(SmscLink link)
    {
	super(link, false);
    }

    /** Create a new Smpp transmitter specifying the type of communications
      * desired.
      * @param link The network link object to the Smsc (cannot be null)
      * @param async true for asyncronous communication, false for synchronous.
      * @throws java.lang.NullPointerException If the link is null
      */
    public SmppTransmitter(SmscLink link, boolean async)
    {
	super(link, async);
    }

    /** Bind to the SMSC as a transmitter. This method will
     * send a bind_transmitter packet to the SMSC.  If the network
     * connection to the SMSC is not already open, it will be opened in
     * this method.
     * @param systemID The system ID of this ESME.
     * @param password The password used to authenticate to the SMSC.
     * @param systemType The system type of this ESME.
     * @param sourceRange The source routing information. If null, the defaults
     * at the SMSC will be used.
     * @return The bind response, or null if asynchronous communication is
     * used.
     * @throws java.lang.IllegalArgumentException if a bad <code>type</code>
     * value is supplied.
     * @throws ie.omk.smpp.UnsupportedOperationException if an attempt is made
     * to bind as transceiver while using SMPP version 3.3.
     * @throws ie.omk.smpp.StringTooLongException If any of systemID, password,
     * system type or address range are outside allowed bounds.
     * @throws ie.omk.smpp.InvalidTONException If the TON is invalid.
     * @throws ie.omk.smpp.InvalidNPIException If the NPI is invalid.
     * @throws java.io.IOException If an I/O error occurs while writing the bind
     * packet to the output stream.
     * @throws ie.omk.smpp.AlreadyBoundException If the Connection is already
     * bound.
     * @see ie.omk.smpp.Connection#bind
     */
    public BindResp bind(String systemID, String password,
	    String systemType, Address sourceRange)
	throws java.io.IOException, UnsupportedOperationException, StringTooLongException, InvalidTONException, InvalidNPIException, IllegalArgumentException, AlreadyBoundException
    {
	return (super.bind(
		    TRANSMITTER,
		    systemID,
		    password,
		    systemType,
		    sourceRange.getTON(),
		    sourceRange.getNPI(),
		    sourceRange.getAddress()));
    }

    /** Submit a message to an ESME
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param dst Destination ESME to send the message to (may be null)
      * @return The submit message response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs
      * @see Address
      * @see SmppTransmitter#submitMulti
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public SubmitSMResp submitMessage(String msg, MsgFlags flags,
	    Address dst)
	throws java.io.IOException, StringTooLongException
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
      * @throws java.io.IOException If a network error occurs
      * @see Address
      * @see SmppTransmitter#submitMulti
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public SubmitSMResp submitMessage(String msg, MsgFlags flags,
	    Address src, Address dst)
	throws java.io.IOException, StringTooLongException
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
      * @throws java.io.IOException If a network error occurs
      * @throws ie.omk.smpp.StringTooLongException if the message string is too
      * long.
      * @see Address
      * @see SmppTransmitter#submitMulti
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public SubmitSMResp submitMessage(String msg, MsgFlags flags,
	    Address src, Address dst, SMPPDate del, SMPPDate valid)
	throws java.io.IOException, StringTooLongException
    {
	SubmitSM s = new SubmitSM();
	s.setPriority(flags.priority);
	s.setRegistered(flags.registered);
	s.setEsmClass(flags.esm_class);
	s.setProtocolID(flags.protocol);
	s.setDataCoding(flags.data_coding);
	s.setDefaultMsg(flags.default_msg);
	s.setMessageText(msg);
	s.setDestination(dst);

	if(src != null)
	    s.setSource(src);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending submit_sm request");
	return ((SubmitSMResp)resp);
    }

    /** Submit a message to multiple destinations
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param src Source ESME this message is from (may be null)
      * @param dst Table of Address structures to send the message to
      * @param del The absolute delivery time of the message (may be null)
      * @param valid The validity period of the message, after which it will
      * @return The submit multi response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs
      * @see Address
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public SubmitMultiResp submitMulti(String msg, MsgFlags flags,
	    Address src, Address dst[])
	throws java.io.IOException, StringTooLongException, InvalidDestinationCountException, InvalidReplaceIfPresentException
    {
	return (this.submitMulti(msg, flags, src, dst, null, null));
    }

    /** Submit a message to multiple destinations
      * @param msg The text of the message.  Must be less than 161 characters
      * (may be null)
      * @param flags Message flags information
      * @param src Source ESME this message is from (may be null)
      * @param dst Table of Address structures to send the message to
      * @param del The absolute delivery time of the message (may be null)
      * @param valid The validity period of the message, after which it will
      * @return The submit multi response, or null if asynchronous
      * communication is used.
      * @throws ie.omk.smpp.InvalidDestinationCountException if the
      * destination table contains 0 addresses. There is currently no upper
      * limit.
      * @throws ie.omk.smpp.InvalidReplaceIfPresentException if the
      * replace-if-present flag is set and there are more than 1 destination.
      * @throws java.io.IOException If a network error occurs
      * @throws ie.omk.smpp.StringTooLongException if the message string is too
      * long.
      * @throws InvalidDestinationCountException If the number of destinations
      * to submit the message to is outside allowed limits.
      * @throws InvalidReplaceIfPresentException If the replace-if-present flag
      * was set and there was more than one destination specified.
      * @see Address
      * @see SmppTransmitter#submitMulti
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public SubmitMultiResp submitMulti(String msg, MsgFlags flags,
	    Address src, Address dst[], SMPPDate del, SMPPDate valid)
	throws java.io.IOException, StringTooLongException, InvalidDestinationCountException, InvalidReplaceIfPresentException
    {
	int loop = 0;
	SubmitMulti s = new SubmitMulti();
	s.setPriority(flags.priority);
	s.setRegistered(flags.registered);
	s.setEsmClass(flags.esm_class);
	s.setProtocolID(flags.protocol);
	s.setDataCoding(flags.data_coding);
	s.setDefaultMsg(flags.default_msg);
	s.setMessageText(msg);

	if(src != null)
	    s.setSource(src);

	// Just to make sure a message doesn't get sent to 0 destinations
	if(dst == null || dst.length == 0)
	    throw new InvalidDestinationCountException();

	// Add in all the destinations
	ie.omk.smpp.message.DestinationTable t = s.getDestinationTable();
	for(loop=0; loop<dst.length; loop++) {
	    t.add(dst[loop]);
	}

	// Cannot use replace-if-present to more than one destination
	if(dst.length > 1 && flags.replace_if_present)
	    throw new InvalidReplaceIfPresentException();

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending submit_multi request");
	return ((SubmitMultiResp)resp);
    }

    /** Cancel an already submitted  message.
      * @param st The service type the original message was submitted under
      * @param msgId The Id of the original message
      * @param d The Destination address of the original message
      * @return The cancel message response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public CancelSMResp cancelMessage(String st, String msgId, Address d)
	throws IOException, StringTooLongException
    {
	return (this.cancelMessage(st, msgId, null, d));
    }

    /** Cancel an already submitted  message.
      * @param st The service type the original message was submitted under
      * @param msgId The Id of the original message
      * @param dst The Destination address of the original message (may be null)
      * @param src The source address of the original message (may be null)
      * @return The cancel message response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public CancelSMResp cancelMessage(String st, String msgId,
	    Address src, Address dst)
	throws IOException, StringTooLongException
    {
	CancelSM s = new CancelSM();
	s.setServiceType(st);
	s.setMessageId(msgId);
	if(dst != null)
	    s.setDestination(dst);
	if(src != null)
	    s.setSource(src);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending cancel_sm request");
	return ((CancelSMResp)resp);
    }

    /** Replace an existing  message with a new one.
      * @param msgId The Id of the original message.
      * @param msg The text of the new message (may be null)
      * @param flags The flags of the new message.  Only the registered delivery
      * and the default message Id flags are relevant.
      * @return The replace message response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public ReplaceSMResp replaceMessage(String msgId, String msg,
	    MsgFlags flags)
	throws java.io.IOException, StringTooLongException
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
      * @throws java.io.IOException If a network error occurs
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public ReplaceSMResp replaceMessage(String msgId, String msg,
	    MsgFlags flags, Address src)
	throws java.io.IOException, StringTooLongException
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
      * @throws java.io.IOException If a network error occurs
      * @deprecated This method will disappear from this class within the next 2
      * releases.
      */
    public ReplaceSMResp replaceMessage(String msgId, String msg,
	    MsgFlags flags, Address src, SMPPDate del, SMPPDate valid)
	throws java.io.IOException, StringTooLongException
    {
	ReplaceSM s = new ReplaceSM();
	s.setMessageId(msgId);
	s.setPriority(flags.priority);
	s.setRegistered(flags.registered);
	s.setEsmClass(flags.esm_class);
	s.setProtocolID(flags.protocol);
	s.setDataCoding(flags.data_coding);
	s.setDefaultMsg(flags.default_msg);
	if(src != null)
	    s.setSource(src);
	if(msg != null)
	    s.setMessageText(msg);
	if(del != null)
	    s.setDeliveryTime(del);
	if(valid != null)
	    s.setExpiryTime(valid);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending replace_sm request");
	return ((ReplaceSMResp)resp);
    }


    /** Get the value of a configurable parameter from the SMSC.
      * @param name The name of the parameter to get
      * @return The param retrieve response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public ParamRetrieveResp paramRetrieve(String name)
	throws java.io.IOException, StringTooLongException
    {
	ParamRetrieve s = new ParamRetrieve();
	s.setParamName(name);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending param_retrieve request");
	return ((ParamRetrieveResp)resp);
    }

    // ---------------- All the query operations below here ----------------

    /** Query the status of a  message.
      * @param msgId The Id of the submitted message
      * @return The query message response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public QuerySMResp queryMessage(String msgId)
	throws java.io.IOException, StringTooLongException
    {
	return (this.queryMessage(msgId, null));
    }

    /** Query the status of a  message.
      * @param msgId The Id of the submitted message
      * @param src The source address of the original message (may be null)
      * @return The query message response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public QuerySMResp queryMessage(String msgId, Address src)
	throws java.io.IOException, StringTooLongException
    {
	QuerySM s = new QuerySM();
	s.setMessageId(msgId);
	if(src != null)
	    s.setSource(src);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending query_sm request");
	return ((QuerySMResp)resp);
    }

    /** Query the last number of messages for an Sme.
      * @param src The address of the Sme to query for.
      * @param num The number of messages to query (0 &lt; num &lt;= 100).
      * @return The query last messages response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public QueryLastMsgsResp queryLastMsgs(Address src, int num)
	throws java.io.IOException, NumberOutOfRangeException
    {
	if(src == null)
	    throw new NullPointerException("Source address cannot be null.");

	if(num < 1)
	    num = 1;
	if(num > 100)
	    num = 100;

	QueryLastMsgs s = new QueryLastMsgs();
	s.setSource(src);
	s.setMsgCount(num);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending query_last_msgs request");
	return ((QueryLastMsgsResp)resp);
    }


    /** Query the details of a submitted message.
      * @param msgId The Id of the message to query.
      * @param len The number of bytes of the message text to get.
      * @return The query details response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public QueryMsgDetailsResp queryMsgDetails(String msgId, int len)
	throws java.io.IOException, StringTooLongException
    {
	return (this.queryMsgDetails(msgId, null, len));
    }

    /** Query the details of a submitted message.
      * @param msgId The Id of the message to query.
      * @param src The source address of the message (may be null)
      * @param len The number of bytes of the message text to get.
      * @return The query details response, or null if asynchronous
      * communication is used.
      * @throws java.io.IOException If a network error occurs.
      */
    public QueryMsgDetailsResp queryMsgDetails(String msgId, Address src,
	    int len)
	throws java.io.IOException, StringTooLongException
    {
	// Make sure the length requested is sane!
	if(len < 0)
	    len = 0;
	if(len > 161)
	    len = 161;

	QueryMsgDetails s = new QueryMsgDetails();
	s.setMessageId(msgId);
	if(src != null)
	    s.setSource(src);
	s.setSmLength(len);

	SMPPResponse resp = sendRequest(s);
	logger.info("Sending query_msg_details request");
	return ((QueryMsgDetailsResp)resp);
    }
}
