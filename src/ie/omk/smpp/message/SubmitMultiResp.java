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

/** Response to submit_multi message (submit message to multiple destinations)
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitMultiResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** No of destinations unsuccessfully submitted to */
    int				unSuccessfulCount;
    /** Table of unsuccessful destinations */
    Vector			unSuccessfulTable;

    /** Construct a new Unbind with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public SubmitMultiResp(int seqNo)
    {
	super(ESME_SUB_MULTI_RESP, seqNo);

	unSuccessfulCount = 0;
	unSuccessfulTable = null;
    }

    /** Create a new SubmitMultiResp packet in response to a BindReceiver.
     * This constructor will set the sequence number to it's expected value.
     * @param r The Request packet the response is to
     */
    public SubmitMultiResp(SubmitMulti r)
    {
	super(r);

	unSuccessfulCount = 0;
	unSuccessfulTable = null;
    }

    /** Read in a SubmitMultiResp from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a SubmitMultiResp packet.
     * @see java.io.InputStream
     */
    public SubmitMultiResp(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	try {
	    messageId = Integer.parseInt(readCString(in), 16);
	    unSuccessfulCount =  readInt(in, 1);

	    if(unSuccessfulCount < 1)
		return;

	    unSuccessfulTable = new Vector(unSuccessfulCount);
	    for(int loop=0; loop<unSuccessfulCount; loop++) {
		SmeAddress_e a = new SmeAddress_e(in);
		unSuccessfulTable.addElement(a);
	    }
	} catch(IOException x) {
	    throw new SMPPException("Input stream does not contain a "
		    + "submit_multi_resp packet");
	} catch(NumberFormatException nx) {
	    throw new SMPPException("Error reading message Id from the "
		    + "input stream.");
	}
    }

    /** Set the message Id
     * @param messageId The new message Id (Up to 8 characters)
     * @exception ie.omk.smpp.SMPPException If the Id is invalid
     */
    public void setMessageId(int id)
    {
	super.setMessageId(id);
    }

    /** Get the message Id */
    public int getMessageId()
    {
	return super.getMessageId();
    }

    /** Get the number of unsuccessful destinations */
    public int getUnsuccessfulCount()
    {
	return unSuccessfulCount;
    }

    /** Add a destination address to the table of unsuccessful destinations
     * @param a SmeAddress_e structure representing the failed destination
     * @return The current count of unsuccessful destinations (including the new one)
     */
    public int addSmeToTable(SmeAddress_e a)
    {
	if(unSuccessfulTable == null) {
	    unSuccessfulTable = new Vector(5);
	    unSuccessfulCount = 0;
	}

	if(a != null) {
	    a.useFlag = false;
	    unSuccessfulTable.addElement(a);
	    unSuccessfulCount =  unSuccessfulTable.size();
	}

	return unSuccessfulTable.size();
    }

    /** Set the destination address table.
     * @param d The array of SmeAddresses the message was unsuccessfully
     * submitted to.
     * @exception java.lang.NullPointerException if the array is null
     * or 0 length
     */
    public void setDestAddresses(SmeAddress_e d[])
    {
	int loop=0;

	if(d == null || d.length < 1)
	    throw new NullPointerException("SubmitMultiResp: Destination "
		    + "table cannot be null or empty");

	if(unSuccessfulTable == null)
	    unSuccessfulTable = new Vector(d.length);

	unSuccessfulTable.removeAllElements();
	unSuccessfulTable.ensureCapacity(d.length);

	for(loop=0; loop<d.length; loop++) {
	    d[loop].useFlag = false;
	    unSuccessfulTable.addElement(d[loop]);
	}
    }

    /** Get the SmeAddress_e(s) that are in the 'unsuccessful' table.
     * @return Array of SmeAddress_e(s) that were unsuccessfully submitted
     * to, or null if there are none
     */
    public SmeAddress_e[] getDestAddresses()
    {
	SmeAddress_e sd[];
	int loop, size;

	if(unSuccessfulTable == null || unSuccessfulTable.size() == 0)
	    return null;

	size = unSuccessfulTable.size();
	sd = new SmeAddress_e[size];
	for(loop=0; loop<size; loop++)
	    sd[loop] = (SmeAddress_e) unSuccessfulTable.elementAt(loop);

	return sd;
    }


    /** Get the size in bytes of this packet */
    public int size()
    {
	String id = Integer.toHexString(getMessageId());
	SmeAddress_e sd[];
	int loop;

	int size = super.size() + 2
	    + ((id != null) ? id.length() : 0);

	sd = getDestAddresses();
	if(sd != null) {
	    for(loop=0; loop<sd.length; loop++)
		size += sd[loop].size();
	} else {
	    // For the one null byte that will be included
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
	SmeAddress_e sd[];
	int loop;
	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    super.writeTo(b);

	    writeCString(Integer.toHexString(getMessageId()), b);
	    writeInt((int)unSuccessfulCount, 1, b);

	    sd = getDestAddresses();
	    if(sd != null) {
		for(loop=0; loop<sd.length; loop++)
		    sd[loop].writeTo(b);
	    } else {
		b.write(0);
	    }

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing bind_receiver packet to "
		    + "output stream");
	}
    }

    public String toString()
    {
	return new String("submit_multi_resp");
    }
}
