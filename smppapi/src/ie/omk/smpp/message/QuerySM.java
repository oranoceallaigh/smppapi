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

/** Query the state of a message
  * @author Oran Kelly
  * @version 1.0
  */
public class QuerySM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new QuerySM with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public QuerySM(int seqNum)
    {
	super(ESME_QUERY_SM, seqNum);
    }

    /** Read in a QuerySM from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a QuerySM packet.
      * @see java.io.InputStream
      */
    public QuerySM(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    messageId = Integer.parseInt(SMPPIO.readCString(in), 16);
	    source = new SmeAddress(in);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "query_sm packet.");
	}
    }

    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	String id = Integer.toHexString(getMessageId());

	return (getHeaderLen() + 1
		+ ((id != null) ? id.length() : 0)
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
	SMPPIO.writeCString(Integer.toHexString(getMessageId()), out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    SMPPIO.writeInt(0, 2, out);
	    SMPPIO.writeCString(null, out);
	}
    }

    public String toString()
    {
	return new String("query_sm");
    }
}
