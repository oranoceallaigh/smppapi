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
package tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ie.omk.smpp.Address;
import ie.omk.smpp.ErrorAddress;

import ie.omk.smpp.message.*;

import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

/** Test the size of packets with filled in fields. This test does the same
 * steps as the NullSize test class only it fills in every available field in
 * the packet before serializing and deserializing it.
 * @author Oran Kelly
 * @version 1.0
 */
public class FullSize extends SizeTest
{
    protected static SMPPPacket[] pakList = null;

    public FullSize()
    {
	pakList = new SMPPPacket[classList.length];
	initArray();
    }

    public boolean runTest()
    {
	boolean passed = true;

	for (int i = 0; i < pakList.length; i++) {
	    try {
		System.out.print(pakList[i].getClass().getName() + ": ");
		byte[] ba = serialize(pakList[i]);

		if (ba.length == pakList[i].getLength())
		    System.out.print("pass1 ");
		else
		    System.out.print("fail1 ");

		SMPPPacket p2 = deserialize(ba);
		if (ba.length == p2.getLength())
		    System.out.println("pass2");
		else
		    System.out.println("fail2");
	    } catch (Exception x) {
		passed = false;
		System.out.println("exception:\n");
		x.printStackTrace(System.out);
	    }
	} 

	return (passed);
    }

    public static final void main(String[] args)
    {
	boolean result = new FullSize().runTest();
	System.out.println("FullSize test "
		+ (result ? "passed." : "failed."));
	System.exit(result ? 0 : 1);
    }

    private void initArray()
    {
	for (int i = 0; i < pakList.length; i++) {
	    try {
		SMPPPacket p = (SMPPPacket)classList[i].newInstance();
		initialiseMe(p);
		pakList[i] = p;
	    } catch (Exception x) {
		pakList[i] = null;
	    }
	}
    }

    private void initialiseMe(SMPPPacket p)
	throws ie.omk.smpp.SMPPException
    {
	int id = p.getCommandId();

	switch (id) {
	case SMPPPacket.ALERT_NOTIFICATION:
	    p.setSequenceNum(34);
	    p.setSource(new Address(0, 0, "445445445"));
	    p.setDestination(new Address(0, 0, "67676767676767"));
	    break;

	case SMPPPacket.BIND_TRANSMITTER:
	case SMPPPacket.BIND_RECEIVER:
	case SMPPPacket.BIND_TRANSCEIVER:
	    Bind b = (Bind)p;
	    b.setSequenceNum(1);
	    b.setSystemId("sysId");
	    b.setSystemType("sysType");
	    b.setPassword("passwd");
	    b.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534[1-3]"));
	    break;

	case SMPPPacket.BIND_TRANSMITTER_RESP:
	case SMPPPacket.BIND_RECEIVER_RESP:
	case SMPPPacket.BIND_TRANSCEIVER_RESP:
	    p.setSequenceNum(2);
	    BindResp br = (BindResp)p;
	    br.setSystemId("SMSC-ID");
	    break;


	case SMPPPacket.CANCEL_SM:
	    p.setSequenceNum(3);
	    p.setMessageId("deadbeef");
	    p.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534111"));
	    p.setDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534222"));
	    break;

	case SMPPPacket.DELIVER_SM:
	case SMPPPacket.SUBMIT_SM:
	case SMPPPacket.SUBMIT_MULTI:
	    p.setSequenceNum(5);
	    p.setServiceType("svcTp");
	    p.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534111"));
	    if (id == SMPPPacket.SUBMIT_MULTI) {
		SubmitMulti sml = (SubmitMulti)p;
		sml.addDestination(new Address(
			    GSMConstants.GSM_TON_UNKNOWN,
			    GSMConstants.GSM_NPI_UNKNOWN,
			    "991293211"));
		sml.addDestination(new Address(
			    GSMConstants.GSM_TON_UNKNOWN,
			    GSMConstants.GSM_NPI_UNKNOWN,
			    "991293212"));
		sml.addDestination(new Address(
			    GSMConstants.GSM_TON_UNKNOWN,
			    GSMConstants.GSM_NPI_UNKNOWN,
			    "991293213"));
	    } else {
		p.setDestination(new Address(
			    GSMConstants.GSM_TON_UNKNOWN,
			    GSMConstants.GSM_NPI_UNKNOWN,
			    "65534222"));
	    }
	    //p.setProtocolId();
	    p.setPriority(true);
	    p.setDeliveryTime(new SMPPDate());
	    p.setExpiryTime(new SMPPDate());
	    p.setRegistered(true);
	    p.setReplaceIfPresent(true);
	    //p.setDataCoding();
	    p.setMessageText("This is a short message");
	    break;

	case SMPPPacket.DATA_SM:
	    p.setSequenceNum(45);
	    p.setServiceType("svcTp");
	    p.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534111"));
	    p.setDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534222"));
	    p.setRegistered(true);
	    break;

	case SMPPPacket.DATA_SM_RESP:
	    p.setSequenceNum(46);
	    p.setMessageId("deadbeef");
	    break;

	case SMPPPacket.SUBMIT_SM_RESP:
	case SMPPPacket.SUBMIT_MULTI_RESP:
	    p.setSequenceNum(6);
	    p.setMessageId("deadbeef");
	    if (id == SMPPPacket.SUBMIT_MULTI_RESP) {
		SubmitMultiResp smr = (SubmitMultiResp)p;
		smr.add(new ErrorAddress(0, 0, "12345", 65));
		smr.add(new ErrorAddress(0, 0, "12346", 66));
		smr.add(new ErrorAddress(0, 0, "12347", 90));
		smr.add(new ErrorAddress(0, 0, "99999", 999));
	    }
	    break;

	case SMPPPacket.PARAM_RETRIEVE:
	    p.setSequenceNum(7);
	    ((ParamRetrieve)p).setParamName("getParam");
	    break;

	case SMPPPacket.PARAM_RETRIEVE_RESP:
	    p.setSequenceNum(8);
	    ((ParamRetrieveResp)p).setParamValue("paramValue - can be long.");
	    break;

	case SMPPPacket.QUERY_LAST_MSGS:
	    p.setSequenceNum(9);
	    p.setSource(new Address(0, 0, "65534111"));
	    ((QueryLastMsgs)p).setMsgCount(45);
	    break;

	case SMPPPacket.QUERY_LAST_MSGS_RESP:
	    p.setSequenceNum(10);
	    QueryLastMsgsResp q = (QueryLastMsgsResp)p;
	    q.addMessageId("deadbeef");
	    q.addMessageId("cafecafe");
	    q.addMessageId("12345678");
	    q.addMessageId("77777777");
	    q.addMessageId("beefdead");
	    break;

	case SMPPPacket.QUERY_MSG_DETAILS:
	    p.setSequenceNum(11);
	    p.setSource(new Address(0, 0, "65534111"));
	    p.setMessageId("deadbeef");
	    ((QueryMsgDetails)p).setSmLength(160);
	    break;

	case SMPPPacket.QUERY_MSG_DETAILS_RESP:
	    p.setSequenceNum(15);
	    QueryMsgDetailsResp q1 = (QueryMsgDetailsResp)p;
	    q1.setServiceType("svcTp");
	    q1.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534111"));
	    q1.addDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"991293211"));
	    q1.addDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"991293212"));
	    q1.addDestination(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"991293213"));
	    //q1.setProtocolId();
	    q1.setPriority(true);
	    q1.setDeliveryTime(new SMPPDate());
	    q1.setExpiryTime(new SMPPDate());
	    q1.setRegistered(true);
	    q1.setReplaceIfPresent(true);
	    //q1.setDataCoding();
	    q1.setMessageText("This is a short message");
	    q1.setMessageId("deadbeef");
	    q1.setFinalDate(new SMPPDate());
	    q1.setMessageStatus(1);
	    q1.setErrorCode(2);
	    break;

	case SMPPPacket.QUERY_SM:
	    p.setSequenceNum(17);
	    p.setMessageId("deadbeef");
	    p.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534111"));
	    break;

	case SMPPPacket.QUERY_SM_RESP:
	    p.setSequenceNum(20);
	    p.setMessageId("deadbeef");
	    p.setFinalDate(new SMPPDate());
	    p.setMessageStatus(1);
	    p.setErrorCode(4);
	    break;

	case SMPPPacket.REPLACE_SM:
	    p.setSequenceNum(22);
	    p.setMessageId("deadbeef");
	    p.setServiceType("svcTp");
	    p.setSource(new Address(
			GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN,
			"65534111"));
	    p.setDeliveryTime(new SMPPDate());
	    p.setExpiryTime(new SMPPDate());
	    p.setRegistered(true);
	    p.setMessageText("This is a short message");
	    break;

	default:
	    p.setSequenceNum(4);
	    break;
	}
    }
}
