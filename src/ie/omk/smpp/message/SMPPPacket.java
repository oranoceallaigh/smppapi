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
package ie.omk.smpp.message;

import java.io.*;
import java.net.SocketException;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.debug.Debug;

/** This is the abstract class that all SMPP messages are inherited from.
 *  @author Oran Kelly
 *  @version 1.0
 */
public abstract class SMPPPacket
{
    /** Command Id: Negative Acknowledgement */
    public static final int ESME_NACK 		= 0x80000000;
    /** Command Id: Bind Receiver */
    public static final int ESME_BNDRCV 	= 0x00000001;
    /** Command Id: Bind Receiver Response */
    public static final int ESME_BNDRCV_RESP 	= 0x80000001;
    /** Command Id: Bind transmitter */
    public static final int ESME_BNDTRN 	= 0x00000002;
    /** Command Id: Bind transmitter response */
    public static final int ESME_BNDTRN_RESP 	= 0x80000002;
    /** Command Id: Unbind */
    public static final int ESME_UBD		= 0x00000006;
    /** Command Id: Unbind response */
    public static final int ESME_UBD_RESP	= 0x80000006;
    /** Command Id: Submit message */
    public static final int ESME_SUB_SM		= 0x00000004;
    /** Command Id: Submit message response */
    public static final int ESME_SUB_SM_RESP	= 0x80000004;
    /** Command Id: Submit multiple messages */
    public static final int ESME_SUB_MULTI	= 0x00000021;
    /** Command Id: Submit multi response */
    public static final int ESME_SUB_MULTI_RESP	= 0x80000021;
    /** Command Id: Deliver Short message */
    public static final int SMSC_DELIVER_SM	= 0x00000005;
    /** Command Id: Deliver message response */
    public static final int SMSC_DELIVER_SM_RESP= 0x80000005;
    /** Command Id: Query message */
    public static final int ESME_QUERY_SM	= 0x00000003;
    /** Command Id: Query message response */
    public static final int ESME_QUERY_SM_RESP	= 0x80000003;
    /** Command Id: Query last messages */
    public static final int ESME_QUERY_LAST_MSGS= 0x00000023;
    /** Command Id: Query last messages response */
    public static final int ESME_QUERY_LAST_MSGS_RESP = 0x80000023;
    /** Command Id: Query message details */
    public static final int ESME_QUERY_MSG_DETAILS = 0x00000024;
    /** Command Id: Query message details response */
    public static final int ESME_QUERY_MSG_DETAILS_RESP	= 0x80000024;
    /** Command Id: Cancel message */
    public static final int ESME_CANCEL_SM	= 0x00000008;
    /** Command Id: Cancel message response */
    public static final int ESME_CANCEL_SM_RESP	= 0x80000008;
    /** Command Id: Replace message */
    public static final int ESME_REPLACE_SM	= 0x00000007;
    /** Command Id: replace message response */
    public static final int ESME_REPLACE_SM_RESP= 0x80000007;
    /** Command Id: Enquire Link */
    public static final int ESME_QRYLINK	= 0x00000015;
    /** Command Id: Enquire link respinse */
    public static final int ESME_QRYLINK_RESP	= 0x80000015;
    /** Command Id: Parameter retrieve */
    public static final int ESME_PARAM_RETRIEVE	= 0x00000022;
    /** Command Id: Paramater retrieve response */
    public static final int ESME_PARAM_RETRIEVE_RESP = 0x80000022;


    /** Message state at Smsc: En route */
    public static final int SM_STATE_EN_ROUTE		= 1;
    /** Message state at Smsc: Delivered (final) */
    public static final int SM_STATE_DELIVERED		= 2;
    /** Message state at Smsc: Expired (final) */
    public static final int SM_STATE_EXPIRED		= 3;
    /** Message state at Smsc: Deleted (final) */
    public static final int SM_STATE_DELETED		= 4;
    /** Message state at Smsc: Undeliverable (final) */
    public static final int SM_STATE_UNDELIVERABLE	= 5;
    /** Message state at Smsc: Accepted */
    public static final int SM_STATE_ACCEPTED		= 6;
    /** Message state at Smsc: Invalid message (final) */
    public static final int SM_STATE_INVALID		= 7;

    /** Esm class: Mobile Terminated; Normal delivery, no address swapping */
    public static final int SMC_MT			= 1;
    /** Esm class: Mobile originated */
    public static final int SMC_MO			= 2;
    /** Esm class: Mobile Originated / Terminated */
    public static final int SMC_MOMT			= 3;
    /** Esm class: Delivery receipt, no address swapping */
    public static final int SMC_RECEIPT						= 4;
    /** Esm class: Predefined message */
    public static final int SMC_DEFMSG			= 8;
    /** Esm class: Normal delivery , address swapping on */
    public static final int SMC_LOOPBACK_RECEIPT	= 16;
    /** Esm class: Delivery receipt, address swapping on */
    public static final int SMC_RECEIPT_SWAP		= 20;
    /** Esm class: Store message, do not send to Kernel */
    public static final int SMC_STORE			= 32;
    /** Esm class: Store message and send to kernel */
    public static final int SMC_STORE_FORWARD		= 36;
    /** Esm class: Distribution submission */
    public static final int SMC_DLIST			= 64;
    /** Esm class: Multiple recipient submission */
    public static final int SMC_MULTI			= 128;
    /** Esm class: Distribution list and multiple recipient submission */
    public static final int SMC_CAS_DL			= 256;
    /** Esm class: Escalated message FFU */
    public static final int SMC_ESCALATED		= 512;
    /** Esm class: Submit with replace message */
    public static final int SMC_SUBMIT_REPLACE		= 1024;
    /** Esm class: Memory capacity error */
    public static final int SMC_MCE			= 2048;

    /** Esme error code: No error */
    public static final int ESME_ROK			= 0;


    /** Command ID. */
    protected int commandId = 0;

    /** Command status. */
    protected int commandStatus = 0;

    /** Packet sequence number. */
    protected int sequenceNum = 0;

    /* Almost all packets use one or more of these.
     * These attributes were all stuck in here for easier maintenance...
     * instead of altering 5 different packets, just alter it here!!
     * Special cases like SubmitMulti and QueryMsgDetailsResp maintain
     * their own destination tables.  Any packets that wish to use
     * these attribs should override the appropriate methods defined
     * below to be public and just call super.method()
     */

    /** Source address */
    protected SmeAddress	source;
    /** Destination address */
    protected SmeAddress	destination;
    /** Set of message type flags */
    protected MsgFlags		flags;
    /** The text of a short message */
    protected String		message;
    /** Service type for this msg */
    protected String		serviceType;
    /** Scheduled delivery time */
    protected SMPPDate		deliveryTime;
    /** Scheduled expiry time */
    protected SMPPDate		expiryTime;
    /** Date of reaching final state */
    protected SMPPDate		finalDate;
    /** Smsc allocated message Id */
    protected String		messageId;
    /** Status of message */
    protected int		messageStatus;
    /** Error associated with message */
    protected int		errorCode;	


    /** Create a new SMPPPacket with specified Id and sequence number.
      * @param id Command Id value
      * @param seqNum Command Sequence number
      */
    public SMPPPacket(int id, int seqNum)
    {
	this.commandId = id;
	this.sequenceNum = seqNum;

	// Flags should always be created (rest of code assumes it is.)
	flags = new MsgFlags();
    }

    /** Read an SMPPPacket header from an InputStream
      * @param in InputStream to read from
      * @exception IOException if there's an error reading from the input
      * stream.
      */
    public SMPPPacket(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	// Flags should always be created (rest of code assumes it is.)
	flags = new MsgFlags();

	int cmdLen = SMPPIO.readInt(in, 4);
	this.commandId = SMPPIO.readInt(in, 4);
	this.commandStatus = SMPPIO.readInt(in, 4);
	this.sequenceNum = SMPPIO.readInt(in, 4);
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return The size in bytes of the packet
      */
    public abstract int getCommandLen();

    /** Get the length of the SMPP header.
      */
    protected int getHeaderLen()
    {
	// 1 4-byte integer each for command length, id, status and sequence
	// number.
	return (16);
    }

    /** Get the Command Id of this SMPP packet.
      * @return The Command Id of this packet
      */
    public int getCommandId()
    {
	return (commandId);
    }

    /** Get the status of this packet.
      * @return The error status of this packet (only relevent to Response
      * packets)
      */
    public int getCommandStatus()
    {
	return (commandStatus);
    }

    /** Get the sequence number of this packet.
      * @return The sequence number of this SMPP packet
      */
    public int getSequenceNum()
    {
	return (sequenceNum);
    }


    /* ************************************************************** */
    /*          Methods to set and read message attributes            */
    /* ************************************************************** */

    /** Set the source address.
      * Not used by all SMPP Packet types.
      * @see ie.omk.smpp.message.SubmitSM
      * @see ie.omk.smpp.message.DeliverSM
      * XXX Add other packets.
      */
    public void setSource(SmeAddress s)
    {
	if(s != null) {
	    this.source = s;
	    if(Debug.dbg)
		Debug.d(this, "setSource", "Source set to "+s, Debug.DBG_4);
	} else if(Debug.dbg) {
	    Debug.d(this, "setSource", "Attempt to set source to null", Debug.DBG_3);
	}
    }

    /** Get the source address.
      * Not used by all SMPP Packet types.
      * @see ie.omk.smpp.message.SubmitSM
      */
    public SmeAddress getSource()
    {
	if(source != null)
	    return (source);
	else
	    return (null);
    }

    /** Set the destination address.
      * Not used by all SMPP Packet types.
      * @see ie.omk.smpp.message.SubmitSM
      */
    public void setDestination(SmeAddress s)
    {
	if(s != null) {
	    this.destination = s;
	    if(Debug.dbg)
		Debug.d(this, "setDestination", "Dest set to"+s, Debug.DBG_4);
	} else {
	    Debug.d(this, "setDestination", "Attempt to set destination to "
		    + "null", Debug.DBG_3);
	}
    }

    /** Get the destination address.
      * Not used by all SMPP Packet types.
      * @see ie.omk.smpp.message.SubmitSM
      */
    public SmeAddress getDestination()
    {
	if(destination != null) {
	    return (destination);
	} else {
	    return (null);
	}
    }

    /** Set the message flags.
      * Not used by all SMPP Packet types.
      * @see ie.omk.smpp.message.SubmitSM
      */
    public void setMessageFlags(MsgFlags f)
    {
	if(f != null) {
	    flags = f;
	    if(Debug.dbg)
		Debug.d(this, "setMessageFlags", "Message flags set to "+f, Debug.DBG_4);
	} else {
	    Debug.d(this, "setMessageFlags", "Attempt to set flags to null",
		    Debug.DBG_3);
	}
    }

    /** Set the 'priority' message flag. */
    public void setPriority(boolean b)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.priority = b;
	if(Debug.dbg)
	    Debug.d(this, "setPriority", b, Debug.DBG_4);
    }

    /** Set the 'registered' message flag. */
    public void setRegistered(boolean b)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.registered = b;
	if(Debug.dbg)
	    Debug.d(this, "setRegistered", b, Debug.DBG_4);
    }

    /** Set the 'replace if present' message flag. */
    public void setReplaceIfPresent(boolean b)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.replace_if_present = b;
	if(Debug.dbg)
	    Debug.d(this, "setReplaceIfPresent", b, Debug.DBG_4);
    }
    
    /** Set the esm class in the message flags. */
    public void setEsmClass(int c)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.esm_class = c;
	if(Debug.dbg)
	    Debug.d(this, "setEsmClass", c, Debug.DBG_4);
    }

    /** Set the protocol Id in the message flags. */
    public void setProtocolId(int id)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.protocol = id;
	if(Debug.dbg)
	    Debug.d(this, "setProtocol", id, Debug.DBG_4);
    }

    /** Set the GSM data coding type in the message flags.
      * @see ie.omk.smpp.util.GSMConstants
      */
    public void setDataCoding(int dc)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.data_coding = dc;
	if(Debug.dbg)
	    Debug.d(this, "setDataCoding", dc, Debug.DBG_4);
    }

    /** Set the default message id in the message flags. */
    public void setDefaultMsg(int id)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.default_msg = id;
	if(Debug.dbg)
	    Debug.d(this, "setDefaultMsg", id, Debug.DBG_4);
    }

    /** Get the message flags. */
    public MsgFlags getMessageFlags()
    {
	return (flags);
    }

    public boolean isRegistered()
    {
	return (flags.registered);
    }

    public boolean isPriority()
    {
	return (flags.priority);
    }

    public boolean isReplaceIfPresent()
    {
	return (flags.replace_if_present);
    }
    public int getEsmClass()
    {
	return (flags.esm_class);
    }
    public int getProtocolId()
    {
	return (flags.protocol);
    }
    public int getDataCoding()
    {
	return (flags.data_coding);
    }
    public int getDefaultMsgId()
    {
	return (flags.default_msg);
    }

    /** Set the text of the message (max 160 characters).
      * @param text The short message text.
      * @exception ie.omk.smpp.SMPPException if the message is too long.
      */
    public void setMessageText(String text)
	throws ie.omk.smpp.SMPPException
    {
	if(text == null) {
	    message = null;
	    return;
	}

	if(text.length() < 161) {
	    this.message = text;
	    if(Debug.dbg)
		Debug.d(this, "setMessageText", text, Debug.DBG_4);
	} else {
	    Debug.d(this, "setMessageText", "Message too long", Debug.DBG_1);
	    throw new SMPPException("SM Text length must be < 160 characters");
	}
    }

    /** Get the text of the message. */
    public String getMessageText()
    {
	return (message);
    }

    /** Get the length of the message text.
      * @return The length of the message (in bytes/characters).
      */
    public int getMessageLen()
    {
	return (message == null) ? 0 : message.length();
    }

    /** Set the service type.
      * @exception ie.omk.smpp.SMPPException If the service type is invalid.
      */
    public void setServiceType(String type)
	throws ie.omk.smpp.SMPPException
    {
	if(type == null) {
	    serviceType = null;
	    return;
	}

	if(type.length() < 6) {
	    this.serviceType = type;
	    if(Debug.dbg)
		Debug.d(this, "setServiceType", type, Debug.DBG_4);
	} else {
	    Debug.d(this, "setServiceType", "Service type too long",
		    Debug.DBG_1);
	    throw new SMPPException("Service type must be < 6 characters");
	}
    }

    /** Get the service type. */
    public String getServiceType()
    {
	return (serviceType);
    }

    /** Set the scheduled delivery time for the short message.
      * @param d The date and time the message should be delivered.
      * @throws ie.omk.smpp.SMPPException (XXX can the date be invalid?)
      */
    public void setDeliveryTime(SMPPDate d)
	throws ie.omk.smpp.SMPPException
    {
	this.deliveryTime = d;
	if(Debug.dbg)
	    Debug.d(this, "setDeliveryTime", "Delivery time set to " + d,
		    Debug.DBG_4);
    }

    /** Get the current value of the scheduled delivery time for the short
      * message.
      */
    public SMPPDate getDeliveryTime()
    {
	return (deliveryTime);
    }

    /** Set the expiry time of the message.
      * If the message is not delivered by time 'd', it will be cancelled and
      * never delivered to it's destination.
      * @param d the date and time the message should expire.
      * @exception ie.omk.smpp.SMPPException (XXX can the time be invalid?)
      */
    public void setExpiryTime(SMPPDate d)
	throws ie.omk.smpp.SMPPException
    {
	expiryTime = d;
	if(Debug.dbg)
	    Debug.d(this, "setExpiryTime", "Expiry time set to " + d,
		    Debug.DBG_4);
    }

    /** Get the current value for the expiry time of the message.
      */
    public SMPPDate getExpiryTime()
    {
	return (expiryTime);
    }

    /** Set the final date of the message.
      * The final date is the date and time that the message reached it's final
      * destination.
      * @param d the date the message was delivered.
      * @exception ie.omk.smpp.SMPPException (XXX can the time be invalid?)
      */
    public void setFinalDate(SMPPDate d)
	throws ie.omk.smpp.SMPPException
    {
	finalDate = d;
	if(Debug.dbg)
	    Debug.d(this, "setFinalDate", "Final date set to " + d,
		    Debug.DBG_4);
    }

    /** Get the final date of the message.
      */
    public SMPPDate getFinalDate()
    {
	return (finalDate);
    }

    /** Set the message Id.
      * Each submitted short message is assigned an Id by the SMSC which is used
      * to uniquely identify it. SMPP v3.3 message Ids are hexadecimal numbers
      * up to 9 characters long. This gives them a range of 0x0 - 0xffffffff.
      * @param id The message's id.
      * @exception ie.omk.smpp.SMPPException if the message id is invalid.
      */
    public void setMessageId(String id)
	throws ie.omk.smpp.SMPPException
    {
	if (id == null) {
	    this.messageId = null;
	} else {
	    if (id.length() > 9)
		throw new SMPPException("Message Id too long. Max 8 digits.");
	    else
		this.messageId = id;
	}

	if(Debug.dbg)
	    Debug.d(this, "setMessageId", id, Debug.DBG_4);
    }
    
    /** Get the message id.
      */
    public String getMessageId()
    {
	return (messageId);
    }

    /** Set the message status. This is different to the command status field.
      * XXX describe the message status.
      * @param st The message status.
      * @exception ie.omk.smpp.SMPPException if the status is invalid.
      */
    public void setMessageStatus(int st)
	throws ie.omk.smpp.SMPPException
    {
	messageStatus = st;
	if(Debug.dbg)
	    Debug.d(this, "setMessageStatus", st, Debug.DBG_4);
    }

    /** Get the message status.
      */
    public int getMessageStatus()
    {
	return (messageStatus);
    }

    /** Set the error code.
      * @param code The error code.
      */
    public void setErrorCode(int code)
	throws ie.omk.smpp.SMPPException
    {
	errorCode = code; 
	if(Debug.dbg)
	    Debug.d(this, "setErrorCode", code, Debug.DBG_4);
    }

    /** Get the error code.
      */
    public int getErrorCode()
    {
	return (errorCode);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("header: " + getHeaderLen() + ", "
		+ Integer.toHexString(commandId) + ", "
		+ commandStatus + ", "
		+ Integer.toHexString(sequenceNum));
    }

    /** Encode the body of the SMPP Packet to the output stream. Sub classes
      * should override this method to output their packet-specific fields. This
      * method is called from SMPPPacket.writeTo(java.io.OutputStream) to
      * encode the message.
      * @param out The output stream to write to.
      * @exception java.io.IOException if there's an error writing to the output
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	// packet has no body!
    }

    /** Write the byte representation of this SMPP packet to an OutputStream
      * @param out The OutputStream to use
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    public final void writeTo(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	// Make sure the size is set properly
	int commandLen = getCommandLen();

	SMPPIO.writeInt(commandLen, 4, out);
	SMPPIO.writeInt(commandId, 4, out);
	SMPPIO.writeInt(commandStatus, 4, out);
	SMPPIO.writeInt(sequenceNum, 4, out);

	encodeBody(out);
	out.flush();

	if(Debug.dbg)
	    Debug.d(this, "writeTo", "Packet written to "
		    + out.getClass().getName(), Debug.DBG_4);
    }

    /** Read an SMPPPacket from an InputStream.  The type of the packet is
      * determined from the Command Id value in the header and the appropriate
      * message class is created, read and returned.
      * @param in The InputStream to read the packet from
      * @return An SMPP message upcast to SMPPPacket.  The actual type can be
      * determined from SMPPPacket.getCommandId()
      * @exception java.io.EOFException If the end of stream is reached
      * @exception java.io.IOException If an I/O error occurs
      */
    public static SMPPPacket readPacket(InputStream in)
	throws java.net.SocketException, java.io.IOException,
	    ie.omk.smpp.SMPPException
    {
	// XXX should this method throw SMPPException or catch it.


	SMPPPacket response = null;
	byte b[] = new byte[4];

	for (int loop = 0; loop < 4; loop++)
	    b[loop] = new Integer(in.read()).byteValue();

	int cmdLen = SMPPIO.bytesToInt(b, 0, 4);
	byte[] buf = new byte[cmdLen];
	int ptr = 4;

	System.arraycopy(b, 0, buf, 0, 4);
	while (ptr < cmdLen) {
	    int c = in.read(buf, ptr, (cmdLen - ptr));
	    if(c == -1)
		throw new EOFException("EOS reached. No data available");

	    ptr += c;
	}

	int cmdId = SMPPIO.bytesToInt(buf, 4, 4);

	// XXX
	Debug.dump(buf, 0, cmdLen);
	Debug.d(SMPPPacket.class, "readPacket",
		"Header(len:" + cmdLen
		+ ", id:" + Integer.toHexString(cmdId), Debug.DBG_2);

	ByteArrayInputStream bin = new ByteArrayInputStream(buf);
	switch(cmdId) {
	    case ESME_NACK:
		Debug.d(SMPPPacket.class,
			"readPacket", "GenericNack", Debug.DBG_3);
		response = new GenericNack(bin);
		break;

	    case ESME_BNDRCV:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindReceiver", Debug.DBG_3);
		response = new BindReceiver(bin);
		break;

	    case ESME_BNDRCV_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindReceiverResp", Debug.DBG_3);
		response = new BindReceiverResp(bin);
		break;

	    case ESME_BNDTRN:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindTransmitter", Debug.DBG_3);
		response = new BindTransmitter(bin);
		break;

	    case ESME_BNDTRN_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindTransmitterResp", Debug.DBG_3);
		response = new BindTransmitterResp(bin);
		break;

	    case ESME_UBD:
		Debug.d(SMPPPacket.class,
			"readPacket", "Unbind", Debug.DBG_3);
		response = new Unbind(bin);
		break;

	    case ESME_UBD_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "UnbindResp", Debug.DBG_3);
		response = new UnbindResp(bin);
		break;

	    case ESME_SUB_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitSM", Debug.DBG_3);
		response = new SubmitSM(bin);
		break;

	    case ESME_SUB_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitSMResp", Debug.DBG_3);
		response = new SubmitSMResp(bin);
		break;

	    case ESME_SUB_MULTI:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitMulti", Debug.DBG_3);
		response = new SubmitMulti(bin);
		break;

	    case ESME_SUB_MULTI_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitMultiResp", Debug.DBG_3);
		response = new SubmitMultiResp(bin);
		break;

	    case SMSC_DELIVER_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "DeliverSm", Debug.DBG_3);
		response = new DeliverSM(bin);
		break;

	    case SMSC_DELIVER_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "DeliverSMResp", Debug.DBG_3);
		response = new DeliverSMResp(bin);
		break;

	    case ESME_QUERY_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "QuerySM", Debug.DBG_3);
		response = new QuerySM(bin);
		break;

	    case ESME_QUERY_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "QuerySMResp", Debug.DBG_3);
		response = new QuerySMResp(bin);
		break;

	    case ESME_QUERY_LAST_MSGS:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryLastMsgs", Debug.DBG_3);
		response = new QueryLastMsgs(bin);
		break;

	    case ESME_QUERY_LAST_MSGS_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryLastMsgsResp", Debug.DBG_3);
		response = new QueryLastMsgsResp(bin);
		break;

	    case ESME_QUERY_MSG_DETAILS:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryMsgDetails", Debug.DBG_3);
		response = new QueryMsgDetails(bin);
		break;

	    case ESME_QUERY_MSG_DETAILS_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryMsgDetailsResp", Debug.DBG_3);
		response = new QueryMsgDetailsResp(bin);
		break;

	    case ESME_CANCEL_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "CancelSM", Debug.DBG_3);
		response = new CancelSM(bin);
		break;

	    case ESME_CANCEL_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "CancelSMResp", Debug.DBG_3);
		response = new CancelSMResp(bin);
		break;

	    case ESME_REPLACE_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "ReplaceSM", Debug.DBG_3);
		response = new ReplaceSM(bin);
		break;

	    case ESME_REPLACE_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "ReplaceSMResp", Debug.DBG_3);
		response = new ReplaceSMResp(bin);
		break;

	    case ESME_QRYLINK:
		Debug.d(SMPPPacket.class,
			"readPacket", "EnquireLink", Debug.DBG_3);
		response = new EnquireLink(bin);
		break;

	    case ESME_QRYLINK_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "EnquireLinkResp", Debug.DBG_3);
		response = new EnquireLinkResp(bin);
		break;

	    case ESME_PARAM_RETRIEVE:
		Debug.d(SMPPPacket.class,
			"readPacket", "ParamRetrieve", Debug.DBG_3);
		response = new ParamRetrieve(bin);
		break;

	    case ESME_PARAM_RETRIEVE_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "ParamRetrieveResp", Debug.DBG_3);
		response = new ParamRetrieveResp(bin);
		break;

	    default:
		Debug.d(SMPPPacket.class,
			"readPacket", "Unknown Packet", Debug.DBG_3);
		throw new SMPPException("Unidentified Packet on input stream. "
			+ "id=" + String.valueOf(cmdId));
	}

	return (response);
    }
}
