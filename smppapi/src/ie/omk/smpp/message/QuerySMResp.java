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
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.debug.Debug;

/** SMSC response to a QuerySM request.
  * Contains the current state of a short message at the SMSC.
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
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public QuerySMResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.ESME_QUERY_SM_RESP)
	    throw new BadCommandIDException(SMPPPacket.ESME_QUERY_SM_RESP,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	messageId = SMPPIO.readCString(in);
	String finald = SMPPIO.readCString(in);
	if (finald != null)
	    finalDate = new SMPPDate(finald);
	messageStatus =  SMPPIO.readInt(in, 1);
	errorCode =  SMPPIO.readInt(in, 1);
    }

    /** Create a new QuerySMResp packet in response to a BindReceiver.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public QuerySMResp(QuerySM r)
    {
	super(r);

	messageId = r.getMessageId();
	finalDate = null;
	messageStatus = 0;
	errorCode = 0;
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	int len = (getHeaderLen()
		+ ((messageId != null) ? messageId.length() : 0)
		+ ((finalDate != null) ?
		    finalDate.toString().length() : 0));

	// 2 1-byte integers, 2 c-strings
	return (len + 2 + 2);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	String fdate = (finalDate == null) ? null : finalDate.toString();
	SMPPIO.writeCString(getMessageId(), out);
	SMPPIO.writeCString(fdate, out);
	SMPPIO.writeInt(messageStatus, 1, out);
	SMPPIO.writeInt(errorCode, 1, out);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("query_sm_resp");
    }
}
