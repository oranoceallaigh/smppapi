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

/** Query the last number of messages sent from a certain ESME
  * @author Oran Kelly
  * @version 1.0
  */
public class QueryLastMsgs
	extends ie.omk.smpp.message.SMPPRequest
{
// File identifier string: used for debug output
	private static String FILE = "QueryLastMsgs";

	/** No of messages to look up */
	int			noOfMsgs;

	/** Construct a new QueryLastMsgs with specified sequence number.
	  * @param seqNo The sequence number to use
	  */
	public QueryLastMsgs(int seqNo)
	{
		super(ESME_QUERY_LAST_MSGS, seqNo);

		noOfMsgs = 0;
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

		if(cmdStatus != 0)
			return;

		try
		{
			source = new SmeAddress(in);
			noOfMsgs = readInt(in, 1);
		}
		catch(IOException iox)
		{
			throw new SMPPException("Input stream does not contain a query_last_msgs packet.");
		}
	}

	/** Set the source address
	  * @param ton Source address Type of number
	  * @param npi Source address Numbering plan indicator
	  * @param addr Source address (Up to 20 characters)
	  * @exception ie.omk.smpp.SMPPException If the Source address is invalid
	  */
	public void setSource(int ton, int npi, String addr)
		{ super.setSource(new SmeAddress(ton, npi, addr)); }

	/** Set the source address
	  * @see SmeAddress
	  */
	public void setSource(SmeAddress d)
		{ super.setSource(d); }

	/** Set the number of messages to look up */
	public void setMsgCount(int s)
	{
		if(s > 0 && s <= 100)
			noOfMsgs = s;
		else
			throw new SMPPException("Number of messages to query must be > 0 and <= 100");
	}

	/** Get the source address */
	public SmeAddress getSource()
		{ return super.getSource(); }
	
	/** Get the count of the number of messages being requested */
	public int getMsgCount()
		{ return noOfMsgs; }

	/** Get the size in bytes of this packet */
	public int size()
	{
		return (super.size() + 1
			+ ((source != null) ? source.size() : 3));
	}
	
	/** Write a byte representation of this packet to an OutputStream
	  * @param out The OutputStream to write to
	  * @exception ie.omk.smpp.SMPPException If an I/O error occurs
	  * @see java.io.OutputStream
	  */
	public void writeTo(OutputStream out)
	{
		try
		{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			super.writeTo(b);

			if(source != null)
				source.writeTo(b);
			else
			{
				writeInt(0, 2, b);
				writeCString(null, b);
			}
			writeInt(noOfMsgs, 1, b);

			b.writeTo(out);
		}
		catch(IOException x)
		{
			throw new SMPPException("Error writing query_last_msgs packet to output stream");
		}
	}

	public String toString()
	{
		return new String("query_last_msgs");
	}
	
}

