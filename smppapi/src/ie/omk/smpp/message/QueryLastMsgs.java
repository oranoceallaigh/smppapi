/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
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
 * $Id$
 */
package ie.omk.smpp.message;

import java.io.IOException;
import java.io.OutputStream;

import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;

import org.apache.log4j.Logger;

/** Query the last number of messages sent from a certain ESME.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
  *   source<br>
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryLastMsgs
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Number of messages to look up */
    private int msgCount;

    /** Construct a new QueryLastMsgs.
      */
    public QueryLastMsgs()
    {
	super(QUERY_LAST_MSGS);
	msgCount = 0;
    }

    /** Construct a new QueryLastMsgs with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public QueryLastMsgs(int seqNum)
    {
	super(QUERY_LAST_MSGS, seqNum);
	msgCount = 0;
    }

    /** Set the number of messages to look up. The minimum number of messages to
     * query is 1 and the maximum is 100.
     * @param n The message count (1 &lt;= n &lt;= 100)
     * @throws ie.omk.smpp.message.InvalidParameterValueException if the count
     * is set outside the valid range.
     */
    public void setMsgCount(int n) throws InvalidParameterValueException {
	if(n > 0 && n <= 100) {
	    this.msgCount = n;
	} else {
	    throw new InvalidParameterValueException("Message count must be between 1 and 100", n);
	}
    }

    /** Get the number of messages being requested. */
    public int getMsgCount()
    {
	return (msgCount);
    }

    public int getBodyLength()
    {
	int len = (((source != null) ? source.getLength() : 3));

	// 1 1-byte integer
	return (len + 1);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @throws java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException
    {
	if(source != null) {
	    source.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    new Address(GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	}
	SMPPIO.writeInt(msgCount, 1, out);
    }

    public void readBodyFrom(byte[] body, int offset) throws SMPPProtocolException {
	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	msgCount = SMPPIO.bytesToInt(body, offset++, 1);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("query_last_msgs");
    }
}
