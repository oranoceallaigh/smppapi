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
 */
package tests;

import java.io.ByteArrayOutputStream;

import ie.omk.smpp.message.*;

import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SMPPIO;


public abstract class SizeTest implements SMPPTest
{
    protected static final Class[] classList = {
	AlertNotification.class,
	BindReceiver.class,
	BindReceiverResp.class,
	BindTransceiver.class,
	BindTransceiverResp.class,
	BindTransmitter.class,
	BindTransmitterResp.class,
	CancelSM.class,
	CancelSMResp.class,
	DataSM.class,
	DataSMResp.class,
	DeliverSM.class,
	DeliverSMResp.class,
	EnquireLink.class,
	EnquireLinkResp.class,
	GenericNack.class,
	ParamRetrieve.class,
	ParamRetrieveResp.class,
	QueryLastMsgs.class,
	QueryLastMsgsResp.class,
	QueryMsgDetails.class,
	QueryMsgDetailsResp.class,
	QuerySM.class,
	QuerySMResp.class,
	ReplaceSM.class,
	ReplaceSMResp.class,
	SubmitMulti.class,
	SubmitMultiResp.class,
	SubmitSM.class,
	SubmitSMResp.class,
	Unbind.class,
	UnbindResp.class
    };

    protected byte[] serialize(SMPPPacket p)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	ByteArrayOutputStream o = new ByteArrayOutputStream();
	p.writeTo(o);
	return (o.toByteArray());
    }

    protected SMPPPacket deserialize(byte[] b)
    {
	int id = SMPPIO.bytesToInt(b, 4, 4);
	SMPPPacket p = PacketFactory.newPacket(id);
	p.readFrom(b, 0);
	return (p);
    }
}
