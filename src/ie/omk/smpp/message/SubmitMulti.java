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

/** Submit a message to multiple destinations.
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitMulti
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Table of destinations */
    private Vector destinationTable;

    /** Construct a new SubmitMulti with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public SubmitMulti(int seqNum)
    {
	super(ESME_SUB_MULTI, seqNum);
	destinationTable = null;
    }

    /** Read in a SubmitMulti from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public SubmitMulti(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if(commandStatus != 0)
	    return;

	int noOfDests = 0, smLength = 0;
	String delivery, valid;
	flags = new MsgFlags();
	// First the service type
	serviceType = SMPPIO.readCString(in);

	source = new SmeAddress(in, true);

	// Read in the number of destination structures to follow:
	noOfDests = SMPPIO.readInt(in, 1);			

	// Now read in noOfDests number of destination structs
	destinationTable = new Vector(noOfDests);
	for(int loop=0; loop<noOfDests; loop++) {
	    SmeAddress d = new SmeAddress(in, true);
	    destinationTable.addElement(d);
	}

	// ESM class, protocol Id, priorityFlag...
	flags.esm_class =  SMPPIO.readInt(in, 1);
	flags.protocol =  SMPPIO.readInt(in, 1);
	flags.priority = (SMPPIO.readInt(in, 1) == 0) ? false : true;

	delivery = SMPPIO.readCString(in);
	valid = SMPPIO.readCString(in);
	deliveryTime = new SMPPDate(delivery);
	expiryTime = new SMPPDate(valid);

	// Registered delivery, replace if present, data coding, default msg
	// and message length
	flags.registered = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	flags.replace_if_present = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	flags.data_coding = SMPPIO.readInt(in, 1);
	flags.default_msg = SMPPIO.readInt(in, 1);
	smLength = SMPPIO.readInt(in, 1);

	message = SMPPIO.readString(in, smLength);
    }


    /** Add a destination address to the destination table.
      * @param d Destination address representing Sme address or distribution
      * list
      * @return The current number of destination addresses
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
      * or 0 length
      */
    public void setDestAddresses(SmeAddress d[])
    {
	int loop=0;

	if(d == null || d.length < 1)
	    throw new NullPointerException("SubmitMulti: Destination table "
		    + "cannot be null or empty");

	if(destinationTable == null)
	    destinationTable = new Vector(d.length);

	destinationTable.removeAllElements();
	destinationTable.ensureCapacity(d.length);

	for(loop=0; loop<d.length; loop++) {
	    // Gotta make sure the SmeAddress uses the dest_flag
	    d[loop].useFlag = true;
	    destinationTable.addElement(d[loop]);
	}
    }

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

    /** Get an array of the SmeAddress(es) in the destination table.
      * @return Array of SmeAddresses in the destination table
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
	int size = (getHeaderLen() + 12
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((deliveryTime != null) ?
		    deliveryTime.toString().length() : 0)
		+ ((expiryTime != null) ?
		    expiryTime.toString().length() : 0)
		+ ((message != null) ? message.length() : 0));

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
	    SMPPIO.writeInt(0, 2, out);
	    SMPPIO.writeCString(null, out);
	}
	SMPPIO.writeInt(noOfDests, 1, out);

	Enumeration e = destinationTable.elements();
	if(e != null && e.hasMoreElements()) {
	    while(e.hasMoreElements()) {
		SmeAddress d = (SmeAddress) e.nextElement();
		d.writeTo(out);
	    }
	    e = null;
	} else {
	    out.write(0);
	}

	String dt = (deliveryTime == null) ? "" : deliveryTime.toString();
	String et = (expiryTime == null) ? "" : expiryTime.toString();

	SMPPIO.writeInt(flags.esm_class, 1, out);
	SMPPIO.writeInt(flags.protocol, 1, out);
	SMPPIO.writeInt(flags.priority ? 1 : 0, 1, out);
	SMPPIO.writeCString(dt, out);
	SMPPIO.writeCString(et, out);
	SMPPIO.writeInt(flags.registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(flags.replace_if_present ? 1 : 0, 1, out);
	SMPPIO.writeInt(flags.data_coding, 1, out);
	SMPPIO.writeInt(flags.default_msg, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	SMPPIO.writeString(message, smLength, out);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("submit_multi");
    }
}
