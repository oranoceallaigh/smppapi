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

/** Bind to the SMSC as transmitter
  * @author Oran Kelly
  * @version 1.0
  */
public class BindTransmitter
    extends ie.omk.smpp.message.SMPPRequest
{
    String		sysId,
    password,
    sysType,
    addressRange;
    int		interfaceVer,
    addrTon,
    addrNpi;


    /** Construct a new BindTransmitter with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public BindTransmitter(int seqNum)
    {
	super(ESME_BNDTRN, seqNum);

	// Initialise the packet's fields to null values
	sysId = password = sysType = addressRange = null;
	interfaceVer = addrTon = addrNpi = 0;
    }

    /*	public BindTransmitter(int seqNum, String sysId, String password,
	String sysType, int interfaceVer)
	{
	super(ESME_BNDTRN, seqNum);

	try {
	if(sysId.length() < 16)
	sysId = new String(sysId);
	else
	throw new SMPPException("System Id must be < 16 chars");
	}
	catch(NullPointerException x)
	{ sysId = null; }

	try {
	if(password.length() < 9)
	password = new String(password);
	else
	throw new SMPPException("Password must be < 9 chars");
	}
	catch(NullPointerException x)
	{ password = null; }

	try {
	if(sysType.length() < 13)
	sysType = new String(sysType);
	else
	throw new SMPPException("System type must be < 13 chars");
	}
	catch(NullPointerException x)
	{ sysType = null; }

	this.interfaceVer = interfaceVer;
	this.addrTon = 0;
	this.addrNpi = 0;
	this.addressRange = null;
	}
      */

    /** Read in a BindTransmitter from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a BindReceiverResp packet.
      * @see java.io.InputStream
      */
    public BindTransmitter(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    sysId = SMPPIO.readCString(in);
	    password = SMPPIO.readCString(in);
	    sysType = SMPPIO.readCString(in);
	    interfaceVer =  SMPPIO.readInt(in, 1);
	    addrTon =  SMPPIO.readInt(in, 1);
	    addrNpi =  SMPPIO.readInt(in, 1);
	    addressRange = SMPPIO.readCString(in);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "bind_transmitter packet.");
	}
    }

    /** Set the system Id
      * @param sysId The System Id to use (Up to 15 characters)
      * @exception ie.omk.smpp.SMPPException Id the System Id is invalid
      */
    public void setSystemId(String sysId)
    {
	if(sysId == null) {
	    sysId = null;
	    return;
	}

	if(sysId.length() < 16)
	    this.sysId = new String(sysId);
	else
	    throw new SMPPException("System Id must be < 16 chars");
    }

    /** Set the password for this transmitter
      * @param password The new password to use (Up to 8 characters in length)
      * @exception ie.omk.smpp.SMPPException If the password is invalid
      */
    public void setPassword(String password)
    {
	if(password == null) {
	    password = null;
	    return;
	}

	if(password.length() < 9)
	    this.password = new String(password);
	else
	    throw new SMPPException("Password must be < 9 chars");
    }

    /** Set the system type for this transmitter
      * @param sysType The new system type (Up to 12 characters in length)
      * @exception ie.omk.smpp.SMPPException If the system type is invalid
      */
    public void setSystemType(String sysType)
    {
	if(sysType == null) {
	    sysType = null;
	    return;
	}

	if(sysType.length() < 13)
	    this.sysType = new String(sysType);
	else
	    throw new SMPPException("System Type must be < 13 chars");
    }

    /** Set the interface version being used by this transmitter
      * @param interfaceVer The interface version to report to the SMSC
      * (major version number only)
      */ 
    public void setInterfaceVersion(int interfaceVer)
    {
	this.interfaceVer = interfaceVer;
    }

    /** Set the message routing Ton for this transmitter
      * @param addrTon The new Type Of Number to use
      */
    public void setAddressTon(int addrTon)
    {
	this.addrTon = addrTon;
    }

    /** Set the message routing Npi for this transmitter
      * @param addrNpi The new Numbering plan indicator to use
      */
    public void setAddressNpi(int addrNpi)
    {
	this.addrNpi = addrNpi;
    }

    /** Set the message routing address range for this transmitter
      * @param addressRange The new address range to use (Up to 40 characters)
      * @exception ie.omk.smpp.SMPPException If the address range is invalid
      */
    public void setAddressRange(String addressRange)
    {
	if(addressRange == null) {
	    this.addressRange = null;
	    return;
	}

	if(addressRange.length() < 41)
	    this.addressRange = new String(addressRange);
	else
	    throw new SMPPException("Address Range invalid.");
    }

    /** Get the system Id */
    public String getSystemId()
    {
	return (sysId == null) ? null : new String(sysId);
    }

    /** Get the authentication password */
    public String getPassword()
    {
	return (password == null) ? null : new String(password);
    }

    /** Get the current system type */
    public String getSystemType()
    {
	return (sysType == null) ? null : new String(sysType);
    }

    /** Get the routing address regular expression */
    public String getAddressRange()
    {
	return (addressRange == null) ? null : new String(addressRange);
    }

    /** Get the Type of number */
    public int getAddressTon()
    {
	return addrTon;
    }

    /** Get the Numbering plan indicator */
    public int getAddressNpi()
    {
	return addrNpi;
    }

    /** Get the interface version */
    public int getInterfaceVersion()
    {
	return interfaceVer;
    }


    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	// Calculated as the size of the header plus 3 1-byte ints and
	// 4 null-terminators for the strings plus the length of the strings
	return (getHeaderLen() + 7
		+ ((sysId != null) ? sysId.length() : 0)
		+ ((password != null) ? password.length() : 0)
		+ ((sysType != null) ? sysType.length() : 0)
		+ ((addressRange != null) ? addressRange.length() : 0));
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception ie.omk.smpp.SMPPException If an I/O error occurs
      * @see java.io.OutputStream
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(sysId, out);
	SMPPIO.writeCString(password, out);
	SMPPIO.writeCString(sysType, out);
	SMPPIO.writeInt(interfaceVer, 1, out);
	SMPPIO.writeInt(addrTon, 1, out);
	SMPPIO.writeInt(addrNpi, 1, out);
	SMPPIO.writeCString(addressRange, out);
    }

    public String toString()
    {
	return new String("bind_transmitter");
    }
}
