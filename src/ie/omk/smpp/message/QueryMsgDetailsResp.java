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
import java.util.*;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.debug.Debug;

/** Response to Query message details.
  * Gives all details of a specified message at the SMSC.
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryMsgDetailsResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Table of destinations the message was routed to */
    private Vector destinationTable;

    /** Construct a new QueryMsgDetailsResp with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public QueryMsgDetailsResp(int seqNum)
    {
	super(ESME_QUERY_LAST_MSGS_RESP, seqNum);
	destinationTable = null;
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

	if(commandStatus != 0)
	    return;

	int noOfDests = 0, smLength = 0;
	String delivery, valid, finalD;
	flags = new MsgFlags();
	try {
	    serviceType = SMPPIO.readCString(in);
	    source = new SmeAddress(in);
	    noOfDests = SMPPIO.readInt(in, 1);

	    destinationTable = new Vector(noOfDests);
	    for(int loop=0; loop<noOfDests; loop++) {
		SmeAddress d = new SmeAddress(in, true);
		destinationTable.addElement(d);
	    }

	    flags.protocol =  SMPPIO.readInt(in, 1);
	    flags.priority =  (SMPPIO.readInt(in, 1) == 0) ? false : true;

	    delivery = SMPPIO.readCString(in);
	    valid = SMPPIO.readCString(in);
	    deliveryTime = new SMPPDate(delivery);
	    expiryTime = new SMPPDate(valid);

	    flags.registered = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	    flags.data_coding = SMPPIO.readInt(in, 1);
	    smLength = SMPPIO.readInt(in, 1);

	    // XXX shouldn't fail on number format
	    message = SMPPIO.readString(in, smLength);
	    messageId = Integer.parseInt(SMPPIO.readCString(in), 16);

	    finalD = SMPPIO.readCString(in);
	    finalDate = new SMPPDate(finalD);

	    messageStatus = SMPPIO.readInt(in, 1);
	    errorCode = SMPPIO.readInt(in, 1);
	} catch(NumberFormatException nx) {
	    throw new SMPPException("Error reading message Id from the input "
		    + "stream.");
	}
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
	if(destinationTable == null)
	    destinationTable = new Vector(5);

	if(d != null) {
	    d.useFlag = true;
	    destinationTable.addElement(d);
	}

	return (destinationTable.size());
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

	if(destinationTable == null)
	    destinationTable = new Vector(d.length);

	destinationTable.removeAllElements();
	destinationTable.ensureCapacity(d.length);

	for(loop = 0; loop < d.length; loop++) {
	    d[loop].useFlag = true;
	    destinationTable.addElement(d[loop]);
	}
    }

    /** Get the number of destination addresses in the destination table */
    public int getNoOfDests()
    {
	return (destinationTable.size());
    }

    /** Get an array of the SmeAddresses in the Destination table
      * @return An array of the destination addresses the message was
      * submitted to.
      */
    public SmeAddress[] getDestAddresses()
    {
	SmeAddress sd[];
	int loop, size;

	if(destinationTable == null || destinationTable.size() == 0)
	    return null;

	size = destinationTable.size();
	sd = new SmeAddress[size];
	for(loop=0; loop<size; loop++)
	    sd[loop] = (SmeAddress) destinationTable.elementAt(loop);

	return (sd);
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	String id = Integer.toHexString(getMessageId());

	int size = (getHeaderLen()
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((deliveryTime != null) ?
		    deliveryTime.toString().length() : 0)
		+ ((expiryTime != null) ?
		    expiryTime.toString().length() : 0)
		+ ((message != null) ? message.length() : 0)
		+ ((id != null) ? id.length() : 0)
		+ ((finalDate != null) ? 
		    finalDate.toString().length() : 0));

	Enumeration e = destinationTable.elements();
	if(e != null && e.hasMoreElements()) {
	    while(e.hasMoreElements()) {
		SmeAddress d = (SmeAddress) e.nextElement();
		size += d.size();
	    }
	} else {
	    size += 1;
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
	int noOfDests = 0, smLength = 0;
	if(destinationTable != null)
	    noOfDests = destinationTable.size();
	if(message != null)
	    smLength = message.length();

	SMPPIO.writeCString(serviceType, out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    SMPPIO.writeInt(0, 3, out);
	}
	SMPPIO.writeInt(noOfDests, 1, out);

	Enumeration e = destinationTable.elements();
	if(e != null && e.hasMoreElements()) {
	    while(e.hasMoreElements()) {
		SmeAddress d = (SmeAddress) e.nextElement();
		d.writeTo(out);
	    }
	} else {
	    out.write(0);
	}

	String dt = (deliveryTime == null) ? "" : deliveryTime.toString();
	String et = (expiryTime == null) ? "" : expiryTime.toString();
	String fd = (finalDate == null) ? "" : finalDate.toString();

	SMPPIO.writeInt(flags.protocol, 1, out);
	SMPPIO.writeInt(flags.priority ? 1 : 0, 1, out);
	SMPPIO.writeCString(dt, out);
	SMPPIO.writeCString(et, out);
	SMPPIO.writeInt(flags.registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(flags.data_coding, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	SMPPIO.writeString(message, smLength, out);
	SMPPIO.writeCString(Integer.toHexString(getMessageId()), out);
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
