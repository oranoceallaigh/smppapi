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
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.InvalidMessageIDException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** SMSC response to a QueryLastMsgs request.
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryLastMsgsResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** The table of messages returned */
    private Vector messageTable = new Vector();

    /** Construct a new QueryLastMsgsResp.
      */
    public QueryLastMsgsResp()
    {
	super(ESME_QUERY_LAST_MSGS_RESP);
    }

    /** Construct a new QueryLastMsgsResp with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public QueryLastMsgsResp(int seqNum)
    {
	super(ESME_QUERY_LAST_MSGS_RESP, seqNum);
    }

    /** Read in a QueryLastMsgsResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public QueryLastMsgsResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.ESME_QUERY_LAST_MSGS_RESP)
	    throw new BadCommandIDException(
		    SMPPPacket.ESME_QUERY_LAST_MSGS_RESP, getCommandId());

	if (getCommandStatus() != 0)
	    return;

	int msgCount = 0;
	long id = 0;
	msgCount = SMPPIO.readInt(in, 1);

	for(int loop = 0; loop < msgCount; loop++) {
	    String s = SMPPIO.readCString(in);
	    messageTable.addElement(s);
	    Debug.d(this, "<init>", "Adding " + s + " to destinations", 3);
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

    /** Add a message Id to the response packet.
      * @param id The message Id to add to the packet.
      * @return The current number of message Ids (including the new one).
      * @exception ie.omk.smpp.InvalidMessageIDException if the id is invalid.
      */
    public int addMessageId(String id)
	throws ie.omk.smpp.SMPPException
    {
	if(id.length() > 8)
	    throw new InvalidMessageIDException(id);

	synchronized (messageTable) {
	    messageTable.addElement(id);
	    return (messageTable.size());
	}
    }

    /** Get the number of message Ids. */
    public int getMsgCount()
    {
	return (messageTable.size());
    }

    /** Get a String array of the message Ids.
      * @return A String array of all the message Ids.
      */
    public String[] getMessageIds()
    {
	String[] ids;
	int loop = 0;

	synchronized (messageTable) {
	    if(messageTable.size() == 0)
		return null;

	    ids = new String[messageTable.size()];
	    Iterator i = messageTable.iterator();
	    while (i.hasNext())
		ids[loop++] = (String)i.next();
	}

	return ids;
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getCommandLen()
    {
	String s = null;

	// 1 1-byte integer!
	int size = getHeaderLen() + 1;
	synchronized (messageTable) {
	    Iterator i = messageTable.iterator();
	    while (i.hasNext())
		size += ((String)i.next()).length() + 1;
	}

	return (size);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	String s = null;
	synchronized (messageTable) {
	    int size = messageTable.size();
	    SMPPIO.writeInt(size, 1, out);
	    Iterator i = messageTable.iterator();
	    while (i.hasNext())
		SMPPIO.writeCString((String)i.next(), out);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("query_last_msgs_resp");
    }
}
