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

/** Response packet to a query int message request
  * @author Oran Kelly
  * @version 1.0
  */
public class QuerySMResp
	extends ie.omk.smpp.message.SMPPResponse
{
// File identifier string: used for debug output
	private static String FILE = "QuerySMResp";

	/** Construct a new QuerySMResp with specified sequence number.
	  * @param seqNo The sequence number to use
	  */
	public QuerySMResp(int seqNo)
	{
		super(ESME_QUERY_SM_RESP, seqNo);
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

		if(cmdStatus != 0)
			return;

		try
		{
			messageId = Integer.parseInt(readCString(in), 16);
			finalDate = makeDateFromString(readCString(in));
			messageStatus =  readInt(in, 1);
			errorCode =  readInt(in, 1);
		}
		catch(IOException iox)
		{
			throw new SMPPException("Input stream does not contain a query_sresp packet.");
		}
		catch(NumberFormatException nx)
		{
			throw new SMPPException("Error reading message Id from the input stream.");
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


	/** Set the message Id
	  * @param messageId The message Id to use (Up to 8 chars)
	  * @exception ie.omk.smpp.SMPPException If the message Id is invalid
	  */
	public void setMessageId(int id)
		{ super.setMessageId(id); }

	/** Set the time that the message reached a final state
	  * @param d The Date the message reached it's final state (must be in UTC)
	  */
	public void setFinalDate(Date d)
		{ super.setFinalDate(d); }

	/** Set the status of this message
	  * @param s The current status of the message.
	  */
	public void setMessageStatus(int s)
		{ super.setMessageStatus(s); }

	/** Set the error code
	  * @param s The error code
	  */
	public void setErrorCode(int s)
		{ super.setErrorCode(s); }

	/** Get the message Id */
	public int getMessageId()
		{ return super.getMessageId(); }

	/** Get the date the message reached a final state */
	public Date getFinalDate()
		{ return super.getFinalDate(); }

	/** Get the status of this message */
	public int getMessageStatus()
		{ return super.getMessageStatus(); }

	/** Get the error code of this message */
	public int getErrorCode()
		{ return super.getErrorCode(); }


	/** Get the size in bytes of this packet */
	public int size()
	{
		String id = Integer.toHexString(getMessageId());

		return(super.size() + 4
			+ ((id != null) ? id.length() : 0)
			+ ((finalDate != null) ?
				makeDateString(finalDate).length() : 0));
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
			writeCString(makeDateString(finalDate), b);
			writeInt(messageStatus, 1, b);
			writeInt(errorCode, 1, b);

			b.writeTo(out);
		}
		catch(IOException x)
		{
			throw new SMPPException("Error writing bind_receiver packet to output stream");
		}
	}

	public String toString()
	{
		return new String("query_sm_resp");
	}
}

