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
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** Query the last number of messages sent from a certain ESME
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryLastMsgs
    extends ie.omk.smpp.message.SMPPRequest
{
    /** No of messages to look up */
    int			msgCount;

    /** Construct a new QueryLastMsgs with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public QueryLastMsgs(int seqNum)
    {
	super(ESME_QUERY_LAST_MSGS, seqNum);
	msgCount = 0;
    }

    /** Read in a QueryLastMsgs from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a QueryLastMsgs packet.
      * @see java.io.InputStream
      */
    public QueryLastMsgs(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    source = new SmeAddress(in);
	    msgCount = SMPPIO.readInt(in, 1);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "query_last_msgs packet.");
	}
    }

    /** Set the number of messages to look up */
    public void setMsgCount(int s)
    {
	if(s > 0 && s <= 100) {
	    msgCount = s;
	} else {
	    throw new SMPPException("Number of messages to query must be > 0 "
		    + "and <= 100");
	}
    }

    /** Get the count of the number of messages being requested */
    public int getMsgCount()
    {
	return (msgCount);
    }

    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	return (getHeaderLen() + 1
		+ ((source != null) ? source.size() : 3));
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception ie.omk.smpp.SMPPException If an I/O error occurs
      * @see java.io.OutputStream
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if(source != null) {
	    source.writeTo(out);
	} else {
	    SMPPIO.writeInt(0, 3, out);
	}
	SMPPIO.writeInt(msgCount, 1, out);
    }

    public String toString()
    {
	return new String("query_last_msgs");
    }
}
