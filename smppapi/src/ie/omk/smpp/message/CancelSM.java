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

/** Cancel a int message
  * @author Oran Kelly
  * @version 1.0
  */
public class CancelSM
	extends ie.omk.smpp.message.SMPPRequest
{
// File identifier string: used for debug output
	private static String FILE = "CancelSM";

	/** Construct a new CancelSM with specified sequence number.
	  * @param seqNo The sequence number to use
	  */
	public CancelSM(int seqNo)
	{
		super(ESME_CANCEL_SM, seqNo);
	}

	/** Read in a CancelSM from an InputStream.  A full packet,
	  * including the header fields must exist in the stream.
	  * @param in The InputStream to read from
	  * @exception ie.omk.smpp.SMPPException If the stream does not
	  * contain a CancelSM packet.
	  * @see java.io.InputStream
	  */
	public CancelSM(InputStream in)
	{
		super(in);

		if(cmdStatus != 0)
			return;

		try
		{
			serviceType = readCString(in);
			messageId = Integer.parseInt(readCString(in), 16);
			source = new SmeAddress(in);
			destination = new SmeAddress(in);
		}
		catch(IOException iox)
		{
			throw new SMPPException("Input stream does not contain a cancel_sm packet");
		}
		catch(NumberFormatException x)
		{
			throw new SMPPException("Error parsing the message Id field from stream.");
		}
	}

	/** Set the service type field */
	public void setServiceType(String s)
		{ super.setServiceType(s); }

	/** Set the Message Id to cancel */
	public void setMessageId(int id)
		{ super.setMessageId(id); }

	/** Set the source address
	  * @param ton Source address type of number
	  * @param npi Source address numbering plan indicator
	  * @param addr Source address (Up to 20 characters)
	  * @exception ie.omk.smpp.SMPPException If the address is invalid
	  * @see SMPPPacket#GSM_TON_UNKNOWN
	  * @see SMPPPacket#GSM_NPI_UNKNOWN
	  */
//	public void setSource(int ton, int npi, String addr)
//		{ super.setSource(new SmeAddress(ton, npi, addr); } 

	/** Set the source address
	  * @see SmeAddress
	  */
	public void setSource(SmeAddress d)
		{ super.setSource(d); }

	/** Set the destination address
	  * @param ton destination address type of number
	  * @param npi destination address numbering plan indicator
	  * @param addr destination address (Up to 20 characters)
	  * @exception ie.omk.smpp.SMPPException If the address is invalid
	  * @see SMPPPacket#GSM_TON_UNKNOWN
	  * @see SMPPPacket#GSM_NPI_UNKNOWN
	  */
//	public void setDestination(int ton, int npi, String addr)
//		{ super.setDestination(new SmeAddress(ton, npi, addr)); }

	/** Set the source address
	  * @see SmeAddress
	  */
	public void setDestination(SmeAddress d)
		{ super.setDestination(d); }

	/** Get the service type */
	public String getServiceType()
		{ return super.getServiceType(); }

	/** Get the message Id */
	public int getMessageId()
		{ return super.getMessageId(); }

	/** Get the source address */
	public SmeAddress getSource()
		{ return super.getSource(); }
	
	/** Get the destination address */
	public SmeAddress getDestination()
		{ return super.getDestination(); }

	/** Get the size in bytes of this packet */
	public int size()
	{
		String id = Integer.toHexString(getMessageId());

		return (super.size() + 2
			+ ((serviceType != null) ? serviceType.length() : 0)
			+ ((id != null) ? id.length() : 0)
			+ ((source != null) ? source.size() : 3)
			+ ((destination != null) ? destination.size() : 3));
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
			writeCString(serviceType, b);
			writeCString(Integer.toHexString(getMessageId()), b);
			if(source != null)
				source.writeTo(b);
			else
			{
				// Write ton=0(null), npi=0(null), address=\0(nul)
				writeInt(0, 2, b);
				writeCString(null, b);
			}

			if(destination != null)
				destination.writeTo(b);
			else
			{
				// Write ton=0(null), npi=0(null), address=\0(nul)
				writeInt(0, 2, b);
				writeCString(null, b);
			}

			b.writeTo(out);
		}
		catch(IOException x)
		{
			throw new SMPPException("Error writing SMPP header information to output stream");
		}
	}

	public String toString()
	{
		return new String("cancel_sm");
	}
}

