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
package ie.omk.smpp.message;

import java.io.*;
import java.util.*;

import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.SMPPDate;

import ie.omk.debug.Debug;

/** Submit a message to multiple destinations.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
  *   serviceType<br>
  *   source<br>
  *   esmClass<br>
  *   protocolID<br>
  *   priority<br>
  *   deliveryTime<br>
  *   expiryTime<br>
  *   registered<br>
  *   replaceIfPresent<br>
  *   dataCoding<br>
  *   defaultMsg<br>
  *   message<br>
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitMulti
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Table of destinations */
    private DestinationTable destinationTable = new DestinationTable();

    /** Construct a new SubmitMulti.
      */
    public SubmitMulti()
    {
	super(SUBMIT_MULTI);
    }

    /** Construct a new SubmitMulti with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public SubmitMulti(int seqNum)
    {
	super(SUBMIT_MULTI, seqNum);
    }

    /** Read in a SubmitMulti from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public SubmitMulti(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.SUBMIT_MULTI)
	    throw new BadCommandIDException(SMPPPacket.SUBMIT_MULTI,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	int numDests = 0, smLength = 0;
	String delivery, valid;

	// First the service type
	serviceType = SMPPIO.readCString(in);

	source = new Address(in, false);

	// Read in the number of destination structures to follow:
	numDests = SMPPIO.readInt(in, 1);			

	// Now read in numDests number of destination structs
	for(int loop=0; loop<numDests; loop++) {
	    Address d = new Address(in, true);
	    destinationTable.addElement(d);
	}

	// ESM class, protocol Id, priorityFlag...
	esmClass =  SMPPIO.readInt(in, 1);
	protocolID =  SMPPIO.readInt(in, 1);
	priority = SMPPIO.readInt(in, 1);

	delivery = SMPPIO.readCString(in);
	valid = SMPPIO.readCString(in);
	if (delivery != null)
	    deliveryTime = new SMPPDate(delivery);
	if (valid != null)
	    expiryTime = new SMPPDate(valid);

	// Registered delivery, replace if present, data coding, default msg
	// and message length
	registered = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	replaceIfPresent = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	dataCoding = SMPPIO.readInt(in, 1);
	defaultMsg = SMPPIO.readInt(in, 1);
	smLength = SMPPIO.readInt(in, 1);

	if (smLength > 0) {
	    message = new byte[smLength];
	    for (int i = 0; i < smLength; )
		i += in.read(message, i, (smLength - i));
	}
    }*/


    /** Get a handle to the error destination table. Applications may add
     * destination addresses or distribution list names to the destination
     * table.
     */
    public DestinationTable getDestinationTable()
    {
	return (destinationTable);
    }

    /** Add an address to the destination table.
      * @param d The SME destination address
      * @return The current number of destination addresses (including the new
      * one).
      * @see Address
      */
    public int addDestination(Address d)
    {
	synchronized (destinationTable) {
	    destinationTable.add(d);
	    return (destinationTable.size());
	}
    }

    /** Add a distribution list to the destination table.
     * @param d the distribution list name.
     * @return The current number of destination addresses (including the new
     * @exception ie.omk.smpp.StringTooLongException if the distribution list
     * name is too long.
     */
    public int addDestination(String d)
	throws ie.omk.smpp.StringTooLongException
    {
	synchronized (destinationTable) {
	    destinationTable.add(d);
	    return (destinationTable.size());
	}
    }

    /** Set the destination address table.
      * @param d The array of Addresses to submit the message to
      * @exception java.lang.NullPointerException if the array is null
      * or 0 length
      */
    /*public void setDestAddresses(Address d[])
    {
	int loop=0;

	if(d == null || d.length < 1)
	    throw new NullPointerException("SubmitMulti: Destination table "
		    + "cannot be null or empty");

	synchronized (destinationTable) {
	    destinationTable.removeAllElements();
	    destinationTable.ensureCapacity(d.length);

	    for(loop=0; loop<d.length; loop++) {
		// Gotta make sure the Address uses the dest_flag
		d[loop].useFlag = true;
		destinationTable.addElement(d[loop]);
	    }
	}
    }*/

    /** Get the number of destinations in the destination table.
      * @deprecated Use getNumDests()
      */
    public int getNoOfDests()
    {
	return  (destinationTable.size());
    }

    /** Get the number of destinations in the destination table.
      */
    public int getNumDests()
    {
	return (destinationTable.size());
    }

    /** Get an array of the Address(es) in the destination table.
      * @return Array of Addresses in the destination table (never null)
      */
    /*public Address[] getDestAddresses()
    {
	Address sd[];
	int loop = 0;

	synchronized (destinationTable) {
	    if(destinationTable.size() == 0)
		return (new Address[0]);

	    sd = new Address[destinationTable.size()];
	    Iterator i = destinationTable.iterator();
	    while (i.hasNext())
		sd[loop++] = (Address)i.next();
	}

	return (sd);
    }*/

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getBodyLength()
    {
	int size = (((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.getLength() : 3)
		+ ((deliveryTime != null) ?
		    deliveryTime.toString().length() : 0)
		+ ((expiryTime != null) ?
		    expiryTime.toString().length() : 0)
		+ ((message != null) ? message.length : 0));

	size += destinationTable.getLength();

	// 9 1-byte integers, 4 c-strings
	return (size + 9 + 3);
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

	// Get a clone of the table that can't be changed while writing..
	DestinationTable table =
		(DestinationTable)this.destinationTable.clone();

	SMPPIO.writeCString(serviceType, out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    new Address(GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	}

	int numDests = table.size();
	SMPPIO.writeInt(numDests, 1, out);
	table.writeTo(out);

	String dt = (deliveryTime == null) ? null : deliveryTime.toString();
	String et = (expiryTime == null) ? null : expiryTime.toString();

	SMPPIO.writeInt(esmClass, 1, out);
	SMPPIO.writeInt(protocolID, 1, out);
	SMPPIO.writeInt(priority, 1, out);
	SMPPIO.writeCString(dt, out);
	SMPPIO.writeCString(et, out);
	SMPPIO.writeInt(registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(replaceIfPresent ? 1 : 0, 1, out);
	SMPPIO.writeInt(dataCoding, 1, out);
	SMPPIO.writeInt(defaultMsg, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	if (message != null)
	    out.write(message);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	int numDests = 0, smLength = 0;
	String delivery, valid;

	// First the service type
	serviceType = SMPPIO.readCString(body, offset);
	offset += serviceType.length() + 1;

	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	// Read in the number of destination structures to follow:
	numDests = SMPPIO.bytesToInt(body, offset++, 1);

	// Now read in numDests number of destination structs
	DestinationTable dt = new DestinationTable();
	dt.readFrom(body, offset, numDests);
	offset += dt.getLength();
	this.destinationTable = dt;

	// ESM class, protocol Id, priorityFlag...
	esmClass = SMPPIO.bytesToInt(body, offset++, 1);
	protocolID = SMPPIO.bytesToInt(body, offset++, 1);
	priority = SMPPIO.bytesToInt(body, offset++, 1);

	delivery = SMPPIO.readCString(body, offset);
	offset += delivery.length() + 1;
	if (delivery.length() > 0)
	    deliveryTime = new SMPPDate(delivery);

	valid = SMPPIO.readCString(body, offset);
	offset += valid.length() + 1;
	if (valid.length() > 0)
	    expiryTime = new SMPPDate(valid);


	// Registered delivery, replace if present, data coding, default msg
	// and message length
	registered =
		(SMPPIO.bytesToInt(body, offset++, 1) == 0) ? false : true;
	replaceIfPresent =
		(SMPPIO.bytesToInt(body, offset++, 1) == 0) ? false : true;
	dataCoding = SMPPIO.bytesToInt(body, offset++, 1);
	defaultMsg = SMPPIO.bytesToInt(body, offset++, 1);
	smLength = SMPPIO.bytesToInt(body, offset++, 1);

	if (smLength > 0) {
	    message = new byte[smLength];
	    System.arraycopy(body, offset, message, 0, smLength);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("submit_multi");
    }
}
