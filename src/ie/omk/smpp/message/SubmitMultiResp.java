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
import ie.omk.debug.Debug;

/** Submit to multiple destinations response.
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitMultiResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Table of unsuccessful destinations */
    private Vector unsuccessfulTable;

    /** Construct a new Unbind with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public SubmitMultiResp(int seqNum)
    {
	super(ESME_SUB_MULTI_RESP, seqNum);

	unsuccessfulTable = null;
    }

    /** Create a new SubmitMultiResp packet in response to a BindReceiver.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public SubmitMultiResp(SubmitMulti r)
    {
	super(r);

	unsuccessfulTable = new Vector();
    }

    /** Read in a SubmitMultiResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException If an error occurs writing to the input
      * stream.
      */
    public SubmitMultiResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if(commandStatus != 0)
	    return;

	messageId = SMPPIO.readCString(in);
	int unsuccessfulCount =  SMPPIO.readInt(in, 1);

	if(unsuccessfulCount < 1)
	    return;

	unsuccessfulTable = new Vector(unsuccessfulCount);
	for(int loop=0; loop<unsuccessfulCount; loop++) {
	    SmeAddress_e a = new SmeAddress_e(in);
	    unsuccessfulTable.addElement(a);
	}
    }

    /** Get the number of unsuccessful destinations */
    public int getUnsuccessfulCount()
    {
	return ((unsuccessfulTable != null) ? unsuccessfulTable.size() : 0);
    }

    /** Add a destination address to the table of unsuccessful destinations.
      * @param a SmeAddress_e structure representing the failed destination
      * @return The current count of unsuccessful destinations (including the
      * new one)
      */
    public int addSmeToTable(SmeAddress_e a)
    {
	if(unsuccessfulTable == null) {
	    unsuccessfulTable = new Vector(5);
	}

	if(a != null) {
	    a.useFlag = false;
	    unsuccessfulTable.addElement(a);
	}

	return unsuccessfulTable.size();
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

	if(unsuccessfulTable == null)
	    unsuccessfulTable = new Vector(d.length);

	unsuccessfulTable.removeAllElements();
	unsuccessfulTable.ensureCapacity(d.length);

	for(loop=0; loop<d.length; loop++) {
	    d[loop].useFlag = false;
	    unsuccessfulTable.addElement(d[loop]);
	}
    }

    /** Get the SmeAddress_e(s) that are in the 'unsuccessful' table.
      * @return Array of SmeAddress_e(s) that were unsuccessfully submitted
      * to. If there are none, an array of length 0 is returned.
      */
    public SmeAddress_e[] getDestAddresses()
    {
	SmeAddress_e sd[];
	int loop, size;

	if(unsuccessfulTable == null || unsuccessfulTable.size() == 0)
	    return (new SmeAddress_e[0]);

	size = unsuccessfulTable.size();
	sd = new SmeAddress_e[size];
	for(loop=0; loop<size; loop++)
	    sd[loop] = (SmeAddress_e) unsuccessfulTable.elementAt(loop);

	return sd;
    }


    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	SmeAddress_e sd[];
	int loop;

	int size = getHeaderLen()
	    + ((messageId != null) ? messageId.length() : 0);

	sd = getDestAddresses();
	if(sd.length > 0) {
	    for(loop=0; loop<sd.length; loop++)
		size += sd[loop].size();
	} else {
	    // For the one null byte that will be included
	    size += 1;
	}

	// 1 1-byte integer, 1 c-string
	return (size + 1 + 1);
    }


    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException If an error occurs writing to the output
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SmeAddress_e sd[];
	int loop, size = 0;

	size = (unsuccessfulTable != null) ? unsuccessfulTable.size() : 0;
	SMPPIO.writeCString(getMessageId(), out);
	SMPPIO.writeInt(size, 1, out);

	sd = getDestAddresses();
	if(sd.length > 0) {
	    for(loop=0; loop<sd.length; loop++)
		sd[loop].writeTo(out);
	} else {
	    out.write(0);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("submit_multi_resp");
    }
}
