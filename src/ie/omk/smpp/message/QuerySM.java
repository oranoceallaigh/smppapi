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
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Query the state of a int message
  * @author Oran Kelly
  * @version 1.0
  */
public class QuerySM
	extends ie.omk.smpp.message.SMPPRequest
{
// File identifier string: used for debug output
	private static String FILE = "QuerySM";

	/** Construct a new QuerySM with specified sequence number.
	  * @param seqNo The sequence number to use
	  */
	public QuerySM(int seqNo)
	{
		super(ESME_QUERY_SM, seqNo);
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

		if(cmdStatus != 0)
			return;

		try
		{
			messageId = Integer.parseInt(readCString(in), 16);
			source = new SmeAddress(in);
		}
		catch(IOException iox)
		{
			throw new SMPPException("Input stream does not contain a query_sm packet.");
		}
	}

	/** Set the message Id
	  * @param messageId The message Id to use (Up to 8 chars)
	  * @exception ie.omk.smpp.SMPPException If the message Id is invalid
	  */
	public void setMessageId(int id)
		{ super.setMessageId(id); }

	/** Set Source address to match against in SMSC
	  * @param sourceTon Source Address Ton
	  * @param sourceNpi Source Address Npi
	  * @param sourceAddr Source Address (up to 20 characters)
	  * @exception ie.omk.smpp.SMPPException If the address is invalid
	  */
//	public void setSource(int ton, int npi, String addr)
//		{ super.setSource(new SmeAddress(ton, npi, addr); }

	/** Set the source address
	  * @see SmeAddress
	  */
	public void setSource(SmeAddress d)
		{ super.setSource(d); }

	/** Get the message Id */
	public int getMessageId()
		{ return super.getMessageId(); }

	/** Get the source address */
	public SmeAddress getSource()
		{ return super.getSource(); }

	/** Get the size in bytes of this packet */
	public int size()
	{
		String id = Integer.toHexString(getMessageId());

		return (super.size() + 1
			+ ((id != null) ? id.length() : 0)
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

			writeCString(Integer.toHexString(getMessageId()), b);
			if(source != null)
				source.writeTo(b);
			else
			{
				writeInt(0, 2, b);
				writeCString(null, b);
			}
			
			b.writeTo(out);
		}
		catch(IOException x)
		{
			throw new SMPPException("Error writing query_sm packet to output stream");
		}
	}

	public String toString()
	{
		return new String("query_sm");
	}
}

