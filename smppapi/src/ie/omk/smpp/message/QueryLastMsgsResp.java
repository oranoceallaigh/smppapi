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

/** Response to query last messages request
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryLastMsgsResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** The table of messages returned */
    Vector			messageTable;

    /** Construct a new QueryLastMsgsResp with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public QueryLastMsgsResp(int seqNo)
    {
	super(ESME_QUERY_LAST_MSGS_RESP, seqNo);
	messageTable = null;
    }

    /** Read in a QueryLastMsgsResp from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a QueryLastMsgsResp packet.
     * @see java.io.InputStream
     */
    public QueryLastMsgsResp(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	int noOfMsgs = 0;
	long id = 0;
	try {
	    noOfMsgs = readInt(in, 1);

	    messageTable = new Vector(noOfMsgs);

	    for(int loop=0; loop<noOfMsgs; loop++) {
		String s = readCString(in);
		try {
		    id = Long.parseLong(s, 16);
		    messageTable.addElement(new Integer((int)id));
		    Debug.d(this, "<init>", "Adding "+id+" to destinations", Debug.DBG_3);
		} catch(NumberFormatException nx) {
		    /* Just don't add it to the table! */
		    Debug.d(this, "<init>", "Not added: " + id, Debug.DBG_2);
		}
	    }
	} catch(IOException iox) {
	    Debug.d(this, "<init>", "Input stream does not contain a "
		    + "query_last_msgs_resp", Debug.DBG_1);
	    throw new SMPPException("Input stream does not contain a "
		    + "query_last_msgs_resp packet.");
	}
    }

    /** Create a new QueryLastMsgsResp packet in response to a BindReceiver.
     * This constructor will set the sequence number to it's expected value.
     * @param r The Request packet the response is to
     */
    public QueryLastMsgsResp(QueryLastMsgs r)
    {
	super(r);
    }

    /** Add a message Id to the message table.
     * @param id The message Id to add to the table (0h - ffffffffh)
     * @return The current number of message Ids in the message table
     * @exception ie.omk.smpp.SMPPException If the message Id is invalid
     */
    public int addMessageId(int id)
    {
	if(messageTable == null)
	    messageTable = new Vector();

	String s = Integer.toHexString(id);
	if(s.length() > 8)
	    throw new SMPPException("Message Id must be < 9 chars");

	messageTable.addElement(new Integer(id));
	return messageTable.size();
    }

    /** Get the number of messages in the message table */
    public int getMsgCount()
    {
	return (messageTable != null) ? messageTable.size() : 0;
    }

    /** Get an enumeration to interate through the message table
     * @return An int array of all the message Ids
     */
    public int[] getMessageIds()
    {
	int ids[];
	int loop;

	if(messageTable == null || messageTable.size() == 0)
	    return null;

	ids = new int[messageTable.size()];
	for(loop=0; loop<messageTable.size(); loop++)
	    ids[loop] = ((Integer)messageTable.elementAt(loop)).intValue();

	return ids;
    }

    /** Get the size in bytes of this packet */
    public int size()
    {
	int id;
	String s = null;
	int size = super.size() + 1;
	Enumeration e = messageTable.elements();
	while(e.hasMoreElements()) {
	    id = ((Integer)e.nextElement()).intValue();
	    s = Integer.toHexString(id);
	    size += s.length() + 1;
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
	String s = null;
	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    super.writeTo(b);

	    writeInt(messageTable.size(), 1, b);
	    Enumeration e = messageTable.elements();
	    while(e.hasMoreElements()) {
		s = Integer.toHexString(((Integer)e.nextElement()).intValue());
		writeCString(s, b);
	    }

	    b.writeTo(out);
	} catch(IOException x) {
	    Debug.d(this, "writeTo", "Error writing packet to output",
		    Debug.DBG_1);
	    throw new SMPPException("Error writing query_last_msgs_resp "
		    + "packet to output stream");
	}
    }

    public String toString()
    {
	return new String("query_last_msgs_resp");
    }
}
