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


import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.version.SMPPVersion;

import java.io.OutputStream;

/** Abstract parent of BindTransmitter and BindReceiver.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class Bind
    extends ie.omk.smpp.message.SMPPRequest
{
    /** System Id */
    private String sysId = null;

    /** Authentication password */
    private String password = null;

    /** System type */
    private String sysType = null;

    /** Address range for message routing */
    private String addressRange = null;

    /** Address Type Of Number for message routing */
    private int addrTon = 0;

    /** Address Numbering Plan Indicator for message routing */
    private int addrNpi = 0;


    public Bind(int id)
    {
	super(id);
    }

    /** Set the system Id
      * @param sysId The System Id to use (Up to 15 characters)
      * @throws ie.omk.smpp.message.InvalidParameterValueException If the system
      * ID fails version validation.
      */
    public void setSystemId(String sysId) throws InvalidParameterValueException {
	if(sysId != null) {
	    if (version.validateSystemId(sysId))
		this.sysId = sysId;
	    else
		throw new InvalidParameterValueException("Invalid system ID", sysId);
	} else {
	    this.sysId = null;
	}
    }

    /** Set the password for this transmitter
      * @param password The new password to use (Up to 8 characters in length)
      * @throws ie.omk.smpp.message.InvalidParameterValueException If the
      * password fails version validation.
      */
    public void setPassword(String password) throws InvalidParameterValueException {
	if(password != null) {
	    if (version.validatePassword(password))
		this.password = password;
	    else
		throw new InvalidParameterValueException("Invalid password", password);
	} else {
	    this.password = null;
	}
    }

    /** Set the system type for this transmitter
      * @param sysType The new system type (Up to 12 characters in length)
      * @throws ie.omk.smpp.message.InvalidParameterValueException If the system
      * type fails version validation.
      */
    public void setSystemType(String sysType) throws InvalidParameterValueException {
	if(sysType != null) {
	    if (version.validateSystemType(sysType))
		this.sysType = sysType;
	    else
		throw new InvalidParameterValueException("Invalid system type", sysType);
	} else {
	    this.sysType = null;
	}
    }

    /** Set the message routing Ton for this transmitter
      * @param addrTon The new Type Of Number to use
      * @throws ie.omk.smpp.message.InvalidParameterValueException If the TON
      * fails version validation.
      */
    public void setAddressTon(int addrTon) throws InvalidParameterValueException {
	// XXX check the TON?
	this.addrTon = addrTon;
    }

    /** Set the message routing Npi for this transmitter
      * @param addrNpi The new Numbering plan indicator to use
      * @throws ie.omk.smpp.message.InvalidParameterValueException If the NPI
      * fails version validation.
      */
    public void setAddressNpi(int addrNpi) throws InvalidParameterValueException {
	// XXX check the NPI?
	this.addrNpi = addrNpi;
    }

    /** Set the message routing address range for this transmitter
      * @param addressRange The new address range to use (Up to 40 characters)
      * @throws ie.omk.smpp.message.InvalidParameterValueException If the
      * address range fails version validation.
      */
    public void setAddressRange(String addressRange) throws InvalidParameterValueException {
	if(addressRange != null) {
	    if (version.validateAddressRange(addressRange))
		this.addressRange = addressRange;
	    else
		throw new InvalidParameterValueException("Invalid address range", addressRange);
	} else {
	    this.addressRange = null;
	}
    }

    /** Get the system Id */
    public String getSystemId()
    {
	return (sysId);
    }

    /** Get the authentication password */
    public String getPassword()
    {
	return (password);
    }

    /** Get the current system type */
    public String getSystemType()
    {
	return (sysType);
    }

    /** Get the routing address regular expression */
    public String getAddressRange()
    {
	return (addressRange);
    }

    /** Get the Type of number */
    public int getAddressTon()
    {
	return (addrTon);
    }

    /** Get the Numbering plan indicator */
    public int getAddressNpi()
    {
	return (addrNpi);
    }

    /** Get the interface version */
    public int getInterfaceVersion()
    {
	return (version.getVersionID());
    }


    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getBodyLength()
    {
	// Calculated as the size of the header plus 3 1-byte ints and
	// 4 null-terminators for the strings plus the length of the strings
	int len = (((sysId != null) ? sysId.length() : 0)
		+ ((password != null) ? password.length() : 0)
		+ ((sysType != null) ? sysType.length() : 0)
		+ ((addressRange != null) ? addressRange.length() : 0));

	// 3 1-byte integers, 4 c-strings
	return (len + 3 + 4);
    }

    /** Write the byte representation of this packet to an OutputStream.
      * @param out The output stream to write to
      * @throws java.io.IOException If there is an error writing to the
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException
    {
	SMPPIO.writeCString(sysId, out);
	SMPPIO.writeCString(password, out);
	SMPPIO.writeCString(sysType, out);
	SMPPIO.writeInt(version.getVersionID(), 1, out);
	SMPPIO.writeInt(addrTon, 1, out);
	SMPPIO.writeInt(addrNpi, 1, out);
	SMPPIO.writeCString(addressRange, out);
    }

    public void readBodyFrom(byte[] body, int offset) throws SMPPProtocolException {
	try {
	    sysId = SMPPIO.readCString(body, offset);
	    offset += sysId.length() + 1;

	    password = SMPPIO.readCString(body, offset);
	    offset += password.length() + 1;

	    sysType = SMPPIO.readCString(body, offset);
	    offset += sysType.length() + 1;

	    // XXX if this api is used to write a server application, this won't
	    // work for apps that try and bind as a later api version (this api
	    // should be able to negotiate downward).
	    int interfaceVer = SMPPIO.bytesToInt(body, offset++, 1);
	    version = SMPPVersion.getVersion(interfaceVer);
	    addrTon = SMPPIO.bytesToInt(body, offset++, 1);
	    addrNpi = SMPPIO.bytesToInt(body, offset++, 1);
	    addressRange = SMPPIO.readCString(body, offset);
	} catch (ie.omk.smpp.version.VersionException x) {
	    throw new SMPPProtocolException("Invalid interface version in response", x);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("bind");
    }
}
