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
import ie.omk.smpp.UnexpectedInputException;
import ie.omk.smpp.InvalidListNameException;
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.InvalidAddressException;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** Defines a destination address structure as used by SubmitMulti
  * and QueryMsgDetails.
  * XXX Move to ie.omk.smpp.util??
  * @author Oran Kelly
  * @version 1.0
  */
public class SmeAddress
    implements java.io.Serializable
{
    /** Does this represent an Sme address or distribution list */
    private boolean		isSme = true;

    /** This flag specifies whether or not the isSme flag is used.
      * If this is true, then the flag will be looked for on InputStreams
      * and written to OutputStreams.  This form of an SmeAddress is only
      * used by SubmitMulti (possibly another?) to distinguish between
      * Sme's and Distribution lists.
      */
    protected boolean		useFlag = false;

    /** Destination address Ton */
    private int			ton = GSMConstants.GSM_TON_UNKNOWN;
    /** Destination address Npi */
    private int			npi = GSMConstants.GSM_NPI_UNKNOWN;;
    /** Destination address */
    private String		addr = null;

    /** Construct a new Sme address.
      * The default address is TON_UNKNOWN, NPI_UNKNOWN and the address string
      * will be null.
      */
    public SmeAddress()
    {
	isSme = true;
    }

    /** Construct a new Sme address
      * @param flag Specify whether to use the destination flag or not
      */
    public SmeAddress(boolean flag)
    {
	this();
	useFlag = flag;
    }

    /** Construct a new Sme address with specified ISDN parameters
      * @param ton Destination address Ton
      * @param npi Destination address Npi
      * @param addr Destination address (Up to 20 characters)
      * @exception ie.omk.smpp.InvalidAddressException if the address is null or
      * is invalid.
      * @exception ie.omk.smpp.StringTooLongException if the address is too
      * long.
      */
    public SmeAddress(int ton, int npi, String addr)
	throws ie.omk.smpp.SMPPException
    {
	isSme = true;
	this.ton = ton;
	this.npi = npi;

	if(addr == null)
	    throw new InvalidAddressException("Address cannot be null.");
	if (addr.length() > 20)
	    throw new StringTooLongException(20);

	this.addr = addr;
    }

    /** Construct a new Sme address with specified ISDN parameters
      * @param ton Destination address Ton
      * @param npi Destination address Npi
      * @param addr Destination address (Up to 20 characters)
      * @param flag Specify whether to use the destination flag or not
      * @exception ie.omk.smpp.SMPPException If destination address is
      * null or is invalid
      */
    public SmeAddress(int ton, int npi, String addr, boolean flag)
	throws ie.omk.smpp.SMPPException
    {
	this(ton, npi, addr);
	useFlag = flag;
    }

    /** Construct a new Destination address for a distribution list.
      * The destination flag will be used by default.
      * @param addr Distribution list name (Up to 20 characters)
      * @exception ie.omk.smpp.InvalidListNameException if the distribution
      * list name is null or invalid.
      * @exception ie.omk.smpp.StringTooLongException if the distribution list
      * name is too long.
      */
    public SmeAddress(String addr)
	throws ie.omk.smpp.SMPPException
    {
	isSme = false;
	useFlag = true;
	ton = npi = -1;

	if(addr == null)
	    throw new InvalidListNameException("List name cannot be null.");
	if (addr.length() > 20)
	    throw new StringTooLongException(20);

	this.addr = new String(addr);
    }


    /** Read in a SmeAddress from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public SmeAddress(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	this(in, false);
    }

    /** Read in a SmeAddress from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @param id If true then read an extra int to distinguish between
      * Sme addresses and distribution lists.
      * @exception ie.omk.smpp.UnexpectedInputException if the dest_flag
      * is a value other than 1 or 2.
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public SmeAddress(InputStream in, boolean id)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	useFlag = id;

	/* The form that identifies itself is only used in
	 * SubmitMulti, QueryMsgDetailsResp
	 * to distinguish betweed sme addresses and dist_lists.
	 */
	if(id) {
	    int x = SMPPIO.readInt(in, 1);
	    if(x == 1)
		isSme = true;
	    else if(x == 2)
		isSme = false;
	    else
		throw new UnexpectedInputException(x);
	}

	if(isSme) {
	    ton =  SMPPIO.readInt(in, 1);
	    npi =  SMPPIO.readInt(in, 1);
	    addr = SMPPIO.readCString(in);
	} else {
	    addr = SMPPIO.readCString(in);
	    ton = npi = -1;
	}
    }

    /** Get the Type Of Number.
      */
    public int getTON()
    {
	return (this.ton);
    }

    /** Get the Numbering Plan Indicator.
      */
    public int getNPI()
    {
	return (this.npi);
    }

    /** Get the address string of this SmeAddress.
      * @return The address string. If the address is null, this method will
      * return an empty String, it will never return null.
      */
    public String getAddress()
    {
	return ((this.addr == null) ? "" : this.addr);
    }

    /** Determine whether this SmeAddress is for a distribution list or not.
      * @return true if this object represents a distribution list, false if it
      * is an address.
      */
    public boolean isDistList()
    {
	return (!this.isSme);
    }

    /** Get the number of bytes this address would be encoded as to an
      * OutputStream.
      * @return the number of bytes this object would encode as.
      */
    public int size()
    {
	int len = 2 + ((addr != null) ? addr.length() : 0);
	len++; // for the nul byte

	if(isSme) {
	    if(useFlag)
		len++;
	} else {
	    len = 1 + ((addr != null) ? addr.length() : 0);
	    len++;
	}
	return (len);
    }

    /** Write a byte representation of this address to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    public void writeTo(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if(isSme) {
	    if(useFlag)
		SMPPIO.writeInt(1, 1, out);
	    SMPPIO.writeInt((int)ton, 1, out);
	    SMPPIO.writeInt((int)npi, 1, out);
	    SMPPIO.writeCString(addr, out);
	} else {
	    // dest_flag is 2 for a distribution list
	    SMPPIO.writeInt(2, 1, out);
	    SMPPIO.writeCString(addr, out);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	if(isSme)
	    return new String("Sme(" + ton + ", " + npi + ", " + addr +")");
	else
	    return new String("Dist(" + addr + ")");
    }
}
