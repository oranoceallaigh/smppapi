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

/** Bind to the SMSC as a Receiver
  * @author Oran Kelly
  * @version 1.0
  */
public class BindReceiver
	extends ie.omk.smpp.message.SMPPRequest
{
// File identifier string: used for debug output
	private static String FILE = "BindReceiver";

	/** System Id */
	String				sysId;
	/** Authentication password */
	String				password;
	/** System type */
	String				sysType;
	/** Address range for message routing */
	String				addressRange;
	/** Interface version */
	int				interfaceVer;
	/** Address Type Of Number for message routing */
	int				addrTon;
	/** Address Numbering Plan Indicator for message routing */
	int				addrNpi;

	/** Constructs a new BindReceiver with specified sequence number
	  * @param seqNo The sequence number to be used by this packet
	  */
	public BindReceiver(int seqNo)
	{
		super(ESME_BNDRCV, seqNo);

		// Initialise the packets fields to null values
		sysId = password = sysType = addressRange = null;
		interfaceVer = addrTon = addrNpi = 0;
	}

	/*
	public BindReceiver(int seqNo, String sysId, String password,
		String sysType, int interfaceVer, int addrTon, int addrNpi,
		String addressRange)
	{
		super(ESME_BNDRCV, seqNo);
		
		try
		{
			if(sysId.length() < 16)
				sysId = new String(sysId);
			else
				throw new SMPPException("System Id must be < 16 chars");
		}
		catch(NullPointerException x)
			{ sysId = null; }

		try
		{
			if(password.length() < 9)
				password = new String(password);
			else
				throw new SMPPException("Password must be < 9 chars");
		}
		catch(NullPointerException x)
			{ password = null; }

		try
		{
			if(sysType.length() < 13)
				sysType = new String(sysType);
			else
				throw new SMPPException("System type must be < 13 chars");
		}
		catch(NullPointerException x)
			{ sysType = null; }

		try
		{
			if(addressRange.length() < 41)
				addressRange = new String(addressRange);
			else
				throw new SMPPException("Address Range invalid.");
		}
		catch(NullPointerException x)
			{ addressRange = null; }

		this.interfaceVer = interfaceVer;
		this.addrTon = addrTon;
		this.addrNpi = addrNpi;
	}*/

	/** Read a BindReceiver packet from an InputStream.  An entire packet
	  * must exist in the stream, including the header information
	  * @param in The InputStream to read from
	  * @exception ie.omk.smpp.SMPPException If the InputStream does not
	  * contain a BindReceiver packet
	  * @see java.io.InputStream
	  */
	public BindReceiver(InputStream in)
	{
		super(in);

		if(cmdStatus != 0)
			return;

		try
		{
			// First the system ID
			sysId = readCString(in);

			// Get the password
			password = readCString(in);

			// System type
			sysType = readCString(in);

			// Interface version
			interfaceVer =  readInt(in, 1);

			// Address TON
			addrTon =  readInt(in, 1);

			// Address NPI
			addrNpi =  readInt(in, 1);

			// Address range
			addressRange = readCString(in);
		}
		catch(IOException x)
		{
			throw new SMPPException("Input stream does not contain a bind_receiver packet");
		}
	}
	
	/** Set the System Id for this Receiver
	  * @param sysId The new System Id (Up to 15 characters in length)
	  * @exception ie.omk.smpp.SMPPException If the Id is invalid
	  */
	public void setSystemId(String sysId)
	{
		if(sysId == null)
			{ sysId = null; return; }

		if(sysId.length() < 16)
			this.sysId = new String(sysId);
		else
			throw new SMPPException("System Id must be < 16 chars");
	}

	/** Set the password for this receiver
	  * @param password The new password to use (Up to 8 characters in length)
	  * @exception ie.omk.smpp.SMPPException If the password is invalid
	  */
	public void setPassword(String password)
	{
		if(password == null)
			{ password = null; return; }
			
		if(password.length() < 9)
			this.password = new String(password);
		else
			throw new SMPPException("Password must be < 9 chars");
	}

	/** Set the system type for this receiver
	  * @param sysType The new system type (Up to 12 characters in length)
	  * @exception ie.omk.smpp.SMPPException If the system type is invalid
	  */
	public void setSystemType(String sysType)
	{
		if(sysType == null)
			{ sysType = null; return; }
			
		if(sysType.length() < 13)
			this.sysType = new String(sysType);
		else
			throw new SMPPException("System type must be < 13 chars");
	}

	/** Set the interface version being used by this receiver
	  * @param interfaceVer The interface version to report to the SMSC
	  * (major version number only)
	  */ 
	public void setInterfaceVersion(int interfaceVer)
		{ this.interfaceVer = interfaceVer; }

	/** Set the message routing Ton for this receiver
	  * @param addrTon The new Type Of Number to use
	  */
	public void setAddressTon(int addrTon)
		{ this.addrTon = addrTon; }

	/** Set the message routing Npi for this receiver
	  * @param addrNpi The new Numbering plan indicator to use
	  */
	public void setAddressNpi(int addrNpi)
		{ this.addrNpi = addrNpi; }

	/** Set the message routing address range for this receiver
	  * @param addressRange The new address range to use (Up to 40 characters)
	  * @exception ie.omk.smpp.SMPPException If the address range is invalid
	  */
	public void setAddressRange(String addressRange)
	{
		if(addressRange == null)
			{ this.addressRange = null; return; }
			
		if(addressRange.length() < 41)
			this.addressRange = new String(addressRange);
		else
			throw new SMPPException("Address Range invalid.");
	}

	/** Get the system Id */
	public String getSystemId()
		{ return (sysId == null) ? null : new String(sysId); }

	/** Get the authentication password */
	public String getPassword()
		{ return (password == null) ? null : new String(password); }

	/** Get the current system type */
	public String getSystemType()
		{ return (sysType == null) ? null : new String(sysType); }

	/** Get the routing address regular expression */
	public String getAddressRange()
		{ return (addressRange == null) ? null : new String(addressRange); }

	/** Get the Type of number */
	public int getAddressTon()
		{ return addrTon; }

	/** Get the Numbering plan indicator */
	public int getAddressNpi()
		{ return addrNpi; }

	/** Get the interface version */
	public int getInterfaceVersion()
		{ return interfaceVer; }


	/** Return the size in bytes of this packet. */
	public int size()
	{
		// Calculated as the size of the header plus 3 1-byte ints and
		// 4 null-terminators for the strings plus the length of the strings
		return (super.size() + 7
			+ ((sysId != null) ? sysId.length() : 0)
			+ ((password != null) ? password.length() : 0)
			+ ((sysType != null) ? sysType.length() : 0)
			+ ((addressRange != null) ? addressRange.length() : 0));
	}
	
	/** Write the byte representation of this packet to an OutputStream.
	  * @param out The output stream to write to
	  * @exception ie.omk.smpp.SMPPException If there is an error writing
	  * to the output stream.
	  */
	public void writeTo(OutputStream out)
	{
		try
		{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			super.writeTo(b);

			writeCString(sysId, b);
			writeCString(password, b);
			writeCString(sysType, b);
			writeInt((int)interfaceVer, 1, b);
			writeInt((int)addrTon, 1, b);
			writeInt((int)addrNpi, 1, b);
			writeCString(addressRange, b);

			b.writeTo(out);
		}
		catch(IOException x)
		{
			throw new SMPPException("Error writing bind_receiver packet to output stream");
		}
	}

	public String toString()
	{
		return new String("bind_receiver");
	}
}

