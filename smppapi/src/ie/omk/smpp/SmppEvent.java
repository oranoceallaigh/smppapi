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

import ie.omk.smpp.message.*;
import ie.omk.debug.Debug;

/** SMPP packet receive event.
  * When asynchronous communication is in use, registered observers of an
  * SmppConnection are notified of incoming packets from the SMSC by an
  * SmppEvent. The SmppEvent object contains the source of the event as well as
  * the incoming packet, plus some possible extra information, depending on the
  * incoming packet type.
  */
public class SmppEvent
{
    /** The source object of this event. */
    protected Object source;

    /** Optional extra information. */
    protected Object infoClass;

    /** A handle to the packet that caused the event. */
    protected SMPPPacket packet;


    /** Construct a new Smpp event.
      * @param source The source of this event (not null)
      * @param p The packet that caused the event (not null)
      * @param o Extra details to associate with the event
      * (see getDetails()) (can be null)
      * @see SmppEvent#getDetails
      */
    public SmppEvent(Object source, SMPPPacket p, Object o)
    {
	if(p == null || source == null)
	    throw new NullPointerException();

	this.source = source;
	this.packet = p;
	this.infoClass = o;
    }

    /** Get the source of this event */
    public Object getSource()
    {
	return (source);
    }

    /** Make an Object of appropriate type for inclusion in an SmppEvent.
      * @param p The packet to generate a details Object for.
      * @see SmppEvent#getDetails
      */
    public static Object detailFactory(SMPPPacket p)
    {
	if(p instanceof DeliverSM)
	    return new MessageDetails((DeliverSM)p);
	else if(p instanceof QuerySMResp)
	    return new MessageDetails((QuerySMResp)p);
	else if(p instanceof QueryMsgDetailsResp)
	    return new MessageDetails((QueryMsgDetailsResp)p);
	else if(p instanceof QueryLastMsgsResp)
	    return ((QueryLastMsgsResp)p).getMessageIds();
	else if(p instanceof BindTransmitterResp)
	    return ((BindTransmitterResp)p).getSystemId();
	else if(p instanceof BindReceiverResp)
	    return ((BindReceiverResp)p).getSystemId();
	else if(p instanceof SubmitSMResp)
	    return p.getMessageId();
	else if(p instanceof SubmitMultiResp)
	    return p.getMessageId();
	else if(p instanceof ParamRetrieveResp)
	    return ((ParamRetrieveResp)p).getParamValue();
	else
	    return null;
    }

    /** Additional details.  This can take the form of one of the following:
      *  Object type		Smpp Command
      *  -----------		------------
      * class MessageDetails	QuerySMResp
     *				QueryMsgDetailsResp
     *				DeliverSM
      * java.lang.String	BindTransmitterResp
     *				BindReceiverResp
     *				ParamRetrieveResp
     *				SubmitMultiResp
     *				SubmitSMResp
      * int[]			QueryLastMsgsResp
      * null			GenericNack
     *				UnbindResp
     *				CancelSMResp
     *				ReplaceSMResp
     *				EnquireLink
     *				EnquireLinkResp
     *
      * (All others return null, but will not usually be part of an
      *  Smpp event)
      */
    public Object getDetails()
    {
	return (infoClass);
    }

    /** Get the command Id associated with this event. */
    public int getCommandId()
    {
	return (packet.getCommandId());
    }

    /** Get the status of the command associated with this event */
    public int getStatus()
    {
	return (packet.getCommandStatus());
    }

    /** Get the sequence number of the command associated with this event */ 
    public int getSequenceNum()
    {
	return (packet.getSequenceNum());
    }

    public SMPPPacket getPacket()
    {
	return (packet);
    }
}
