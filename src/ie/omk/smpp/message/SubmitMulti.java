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

/** Submit a message to multiple destinations
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitMulti
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Table of destinations */
    Vector				destinationTable;

    /** Construct a new SubmitMulti with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public SubmitMulti(int seqNo)
    {
	super(ESME_SUB_MULTI, seqNo);
	destinationTable = null;
    }

    /** Read in a SubmitMulti from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a SubmitMulti packet.
     * @see java.io.InputStream
     */
    public SubmitMulti(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	int noOfDests = 0, smLength = 0;
	String delivery, valid;
	flags = new MsgFlags();
	try {
	    // First the service type
	    serviceType = readCString(in);

	    source = new SmeAddress(in, true);

	    // Read in the number of destination structures to follow:
	    noOfDests = readInt(in, 1);			

	    // Now read in noOfDests number of destination structs
	    destinationTable = new Vector(noOfDests);
	    for(int loop=0; loop<noOfDests; loop++) {
		SmeAddress d = new SmeAddress(in, true);
		destinationTable.addElement(d);
	    }

	    // ESM class, protocol Id, priorityFlag...
	    flags.esm_class =  readInt(in, 1);
	    flags.protocol =  readInt(in, 1);
	    flags.priority = (readInt(in, 1) == 0) ? false : true;

	    delivery = readCString(in);
	    valid = readCString(in);
	    deliveryTime = makeDateFromString(delivery);
	    expiryTime = makeDateFromString(valid);

	    // Registered delivery, replace if present, data coding, default msg
	    // and message length
	    flags.registered = (readInt(in, 1) == 0) ? false : true;
	    flags.replace_if_present = (readInt(in, 1) == 0) ? false : true;
	    flags.data_coding = readInt(in, 1);
	    flags.default_msg = readInt(in, 1);
	    smLength = readInt(in, 1);

	    message = readString(in, smLength);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "submit_multi packet");
	}
    }


    /** Set the source address
     * @param srcTon Source address Ton
     * @param srcNpi Source address Npi
     * @param srcAddr Source address (Up to 20 characters)
     * @exception ie.omk.smpp.SMPPException If the address is not valid
     */
    public void setSource(int ton, int npi, String addr)
    {
	super.setSource(new SmeAddress(ton, npi, addr));
    }

    /** Set the source address
     * @see SmeAddress
     */
    public void setSource(SmeAddress d)
    {
	super.setSource(d);
    }

    /** Add a destination address to the destination table.
     * @param d Destination address representing Sme address or distribution list
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

    /** Set the service type */
    public void setServiceType(String s)
    {
	super.setServiceType(s);
    }

    /** Set the GSM protocol Id */
    public void setProtocolId(int s)
    {
	super.setProtocolId(s);
    }

    /** Set / unset priority delivery.
     * @param b false=Normal delivery, true=Registered delivery
     * @exception ie.omk.smpp.SMPPException For any other value of s
     */
    public void setPriority(boolean b)
    {
	super.setPriority(b);
    }

    /** Set the delivery time for this message
     * @param d Absolute time for delivery.  Must be specified in UTC
     * @see java.util.Date
     */
    public void setDeliveryTime(Date d)
    {
	super.setDeliveryTime(d);
    }

    /** Set the validity period
     * @param d Absolute time that this message will expire.  Must be specified in UTC.
     * @see java.util.Date
     */
    public void setExpiryTime(Date d)
    {
	super.setExpiryTime(d);
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

    /** Set / unset registered delivery.
     * @param b false=Normal delivery, true=Registered delivery
     * @exception ie.omk.smpp.SMPPException For any other value of s
     */
    public void setRegistered(boolean b)
    {
	super.setRegistered(b);
    }

    /** Set / unset replace if present flag.  This is only valid for
     * a message going to one destination.
     * @param b false=Normal submission, true=Replace if present
     * @exception ie.omk.smpp.SMPPException For any other value of s or if there is more than 1 destination
     */
    public void setReplaceIfPresent(boolean b)
    {
	super.setReplaceIfPresent(b);
    }

    /** Set the GSM Data coding scheme */
    public void setDataCoding(int s)
    {
	super.setDataCoding(s);
    }

    /** Set the default message Id
     * @param s Message Id to use.  Must be between 1 and 100 (0x64)
     * @exception ie.omk.smpp.SMPPException If message Id is out of range
     */
    public void setDefaultMsg(int s)
    {
	super.setDefaultMsg(s);
    }

    /** Set the text of the int message
     * @param s Message text.  Up to 160 characters
     * @exception ie.omk.smpp.SMPPException If Message is too long
     */
    public void setMessageText(String s)
    {
	super.setMessageText(s);
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

    /** Get the number of destinations in the destination table */
    public int getNoOfDests()
    {
	return  destinationTable.size();
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

	return sd;
    }

    /** Get the scheduled delivery time */
    public Date getDeliveryTime()
    {
	return super.getDeliveryTime();
    }

    /** Get the validity period */
    public Date getExpiryTime()
    {
	return super.getExpiryTime();
    }

    /** Get the flags structure of this message */
    public MsgFlags getMessageFlags()
    {
	return super.getMessageFlags();
    }

    /** Get the text of the message */
    public String getMessageText()
    {
	return super.getMessageText();
    }

    /** Get the GSM protocol Id */
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

    /** Check will this message replace an existing message */
    public boolean isReplaceIfPresent()
    {
	return super.isReplaceIfPresent();
    }

    /** Get the GSM Data Coding scheme */
    public int getDataCoding()
    {
	return super.getDataCoding();
    }

    /** Get the default message Id */
    public int getDefaultMsgId()
    {
	return super.getDefaultMsgId();
    }

    /** Get the length in bytes of the int message */
    public int getMessageLen()
    {
	return super.getMessageLen();
    }


    /** Get the size in bytes of this packet */
    public int size()
    {
	int size = (super.size() + 12
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((deliveryTime != null) ?
		    makeDateString(deliveryTime).length() : 0)
		+ ((expiryTime != null) ?
		    makeDateString(expiryTime).length() : 0)
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
		e = null;
	    } else {
		b.write(0);
	    }

	    writeInt(flags.esm_class, 1, b);
	    writeInt(flags.protocol, 1, b);
	    writeInt(flags.priority ? 1 : 0, 1, b);
	    writeCString(makeDateString(deliveryTime), b);
	    writeCString(makeDateString(expiryTime), b);
	    writeInt(flags.registered ? 1 : 0, 1, b);
	    writeInt(flags.replace_if_present ? 1 : 0, 1, b);
	    writeInt(flags.data_coding, 1, b);
	    writeInt(flags.default_msg, 1, b);
	    writeInt(smLength, 1, b);
	    writeString(message, smLength, b);

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing bind_receiver packet to "
		    + "output stream");
	}
    }

    public String toString()
    {
	return new String("submit_multi");
    }
}
