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
package ie.omk.smpp;

import java.io.*;
import java.util.*;
import ie.omk.smpp.message.*;
import ie.omk.debug.Debug;

/** Structure returned by the message query functions of SmppTransmitter.
  * The main use of this Object is to return query results.  However,
  * any type of SMPPPacket can be passed to the constructor and as many
  * fields as are available will be filled in.  All other fields will
  * default to their Null values (0 for integers, null for Strings)
  * @see SmppTransmitter#queryMessage
  * @see SmppTransmitter#queryMsgDetails
  * @author Oran Kelly
  * @version 1.0
  */
public class MessageDetails
{
// File identifier string: used for debug output
	private static String FILE = "MessageDetails";

	/** Message Id assigned by the SMSC to the message */
	public int 				messageId = 0;

	/** Date and time (SMSC local time) that message reached a final state */
	public Date				finalDate = null;

	/** Current status of the message */
	public int			status =  0;

	/** Error code associated with this message */
	public int			error =  0;

	/** Service type this message was submitted under */
	public String			serviceType = null;

	/** Source address this message came from */
	public SmeAddress		sourceAddr = null;

	/** Table of all destinations this message was successfully delivered to */
	public Vector			destinationTable = new Vector();

	/** Message flags structure.
	  * @see MsgFlags
	  */
	public MsgFlags			flags = new MsgFlags();

	/** Date and time this message is scheduled to be delivered on */
	public Date				deliveryTime = null;

	/** Date and time this message will expire on */
	public Date				expiryTime = null;

	/** Text of the int message */
	public String			message = null;


	/** Create a new Details object with all null values.
	  */
	public MessageDetails()
		{ }

	/** Create a new an Objects with details from a QueryMessageDetailsResp.
	  * This type of packet will fill in all the available fields.
	  */
	public MessageDetails(QueryMsgDetailsResp r)
	{
		messageId = r.getMessageId();
		finalDate = r.getFinalDate();
		status = r.getMessageStatus();
		error = r.getErrorCode();
		serviceType = r.getServiceType();
		sourceAddr = r.getSource();
		deliveryTime = r.getDeliveryTime();
		expiryTime = r.getExpiryTime();
		message = r.getMessageText();

		flags.priority = r.isPriority();
		flags.registered = r.isRegistered();
		flags.data_coding = r.getDataCoding();
		flags.protocol = r.getProtocolId();

		// Haven't got the destination table yet...
	}

	/** Create a new Details object from a QuerySM Response.  Fields filled
	  * in: messageId, finalDate, status and error code
	  */
	public MessageDetails(QuerySMResp r)
	{
		messageId = r.getMessageId();
		finalDate = r.getFinalDate();
		status = r.getMessageStatus();
		error = r.getErrorCode();
	}

	/** Create a new Details object from a DeliverSM packet.  Fields filled
	  * in: serviceType, sourceAddr, destinationTable (1 element),
	  * flags.protocol, flags.data_coding, flags.esm_class and message.
	  */
	public MessageDetails(DeliverSM r)
	{
		serviceType = r.getServiceType();
		sourceAddr = r.getSource();
		destinationTable.addElement(r.getDestination());
		flags.protocol = r.getProtocolId();
		flags.data_coding = r.getDataCoding();
		flags.esm_class = r.getEsmClass();
		message = r.getMessageText();
	}

	/** Take any Smpp packet and fill in as many fields as are available.
	  * Any fields that cannot be read from the packet will default to
	  * null (ints will equal 0, Strings will equal null)
	  */
	public MessageDetails(SMPPPacket p)
	{
		int loop=0;
		SmeAddress add[] = null;

		if(p == null)
			return;

		if(p instanceof SubmitSM)
		{
			serviceType = ((SubmitSM)p).getServiceType();
			sourceAddr = ((SubmitSM)p).getSource();
			destinationTable.addElement(((SubmitSM)p).getDestination());
			destinationTable.trimToSize();
			flags.priority = ((SubmitSM)p).isPriority();
			flags.registered = ((SubmitSM)p).isRegistered();
			flags.default_msg = ((SubmitSM)p).getDefaultMsgId();
			flags.protocol = ((SubmitSM)p).getProtocolId();
			flags.data_coding = ((SubmitSM)p).getDataCoding();
			deliveryTime = ((SubmitSM)p).getDeliveryTime();
			expiryTime = ((SubmitSM)p).getExpiryTime();
			message = ((SubmitSM)p).getMessageText();
		}
		else if(p instanceof SubmitMulti)
		{
			serviceType = ((SubmitMulti)p).getServiceType();
			sourceAddr = ((SubmitMulti)p).getSource();

			add = ((SubmitMulti)p).getDestAddresses();
			for(loop=0; loop<add.length; loop++)
				destinationTable.addElement(add[loop]);

			flags.priority = ((SubmitMulti)p).isPriority();
			flags.registered = ((SubmitMulti)p).isRegistered();
			flags.default_msg = ((SubmitMulti)p).getDefaultMsgId();
			flags.protocol = ((SubmitMulti)p).getProtocolId();
			flags.data_coding = ((SubmitMulti)p).getDataCoding();
			deliveryTime = ((SubmitMulti)p).getDeliveryTime();
			expiryTime = ((SubmitMulti)p).getExpiryTime();
			message = ((SubmitMulti)p).getMessageText();
		}
		else if(p instanceof ReplaceSM)
		{
			messageId = ((ReplaceSM)p).getMessageId();
			sourceAddr = ((ReplaceSM)p).getSource();
			deliveryTime = ((ReplaceSM)p).getDeliveryTime();
			expiryTime = ((ReplaceSM)p).getExpiryTime();
			flags.registered = ((ReplaceSM)p).isRegistered();
			flags.default_msg = ((ReplaceSM)p).getDefaultMsgId();
			message = ((ReplaceSM)p).getMessageText();
		}
		else if(p instanceof CancelSM)
		{
			serviceType = ((CancelSM)p).getServiceType();
			messageId = ((CancelSM)p).getMessageId();
			sourceAddr = ((CancelSM)p).getSource();
			destinationTable.addElement(((CancelSM)p).getDestination());
			destinationTable.trimToSize();
		}
		else if(p instanceof SubmitSMResp)
			messageId = ((SubmitSMResp)p).getMessageId();
		else if(p instanceof SubmitMultiResp)
			messageId = ((SubmitMultiResp)p).getMessageId();

		// otherwise, just leave all the fields blank!
	}
}

