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
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.smpp.message;

import java.io.*;
import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;

import ie.omk.debug.Debug;

/** Query Message details.
  * Get all information about an existing message at the SMSC.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
  *   messageId
  *   source
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryMsgDetails
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Length of the message text required */
    private int smLength;

    /** Construct a new QueryMsgDetails.
      */
    public QueryMsgDetails()
    {
	super(QUERY_MSG_DETAILS);
	smLength = 0;
    }

    /** Construct a new QueryMsgDetails with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public QueryMsgDetails(int seqNum)
    {
	super(QUERY_MSG_DETAILS, seqNum);
	smLength = 0;
    }

    /** Read in a QueryMsgDetails from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public QueryMsgDetails(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.QUERY_MSG_DETAILS)
	    throw new BadCommandIDException(SMPPPacket.QUERY_MSG_DETAILS,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	messageId = SMPPIO.readCString(in);
	source = new Address(in);
	smLength =  SMPPIO.readInt(in, 1);
    }*/

    /** Set the number of bytes of the original message required.
      * Minimum request length is 0, maximum is 160. If the length is outside
      * these bounds, it will be set to the min or max.
      * @param len The number of bytes required.
      */
    public void setSmLength(int len)
    {
	smLength = len;

	if(smLength < 0)
	    smLength = 0;
	if(smLength > 160)
	    smLength = 160;
    }

    /** Get the number of bytes of the original message being requested. */
    public int getSmLength()
    {
	return (smLength);
    }


    public int getBodyLength()
    {
	int len = (((messageId != null) ? messageId.length() : 0)
		+ ((source != null) ? source.getLength() : 3));

	// 1 1-byte integer, 1 c-string
	return (len + 1 + 1);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(getMessageId(), out);

	if(source != null) {
	    source.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    new Address(GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	}
	SMPPIO.writeInt(smLength, 1, out);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	messageId = SMPPIO.readCString(body, offset);
	offset += messageId.length() + 1;

	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	smLength =  SMPPIO.bytesToInt(body, offset++, 1);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("query_msg_details");
    }
}
