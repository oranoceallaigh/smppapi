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

import ie.omk.smpp.Address;

import ie.omk.smpp.message.*;

import ie.omk.smpp.util.SMPPDate;

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
    public Address		sourceAddr = null;

    /** Table of all destinations this message was successfully delivered to */
    public Vector		destinationTable = new Vector();

    /** Priority of message. */
    public boolean		priority = false;

    /** Registered delivery. */
    public boolean		registered = false;

    /** Replace if present flag. */
    public boolean		replaceIfPresent = false;

    /** ESM class. */
    protected int		esmClass = 0;

    /** GSM protocol ID. */
    protected int		protocolID = 0;

    /** GSM data coding (see GSM 03.38). */
    public int			dataCoding = 0;

    /** Default message number. */
    public int			defaultMsg = 0;

    /** Date and time this message is scheduled to be delivered on */
    public SMPPDate		deliveryTime = null;

    /** Date and time this message will expire on */
    public SMPPDate		expiryTime = null;

    /** Text of the message */
    public byte[]		message = null;


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

	if(p == null)
	    return;

	getAllFields(p);
    }

    public SubmitSM getSubmitSM()
	throws ie.omk.smpp.SMPPException
    {
	SubmitSM p = new SubmitSM();
	fillAllFields(p);
	return (p);
    }
    
    public DeliverSM getDeliverSM()
	throws ie.omk.smpp.SMPPException
    {
	DeliverSM p = new DeliverSM();
	fillAllFields(p);
	return (p);
    }

    public SubmitMulti getSubmitMulti()
	throws ie.omk.smpp.SMPPException
    {
	SubmitMulti p = new SubmitMulti();
	fillAllFields(p);

	DestinationTable t = p.getDestinationTable();
	Iterator i = destinationTable.iterator();
	while (i.hasNext()) {
	    t.add((Address)i.next());
	}
	return (p);
    }

    public QueryMsgDetailsResp getQueryMsgDetailsResp()
	throws ie.omk.smpp.SMPPException
    {
	QueryMsgDetailsResp p = new QueryMsgDetailsResp();
	fillAllFields(p);
	return (p);
    }

    public QuerySMResp getQuerySMResp()
	throws ie.omk.smpp.SMPPException
    {
	QuerySMResp p = new QuerySMResp();
	fillAllFields(p);
	return (p);
    }

    public ReplaceSM getReplaceSM()
	throws ie.omk.smpp.SMPPException
    {
	ReplaceSM p = new ReplaceSM();
	fillAllFields(p);
	return (p);
    }

    public CancelSM getCancelSM()
	throws ie.omk.smpp.SMPPException
    {
	CancelSM p = new CancelSM();
	fillAllFields(p);
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

    private void getAllFields(SMPPPacket p)
    {
	this.messageId = p.getMessageId();
	this.finalDate = p.getFinalDate();
	this.status = p.getMessageStatus();
	this.error = p.getErrorCode();
	this.serviceType = p.getServiceType();
	this.sourceAddr = p.getSource();
	this.deliveryTime = p.getDeliveryTime();
	this.expiryTime = p.getExpiryTime();
	this.message = p.getMessage();

	DestinationTable dt = null;
	switch (p.getCommandId()) {
	case SMPPPacket.SUBMIT_MULTI:
	    dt = ((SubmitMulti)p).getDestinationTable();
	    break;
	case SMPPPacket.QUERY_MSG_DETAILS_RESP:
	    dt = ((QueryMsgDetailsResp)p).getDestinationTable();
	    break;
	}

	if (dt == null) {
	    this.destinationTable.add(p.getDestination());
	} else {
	    Iterator i = dt.iterator();
	    while (i.hasNext())
		this.destinationTable.add(i.next());
	}

	this.replaceIfPresent = p.isReplaceIfPresent();
	this.priority = p.isPriority();
	this.dataCoding = p.getDataCoding();
	this.defaultMsg = p.getDefaultMsgId();
	this.protocolID = p.getProtocolID();
	this.esmClass = p.getEsmClass();
    }

    private void fillAllFields(SMPPPacket p)
	throws ie.omk.smpp.SMPPException
    {
	p.setMessageId(messageId);
	p.setFinalDate(finalDate);
	p.setMessageStatus(status);
	p.setErrorCode(error);
	p.setServiceType(serviceType);
	p.setSource(sourceAddr);
	p.setDestination((Address)destinationTable.get(0));
	p.setDeliveryTime(deliveryTime);
	p.setExpiryTime(expiryTime);
	p.setMessage(message);

	p.setReplaceIfPresent(replaceIfPresent);
	p.setPriority(priority);
	p.setDataCoding(dataCoding);
	p.setDefaultMsg(defaultMsg);
	p.setProtocolID(protocolID);
	p.setEsmClass(esmClass);
    }
}
