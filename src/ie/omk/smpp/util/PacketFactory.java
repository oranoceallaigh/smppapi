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
package ie.omk.smpp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.message.*;
import ie.omk.debug.Debug;

// XXX javadoc
public class PacketFactory
{
    private PacketFactory()
    {
    }

    /** Create a new instance of the appropriate sub class of SMPPPacket.
     */
    public static SMPPPacket newPacket(int id)
    {
	SMPPPacket response = null;

	switch(id) {
	    case SMPPPacket.GENERIC_NACK:
		response = new GenericNack();
		break;

	    case SMPPPacket.BIND_RECEIVER:
		response = new BindReceiver();
		break;

	    case SMPPPacket.BIND_RECEIVER_RESP:
		response = new BindReceiverResp();
		break;

	    case SMPPPacket.BIND_TRANSMITTER:
		response = new BindTransmitter();
		break;

	    case SMPPPacket.BIND_TRANSMITTER_RESP:
		response = new BindTransmitterResp();
		break;

	    case SMPPPacket.BIND_TRANSCEIVER:
		response = new BindTransceiver();
		break;

	    case SMPPPacket.BIND_TRANSCEIVER_RESP:
		response = new BindTransceiverResp();
		break;

	    case SMPPPacket.UNBIND:
		response = new Unbind();
		break;

	    case SMPPPacket.UNBIND_RESP:
		response = new UnbindResp();
		break;

	    case SMPPPacket.SUBMIT_SM:
		response = new SubmitSM();
		break;

	    case SMPPPacket.SUBMIT_SM_RESP:
		response = new SubmitSMResp();
		break;

	    case SMPPPacket.DATA_SM:
		response = new DataSM();
		break;

	    case SMPPPacket.DATA_SM_RESP:
		response = new DataSMResp();
		break;

	    case SMPPPacket.ALERT_NOTIFICATION:
		response = new AlertNotification();
		break;

	    case SMPPPacket.SUBMIT_MULTI:
		response = new SubmitMulti();
		break;

	    case SMPPPacket.SUBMIT_MULTI_RESP:
		response = new SubmitMultiResp();
		break;

	    case SMPPPacket.DELIVER_SM:
		response = new DeliverSM();
		break;

	    case SMPPPacket.DELIVER_SM_RESP:
		response = new DeliverSMResp();
		break;

	    case SMPPPacket.QUERY_SM:
		response = new QuerySM();
		break;

	    case SMPPPacket.QUERY_SM_RESP:
		response = new QuerySMResp();
		break;

	    case SMPPPacket.QUERY_LAST_MSGS:
		response = new QueryLastMsgs();
		break;

	    case SMPPPacket.QUERY_LAST_MSGS_RESP:
		response = new QueryLastMsgsResp();
		break;

	    case SMPPPacket.QUERY_MSG_DETAILS:
		response = new QueryMsgDetails();
		break;

	    case SMPPPacket.QUERY_MSG_DETAILS_RESP:
		response = new QueryMsgDetailsResp();
		break;

	    case SMPPPacket.CANCEL_SM:
		response = new CancelSM();
		break;

	    case SMPPPacket.CANCEL_SM_RESP:
		response = new CancelSMResp();
		break;

	    case SMPPPacket.REPLACE_SM:
		response = new ReplaceSM();
		break;

	    case SMPPPacket.REPLACE_SM_RESP:
		response = new ReplaceSMResp();
		break;

	    case SMPPPacket.ENQUIRE_LINK:
		response = new EnquireLink();
		break;

	    case SMPPPacket.ENQUIRE_LINK_RESP:
		response = new EnquireLinkResp();
		break;

	    case SMPPPacket.PARAM_RETRIEVE:
		response = new ParamRetrieve();
		break;

	    case SMPPPacket.PARAM_RETRIEVE_RESP:
		response = new ParamRetrieveResp();
		break;

	    default:
		response = null;
	}

	if (Debug.getLevel() > 2) {
	    if (response != null)
		Debug.d(PacketFactory.class, "newPacket",
			response.getClass().getName(), 3);
	    else
		Debug.d(PacketFactory.class, "newPacket",
			"Unknown packet " + id, 3);
	}

	return (response);
    }
}
