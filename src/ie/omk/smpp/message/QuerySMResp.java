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
import ie.omk.smpp.util.SMPPDate;
import ie.omk.debug.Debug;

/** Response packet to a query message request
  * @author Oran Kelly
  * @version 1.0
  */
public class QuerySMResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Construct a new QuerySMResp with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public QuerySMResp(int seqNum)
    {
	super(ESME_QUERY_SM_RESP, seqNum);
    }

    /** Read in a QuerySMResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a QuerySMResp packet.
      * @see java.io.InputStream
      */
    public QuerySMResp(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    messageId = Integer.parseInt(SMPPIO.readCString(in), 16);
	    finalDate = new SMPPDate(SMPPIO.readCString(in));
	    messageStatus =  SMPPIO.readInt(in, 1);
	    errorCode =  SMPPIO.readInt(in, 1);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "query_sresp packet.");
	} catch(NumberFormatException nx) {
	    throw new SMPPException("Error reading message Id from the input "
		    + "stream.");
	}
    }

    /** Create a new QuerySMResp packet in response to a BindReceiver.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public QuerySMResp(QuerySM r)
    {
	super(r);

	messageId = r.messageId;
	finalDate = null;
	messageStatus = 0;
	errorCode = 0;
    }

    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	String id = Integer.toHexString(getMessageId());

	return(getHeaderLen() + 4
		+ ((id != null) ? id.length() : 0)
		+ ((finalDate != null) ?
		    finalDate.toString().length() : 0));
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
	SMPPIO.writeCString(finalDate.toString(), out);
	SMPPIO.writeInt(messageStatus, 1, out);
	SMPPIO.writeInt(errorCode, 1, out);
    }

    public String toString()
    {
	return new String("query_sm_resp");
    }
}
