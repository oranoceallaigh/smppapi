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
package ie.omk.smpp;

import java.io.*;
import java.util.*;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.message.*;
import ie.omk.debug.Debug;

/** Object used as extra information in an SMPP event.
  * Any packets that contain some form of message information will generate a
  * MessageDetails object in the 'extra information' field of an SmppEvent.
  * @see ie.omk.smpp.SmppEvent
  * @see SmppTransmitter#queryMessage
  * @see SmppTransmitter#queryMsgDetails
  * @author Oran Kelly
  * @version 1.0
  */
public class MessageDetails
    implements java.io.Serializable
{
    /** Message Id assigned by the SMSC to the message */
    public String		messageId = null;

    /** Date and time (SMSC local time) that message reached a final state */
    public SMPPDate		finalDate = null;

    /** Current status of the message */
    public int			status =  0;

    /** Error code associated with this message */
    public int			error =  0;

    /** Service type this message was submitted under */
    public String		serviceType = null;

    /** Source address this message came from */
    public SmeAddress		sourceAddr = null;

    /** Table of all destinations this message was successfully delivered to */
    public Vector		destinationTable = new Vector();

    /** Message flags structure.
      * @see MsgFlags
      */
    public MsgFlags		flags = new MsgFlags();

    /** Date and time this message is scheduled to be delivered on */
    public SMPPDate		deliveryTime = null;

    /** Date and time this message will expire on */
    public SMPPDate		expiryTime = null;

    /** Text of the message */
    public String		message = null;


    /** Create a new Details object with all null (or default) values.
      */
    public MessageDetails()
    {
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

	if(p instanceof SubmitSM) {
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
	} else if(p instanceof DeliverSM) {
	    serviceType = p.getServiceType();
	    sourceAddr = p.getSource();
	    destinationTable.addElement(p.getDestination());
	    flags.protocol = p.getProtocolId();
	    flags.data_coding = p.getDataCoding();
	    flags.esm_class = p.getEsmClass();
	    message = p.getMessageText();
	} else if(p instanceof SubmitMulti) {
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
	} else if (p instanceof QueryMsgDetailsResp) {
	    messageId = p.getMessageId();
	    finalDate = p.getFinalDate();
	    status = p.getMessageStatus();
	    error = p.getErrorCode();
	    serviceType = p.getServiceType();
	    sourceAddr = p.getSource();
	    deliveryTime = p.getDeliveryTime();
	    expiryTime = p.getExpiryTime();
	    message = p.getMessageText();

	    flags.priority = p.isPriority();
	    flags.registered = p.isRegistered();
	    flags.data_coding = p.getDataCoding();
	    flags.protocol = p.getProtocolId();
	} else if (p instanceof QuerySMResp) {
	    messageId = p.getMessageId();
	    finalDate = p.getFinalDate();
	    status = p.getMessageStatus();
	    error = p.getErrorCode();
	} else if(p instanceof ReplaceSM) {
	    messageId = ((ReplaceSM)p).getMessageId();
	    sourceAddr = ((ReplaceSM)p).getSource();
	    deliveryTime = ((ReplaceSM)p).getDeliveryTime();
	    expiryTime = ((ReplaceSM)p).getExpiryTime();
	    flags.registered = ((ReplaceSM)p).isRegistered();
	    flags.default_msg = ((ReplaceSM)p).getDefaultMsgId();
	    message = ((ReplaceSM)p).getMessageText();
	} else if(p instanceof CancelSM) {
	    serviceType = ((CancelSM)p).getServiceType();
	    messageId = ((CancelSM)p).getMessageId();
	    sourceAddr = ((CancelSM)p).getSource();
	    destinationTable.addElement(((CancelSM)p).getDestination());
	    destinationTable.trimToSize();
	} else if(p instanceof SubmitSMResp) {
	    messageId = ((SubmitSMResp)p).getMessageId();
	} else if(p instanceof SubmitMultiResp) {
	    messageId = ((SubmitMultiResp)p).getMessageId();
	}
    }

    public SubmitSM getSubmitSM()
	throws ie.omk.smpp.SMPPException
    {
	SubmitSM p = new SubmitSM();
	p.setServiceType(serviceType);
	p.setSource(sourceAddr);
	p.setDestination((SmeAddress)destinationTable.get(0));
	p.setMessageFlags(flags);
	p.setDeliveryTime(deliveryTime);
	p.setExpiryTime(expiryTime);
	p.setMessageText(message);
	return (p);
    }
    
    public DeliverSM getDeliverSM()
	throws ie.omk.smpp.SMPPException
    {
	DeliverSM p = new DeliverSM();
	p.setServiceType(serviceType);
	p.setSource(sourceAddr);
	p.setDestination((SmeAddress)destinationTable.get(0));
	p.setMessageFlags(flags);
	p.setMessageText(message);
	return (p);
    }

    public SubmitMulti getSubmitMulti()
	throws ie.omk.smpp.SMPPException
    {
	SubmitMulti p = new SubmitMulti();
	p.setServiceType(serviceType);
	p.setSource(sourceAddr);

	SmeAddress[] smes = new SmeAddress[destinationTable.size()];
	System.arraycopy(destinationTable.toArray(), 0, smes, 0, smes.length);
	p.setDestAddresses(smes);

	p.setMessageFlags(flags);
	p.setDeliveryTime(deliveryTime);
	p.setExpiryTime(expiryTime);
	p.setMessageText(message);
	return (p);
    }

    public QueryMsgDetailsResp getQueryMsgDetailsResp()
	throws ie.omk.smpp.SMPPException
    {
	QueryMsgDetailsResp p = new QueryMsgDetailsResp();
	p.setMessageId(messageId);
	p.setFinalDate(finalDate);
	p.setMessageStatus(status);
	p.setErrorCode(error);
	p.setServiceType(serviceType);
	p.setSource(sourceAddr);
	p.setDeliveryTime(deliveryTime);
	p.setExpiryTime(expiryTime);
	p.setMessageText(message);
	p.setMessageFlags(flags);
	return (p);
    }

    public QuerySMResp getQuerySMResp()
	throws ie.omk.smpp.SMPPException
    {
	QuerySMResp p = new QuerySMResp();
	p.setMessageId(messageId);
	p.setFinalDate(finalDate);
	p.setMessageStatus(status);
	p.setErrorCode(error);
	return (p);
    }

    public ReplaceSM getReplaceSM()
	throws ie.omk.smpp.SMPPException
    {
	ReplaceSM p = new ReplaceSM();
	p.setMessageId(messageId);
	p.setSource(sourceAddr);
	p.setDeliveryTime(deliveryTime);
	p.setExpiryTime(expiryTime);
	p.setMessageFlags(flags);
	p.setMessageText(message);
	return (p);
    }

    public CancelSM getCancelSM()
	throws ie.omk.smpp.SMPPException
    {
	CancelSM p = new CancelSM();
	p.setServiceType(serviceType);
	p.setMessageId(messageId);
	p.setSource(sourceAddr);
	p.setDestination((SmeAddress)destinationTable.get(0));
	return (p);
    }

    public SubmitSMResp getSubmitSMResp()
	throws ie.omk.smpp.SMPPException
    {
	SubmitSMResp p = new SubmitSMResp();
	p.setMessageId(messageId);
	return (p);
    }

    public SubmitMultiResp getSubmitMultiResp()
	throws ie.omk.smpp.SMPPException
    {
	SubmitMultiResp p = new SubmitMultiResp();
	p.setMessageId(messageId);
	return (p);
    }
}
