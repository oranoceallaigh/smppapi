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

/** Query Message details...get all information about an existing message
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryMsgDetails
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Length of the message required */
    int				smLength;

    /** Construct a new QueryMsgDetails with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public QueryMsgDetails(int seqNum)
    {
	super(ESME_QUERY_MSG_DETAILS, seqNum);
	smLength = 0;
    }

    /** Read in a QueryMsgDetails from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a QueryMsgDetails packet.
      * @see java.io.InputStream
      */
    public QueryMsgDetails(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    messageId = Integer.parseInt(SMPPIO.readCString(in), 16);
	    source = new SmeAddress(in);
	    smLength =  SMPPIO.readInt(in, 1);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "query_msg_details packet.");
	} catch(NumberFormatException nx) {
	    throw new SMPPException("Error reading message Id from the input stream");
	}
    }

    /** Set the number of bytes of the original message required.
      * @param s The number of bytes required.
      * This will be truncated if goes < 0 or > 160
      */
    public void setSmLength(int s)
    {
	smLength = s;

	if(smLength < 0) smLength = 0;
	if(smLength > 160) smLength = 160;
    }

    /** Get the number of bytes being requested of the original message */
    public int getSmLength()
    {
	return (smLength);
    }


    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	String id = Integer.toHexString(getMessageId());

	return (getHeaderLen() + 2
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
	    SMPPIO.writeInt(0, 3, out);
	}
	SMPPIO.writeInt(smLength, 1, out);
    }

    public String toString()
    {
	return new String("query_msg_details");
    }
}
