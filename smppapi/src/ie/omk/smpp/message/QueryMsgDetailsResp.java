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
package ie.omk.smpp.message;

import java.io.*;
import java.util.*;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.debug.Debug;

/** Response to Query message details.
  * Gives all details of a specified message at the SMSC.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
	serviceType<br>
	source<br>
	protocolID<br>
	priority<br>
	deliveryTime<br>
	expiryTime<br>
	registered<br>
	dataCoding<br>
	message<br>
	messageId<br>
	finalDate<br>
	messageStatus<br>
	errorCode<br>
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryMsgDetailsResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Table of destinations the message was routed to */
    private Vector destinationTable = new Vector();

    /** Construct a new QueryMsgDetailsResp.
      */
    public QueryMsgDetailsResp()
    {
	super(ESME_QUERY_MSG_DETAILS_RESP);
    }

    /** Construct a new QueryMsgDetailsResp with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public QueryMsgDetailsResp(int seqNum)
    {
	super(ESME_QUERY_MSG_DETAILS_RESP, seqNum);
    }

    /** Read in a QueryMsgDetailsResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public QueryMsgDetailsResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.ESME_QUERY_MSG_DETAILS_RESP)
	    throw new BadCommandIDException(
		    SMPPPacket.ESME_QUERY_MSG_DETAILS_RESP, getCommandId());

	if (getCommandStatus() != 0)
	    return;

	int noOfDests = 0, smLength = 0;
	String delivery, valid, finalD;

	serviceType = SMPPIO.readCString(in);
	source = new SmeAddress(in);
	noOfDests = SMPPIO.readInt(in, 1);

	for(int loop=0; loop<noOfDests; loop++) {
	    SmeAddress d = new SmeAddress(in, true);
	    destinationTable.addElement(d);
	}

	protocolID =  SMPPIO.readInt(in, 1);
	priority =  SMPPIO.readInt(in, 1);

	delivery = SMPPIO.readCString(in);
	valid = SMPPIO.readCString(in);
	if (delivery != null)
	    deliveryTime = new SMPPDate(delivery);
	if (valid != null)
	    expiryTime = new SMPPDate(valid);

	registered = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	dataCoding = SMPPIO.readInt(in, 1);
	smLength = SMPPIO.readInt(in, 1);

	if (smLength > 0) {
	    message = new byte[smLength];
	    for (int i = 0; i < smLength; )
		i += in.read(message, i, (smLength - i));
	}
	messageId = SMPPIO.readCString(in);

	finalD = SMPPIO.readCString(in);
	if (finalD != null)
	    finalDate = new SMPPDate(finalD);

	messageStatus = SMPPIO.readInt(in, 1);
	errorCode = SMPPIO.readInt(in, 1);
    }

    /** Create a new QueryMsgDetailsResp packet in response to a BindReceiver.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public QueryMsgDetailsResp(QueryMsgDetails r)
    {
	super(r);

	// These are the only fields that can be got from the request packet
	messageId = r.getMessageId();
	source = r.getSource();
    }

    /** Add a destination address to the destination table
      * @param d Destination address structure to add
      * @return Number of destinations currently in table (including new one)
      * @see SmeAddress
      */
    public int addDestination(SmeAddress d)
    {
	synchronized (destinationTable) {
	    if(d != null) {
		d.useFlag = true;
		destinationTable.addElement(d);
	    }

	    return (destinationTable.size());
	}
    }

    /** Set the destination address table.
      * @param d The array of SmeAddresses to submit the message to
      * @exception java.lang.NullPointerException if the array is null
      */
    public void setDestAddresses(SmeAddress d[])
    {
	int loop=0;

	if(d == null)
	    throw new NullPointerException("QueryMsgDetailsResp: Destination "
		    + "table cannot be null or empty");

	synchronized (destinationTable) {
	    destinationTable.removeAllElements();
	    destinationTable.ensureCapacity(d.length);

	    for(loop = 0; loop < d.length; loop++) {
		d[loop].useFlag = true;
		destinationTable.addElement(d[loop]);
	    }
	}
    }

    /** Get the current number of destination addresses.
      * @deprecated Use getNumDests.
      */
    public int getNoOfDests()
    {
	return (destinationTable.size());
    }

    /** Get the current number of destination addresses.
      */
    public int getNumDests()
    {
	return (destinationTable.size());
    }

    /** Get an array of SmeAddresse representing the destination list.
      * @return An array of the destination addresses the message was
      * submitted to.
      */
    public SmeAddress[] getDestAddresses()
    {
	SmeAddress sd[];
	int loop = 0;

	synchronized (destinationTable) {
	    if(destinationTable.size() == 0)
		return null;

	    sd = new SmeAddress[destinationTable.size()];
	    Iterator i = destinationTable.iterator();
	    while (i.hasNext())
		sd[loop++] = (SmeAddress)i.next();
	}

	return (sd);
    }

    /** Get a handle to the destination table.
      * @return a java.util.Collection containing SmeAddress_e objects.
      */
    public Collection getDestinationTable()
    {
	// XXX: should this return a deep-clone?
	return ((Collection)this.destinationTable);
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getCommandLen()
    {
	int size = (getHeaderLen()
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((deliveryTime != null) ?
		    deliveryTime.toString().length() : 0)
		+ ((expiryTime != null) ?
		    expiryTime.toString().length() : 0)
		+ ((message != null) ? message.length : 0)
		+ ((messageId != null) ? messageId.length() : 0)
		+ ((finalDate != null) ? 
		    finalDate.toString().length() : 0));

	synchronized (destinationTable) {
	    Iterator i = destinationTable.iterator();
	    while (i.hasNext())
		size += ((SmeAddress)i.next()).size();
	}

	// 8 1-byte integers, 5 c-strings
	return (size + 8 + 5);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	int smLength = 0;
	if(message != null)
	    smLength = message.length;

	synchronized (destinationTable) {
	    int noOfDests = destinationTable.size();
	    SMPPIO.writeCString(serviceType, out);
	    if(source != null) {
		source.writeTo(out);
	    } else {
		// Write ton=0(null), npi=0(null), address=\0(nul)
		new SmeAddress(GSMConstants.GSM_TON_UNKNOWN,
			GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	    }
	    SMPPIO.writeInt(noOfDests, 1, out);

	    Iterator i = destinationTable.iterator();
	    while(i.hasNext())
		((SmeAddress)i.next()).writeTo(out);
	}

	String dt = (deliveryTime == null) ? null : deliveryTime.toString();
	String et = (expiryTime == null) ? null : expiryTime.toString();
	String fd = (finalDate == null) ? null : finalDate.toString();

	SMPPIO.writeInt(protocolID, 1, out);
	SMPPIO.writeInt(priority, 1, out);
	SMPPIO.writeCString(dt, out);
	SMPPIO.writeCString(et, out);
	SMPPIO.writeInt(registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(dataCoding, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	if (message != null)
	    out.write(message);
	SMPPIO.writeCString(getMessageId(), out);
	SMPPIO.writeCString(fd, out);
	SMPPIO.writeInt(messageStatus, 1, out);
	SMPPIO.writeInt(errorCode, 1, out);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("query_msg_details_resp");
    }
}
