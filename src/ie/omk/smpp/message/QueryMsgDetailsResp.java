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
package ie.omk.smpp.message;

import java.io.*;
import java.util.*;
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Response to Query message details...gives all details of a specified
  * message
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryMsgDetailsResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Table of destinations the message was routed to */
    Vector				destinationTable;

    /** Construct a new QueryMsgDetailsResp with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public QueryMsgDetailsResp(int seqNo)
    {
	super(ESME_QUERY_LAST_MSGS_RESP, seqNo);
	destinationTable = null;
    }

    /** Read in a QueryMsgDetailsResp from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a QueryMsgDetailsResp packet.
     * @see java.io.InputStream
     */
    public QueryMsgDetailsResp(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	int noOfDests = 0, smLength = 0;
	String delivery, valid, finalD;
	flags = new MsgFlags();
	try {
	    serviceType = readCString(in);
	    source = new SmeAddress(in);
	    noOfDests = readInt(in, 1);

	    destinationTable = new Vector(noOfDests);
	    for(int loop=0; loop<noOfDests; loop++) {
		SmeAddress d = new SmeAddress(in, true);
		destinationTable.addElement(d);
	    }

	    flags.protocol =  readInt(in, 1);
	    flags.priority =  (readInt(in, 1) == 0) ? false : true;

	    delivery = readCString(in);
	    valid = readCString(in);
	    deliveryTime = makeDateFromString(delivery);
	    expiryTime = makeDateFromString(valid);

	    flags.registered = (readInt(in, 1) == 0) ? false : true;
	    flags.data_coding = readInt(in, 1);
	    smLength = readInt(in, 1);

	    message = readString(in, smLength);
	    messageId = Integer.parseInt(readCString(in), 16);

	    finalD = readCString(in);
	    finalDate = makeDateFromString(finalD);

	    messageStatus = readInt(in, 1);
	    errorCode = readInt(in, 1);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "query_msg_details_resp packet");
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
	messageId = r.messageId;
	source = r.source;
    }

    /** Set the source address
     * @param srcTon Source address Type of number
     * @param srcNpi Source address Numbering plan indicator
     * @param srcAddr Source address (Up to 20 characters)
     * @exception ie.omk.smpp.SMPPException If the Source address is invalid
     */
    public void setSource(int srcTon, int srcNpi, String srcAddr)
    {
	super.setSource(new SmeAddress(srcTon, srcNpi, srcAddr));
    }

    /** Set the source address
     * @see SmeAddress
     */
    public void setSource(SmeAddress d)
    {
	super.setSource(d);
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

	return destinationTable.size();
    }

    /** Set the destination address table.
     * @param d The array of SmeAddresses to submit the message to
     * @exception java.lang.NullPointerException if the array is null
     * or 0 length
     */
    public void setDestAddresses(SmeAddress d[])
    {
	int loop=0;

	if(d == null || d.length < 1)
	    throw new NullPointerException("QueryMsgDetailsResp: Destination "
		    + "table cannot be null or empty");

	if(destinationTable == null)
	    destinationTable = new Vector(d.length);

	destinationTable.removeAllElements();
	destinationTable.ensureCapacity(d.length);

	for(loop=0; loop<d.length; loop++) {
	    d[loop].useFlag = true;
	    destinationTable.addElement(d[loop]);
	}
    }

    /** Set the service type */
    public void setServiceType(String s)
    {
	super.setServiceType(s);
    }

    /** Set the GSM Protocol Id */
    public void setProtocolId(int s)
    {
	super.setProtocolId(s);
    }

    /** Set or unset Priority delivery
     * @param b false=Regular delivery, true=Priority Delivery
     */
    public void setPriority(boolean b)
    {
	super.setPriority(b);
    }

    /** Set the delivery time
     * @param d The Date to schedule delivery for (must be in UTC)
     */
    public void setDeliveryTime(Date d)
    {
	super.setDeliveryTime(d);
    }

    /** Set the expiry time for the int message
     * @param d The Date the message should expire at (must be in UTC)
     */
    public void setExpiryTime(Date s)
    {
	super.setExpiryTime(s);
    }

    /** Set the relevant message flags.  The following flags are relevant
     * for this type of packet:
     * Registered delivery, Priority, Replace-if-present, GSM protocol,
     * GSM data encoding and default message Id.
     * @param flags The MsgFlags structure to read from.
     */
    public void setMessageFlags(MsgFlags f)
    {
	super.setMessageFlags(f);
    }

    /** Set registered delivery.
     * @param s 0=Normal delivery, 1=Registered Delivery
     * @exception ie.omk.smpp.SMPPException If invalid value is specified
     */
    public void setRegistered(boolean b)
    {
	super.setRegistered(b);
    }

    /** Set the GSM Data coding to use
     * @param s The Coding scheme to use
     */
    public void setDataCoding(int s)
    {
	super.setDataCoding(s);
    }

    /** Set the text of the message to send
     * @param s The text of the message.  (Up to 160 characters may be sent)
     * @exception ie.omk.smpp.SMPPException If the mesasge is too long
     */
    public void setMessageText(String s)
    {
	super.setMessageText(s);
    }

    /** Set the Id of the short message.
     * @param id The message Id as a hex integer
     * @exception ie.omk.smpp.SMPPException If the id is invalid
     */
    public void setMessageId(int id)
    {
	super.setMessageId(id);
    }

    /** Set the date this message reached it's final state.
     * @param d The Date representing the date and time the message reached
     * a final state.
     * @exception ie.omk.smpp.SMPPException If the date is invalid.
     */
    public void setFinalDate(Date d)
    {
	super.setFinalDate(d);
    }

    /** Set the current status of the message.
     * @param status The status of the message
     */
    public void setMessageStatus(int status)
    {
	super.setMessageStatus(status);
    }

    /** Set the error code for this message.
     * @param err The error code of this message
     */
    public void setErrorCode(int err)
    {
	super.setErrorCode(err);
    }

    /** Get the service type */
    public String getServiceType()
    {
	return super.getServiceType();
    }

    /** Get the source address */
    public SmeAddress getSource()
    {
	return super.getSource();
    }

    /** Get the number of destination addresses in the destination table */
    public int getNoOfDests()
    {
	return  destinationTable.size();
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

	return sd;
    }

    /** Get the flags structure of this message */
    public MsgFlags getMessageFlags()
    {
	return super.getMessageFlags();
    }

    /** Get the scheduled delivery time */
    public Date getDeliveryTime()
    {
	return super.getDeliveryTime();
    }

    /** Get the expiry time */
    public Date getExpiryTime()
    {
	return super.getExpiryTime();
    }

    /** Get the text of the int message */
    public String getMessageText()
    {
	return super.getMessageText();
    }

    /** Get the GSM Protocol Id */
    public int getProtocolId()
    {
	return super.getProtocolId();
    }

    /** Check is this message priority delivery */
    public boolean isPriority()
    {
	return super.isPriority();
    }

    /** Check is this message registered delivery */
    public boolean isRegistered()
    {
	return super.isRegistered();
    }

    /** Get the GSM data coding scheme used */
    public int getDataCoding()
    {
	return super.getDataCoding();
    }

    /** Get the length of the text of the message */
    public int getMessageLen()
    {
	return super.getMessageLen();
    }

    /** Get the Message Id */
    public int getMessageId()
    {
	return super.getMessageId();
    }

    /** Get the date the message reached it's final state */
    public Date getFinalDate()
    {
	return super.getFinalDate();
    }

    /** Get the status of the message */
    public int getMessageStatus()
    {
	return super.getMessageStatus();
    }

    /** Get the error code of this messages state*/
    public int getErrorCode()
    {
	return super.getErrorCode();
    }


    /** Get the size in bytes of this packet */
    public int size()
    {
	String id = Integer.toHexString(getMessageId());

	int size = (super.size() + 13
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((deliveryTime != null) ?
		    makeDateString(deliveryTime).length() : 0)
		+ ((expiryTime != null) ?
		    makeDateString(expiryTime).length() : 0)
		+ ((message != null) ? message.length() : 0)
		+ ((id != null) ? id.length() : 0)
		+ ((finalDate != null) ? 
		    makeDateString(finalDate).length() : 0));

	Enumeration e = destinationTable.elements();
	if(e != null && e.hasMoreElements()) {
	    while(e.hasMoreElements()) {
		SmeAddress d = (SmeAddress) e.nextElement();
		size += d.size();
	    }
	} else {
	    size += 1;
	}

	return size;
    }

    /** Write a byte representation of this packet to an OutputStream
     * @param out The OutputStream to write to
     * @exception ie.omk.smpp.SMPPException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public void writeTo(OutputStream out)
    {
	int noOfDests = 0, smLength = 0;
	if(destinationTable != null)
	    noOfDests = destinationTable.size();
	if(message != null)
	    smLength = message.length();

	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    super.writeTo(b);

	    writeCString(serviceType, b);
	    if(source != null) {
		source.writeTo(b);
	    } else {
		writeInt(0, 2, b);
		writeCString(null, b);
	    }
	    writeInt(noOfDests, 1, b);

	    Enumeration e = destinationTable.elements();
	    if(e != null && e.hasMoreElements()) {
		while(e.hasMoreElements()) {
		    SmeAddress d = (SmeAddress) e.nextElement();
		    d.writeTo(b);
		}
	    } else {
		b.write(0);
	    }

	    writeInt(flags.protocol, 1, b);
	    writeInt(flags.priority ? 1 : 0, 1, b);
	    writeCString(makeDateString(deliveryTime), b);
	    writeCString(makeDateString(expiryTime), b);
	    writeInt(flags.registered ? 1 : 0, 1, b);
	    writeInt(flags.data_coding, 1, b);
	    writeInt(smLength, 1, b);
	    writeString(message, smLength, b);
	    writeCString(Integer.toHexString(getMessageId()), b);
	    writeCString(makeDateString(finalDate), b);
	    writeInt(messageStatus, 1, b);
	    writeInt(errorCode, 1, b);

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing bind_receiver packet to "
		    + "output stream");
	}
    }

    public String toString()
    {
	return new String("query_msg_details_resp");
    }
}
