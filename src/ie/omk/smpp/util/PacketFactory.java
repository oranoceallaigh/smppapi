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

    /** Create a new instance of the appropriate sub class of SMPPPacket. This
     * class creates a ByteArrayInputStream around <code>b</code> and uses the
     * <code>InputStream</code> constructors of the classes. The plan is to add
     * byte array constructors to the packets and use that instead (although
     * better suggestions are welcomed)..
     */
    public static SMPPPacket newPacket(byte[] b)
    {
	SMPPPacket response = null;

	try {
	    int cmdId = SMPPIO.bytesToInt(b, 4, 4);
	    ByteArrayInputStream bin = new ByteArrayInputStream(b);
	    switch(cmdId) {
		case SMPPPacket.ESME_NACK:
		    Debug.d(SMPPPacket.class, "readPacket", " GenericNack", 3);
		    response = new GenericNack(bin);
		    break;

		case SMPPPacket.ESME_BNDRCV:
		    Debug.d(SMPPPacket.class, "readPacket", "BindReceiver", 3);
		    response = new BindReceiver(bin);
		    break;

		case SMPPPacket.ESME_BNDRCV_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "BindReceiverResp", 3);
		    response = new BindReceiverResp(bin);
		    break;

		case SMPPPacket.ESME_BNDTRN:
		    Debug.d(SMPPPacket.class, "readPacket", "BindTransmitter", 3);
		    response = new BindTransmitter(bin);
		    break;

		case SMPPPacket.ESME_BNDTRN_RESP:
		    Debug.d(SMPPPacket.class, "readPacket",
			    "BindTransmitterResp", 3);
		    response = new BindTransmitterResp(bin);
		    break;

		case SMPPPacket.ESME_UBD:
		    Debug.d(SMPPPacket.class, "readPacket", "Unbind", 3);
		    response = new Unbind(bin);
		    break;

		case SMPPPacket.ESME_UBD_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "UnbindResp", 3);
		    response = new UnbindResp(bin);
		    break;

		case SMPPPacket.ESME_SUB_SM:
		    Debug.d(SMPPPacket.class, "readPacket", "SubmitSM", 3);
		    response = new SubmitSM(bin);
		    break;

		case SMPPPacket.ESME_SUB_SM_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "SubmitSMResp", 3);
		    response = new SubmitSMResp(bin);
		    break;

		case SMPPPacket.ESME_SUB_MULTI:
		    Debug.d(SMPPPacket.class, "readPacket", "SubmitMulti", 3);
		    response = new SubmitMulti(bin);
		    break;

		case SMPPPacket.ESME_SUB_MULTI_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "SubmitMultiResp", 3);
		    response = new SubmitMultiResp(bin);
		    break;

		case SMPPPacket.SMSC_DELIVER_SM:
		    Debug.d(SMPPPacket.class, "readPacket", "DeliverSm", 3);
		    response = new DeliverSM(bin);
		    break;

		case SMPPPacket.SMSC_DELIVER_SM_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "DeliverSMResp", 3);
		    response = new DeliverSMResp(bin);
		    break;

		case SMPPPacket.ESME_QUERY_SM:
		    Debug.d(SMPPPacket.class, "readPacket", "QuerySM", 3);
		    response = new QuerySM(bin);
		    break;

		case SMPPPacket.ESME_QUERY_SM_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "QuerySMResp", 3);
		    response = new QuerySMResp(bin);
		    break;

		case SMPPPacket.ESME_QUERY_LAST_MSGS:
		    Debug.d(SMPPPacket.class, "readPacket", "QueryLastMsgs", 3);
		    response = new QueryLastMsgs(bin);
		    break;

		case SMPPPacket.ESME_QUERY_LAST_MSGS_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "QueryLastMsgsResp", 3);
		    response = new QueryLastMsgsResp(bin);
		    break;

		case SMPPPacket.ESME_QUERY_MSG_DETAILS:
		    Debug.d(SMPPPacket.class, "readPacket", "QueryMsgDetails", 3);
		    response = new QueryMsgDetails(bin);
		    break;

		case SMPPPacket.ESME_QUERY_MSG_DETAILS_RESP:
		    Debug.d(SMPPPacket.class, "readPacket",
			    "QueryMsgDetailsResp", 3);
		    response = new QueryMsgDetailsResp(bin);
		    break;

		case SMPPPacket.ESME_CANCEL_SM:
		    Debug.d(SMPPPacket.class, "readPacket", "CancelSM", 3);
		    response = new CancelSM(bin);
		    break;

		case SMPPPacket.ESME_CANCEL_SM_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "CancelSMResp", 3);
		    response = new CancelSMResp(bin);
		    break;

		case SMPPPacket.ESME_REPLACE_SM:
		    Debug.d(SMPPPacket.class, "readPacket", "ReplaceSM", 3);
		    response = new ReplaceSM(bin);
		    break;

		case SMPPPacket.ESME_REPLACE_SM_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "ReplaceSMResp", 3);
		    response = new ReplaceSMResp(bin);
		    break;

		case SMPPPacket.ESME_QRYLINK:
		    Debug.d(SMPPPacket.class, "readPacket", "EnquireLink", 3);
		    response = new EnquireLink(bin);
		    break;

		case SMPPPacket.ESME_QRYLINK_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "EnquireLinkResp", 3);
		    response = new EnquireLinkResp(bin);
		    break;

		case SMPPPacket.ESME_PARAM_RETRIEVE:
		    Debug.d(SMPPPacket.class, "readPacket", "ParamRetrieve", 3);
		    response = new ParamRetrieve(bin);
		    break;

		case SMPPPacket.ESME_PARAM_RETRIEVE_RESP:
		    Debug.d(SMPPPacket.class, "readPacket", "ParamRetrieveResp", 3);
		    response = new ParamRetrieveResp(bin);
		    break;

		default:
		    Debug.d(SMPPPacket.class, "readPacket", "Unknown Packet", 3);
		    throw new SMPPException("Unidentified Packet: "
			    + Integer.toHexString(cmdId));
	    }
	} catch (ie.omk.smpp.SMPPException x) {
	    Debug.warn(PacketFactory.class, "newPacket", "SMPPException");
	} catch (IOException x) {
	    Debug.warn(PacketFactory.class, "newPacket", "SMPPException");
	}

	return (response);
    }
}
