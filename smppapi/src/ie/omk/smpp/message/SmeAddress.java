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

/** Defines a destination address structure as used by SubmitMulti
  * and QueryMsgDetails
  * @author Oran Kelly
  * @version 1.0
  */
public class SmeAddress
{
    /** Does this represent an Sme address or distribution list */
    public boolean			isSme = true;

    /** This flag specifies whether or not the isSme flag is used.
      * If this is true, then the flag will be looked for on InputStreams
      * and written to OutputStreams.  This form of an SmeAddress is only
      * used by SubmitMulti (possibly another?) to distinguish between
      * Sme's and Distribution lists.
      */
    protected boolean		useFlag = false;

    /** Destination address Ton */
    public int			ton;
    /** Destination address Npi */
    public int			npi;
    /** Destination address */
    public String			addr;

    /** Construct a new Sme address */
    public SmeAddress()
    {
	isSme = true;
	ton = npi = 0;
	addr = null;
    }

    /** Construct a new Sme address
      * @param flag Specify whether to use the destination flag or not
      */
    public SmeAddress(boolean flag)
    {
	this();
	useFlag = true;
    }

    /** Construct a new Sme address with specified ISDN parameters
      * @param ton Destination address Ton
      * @param npi Destination address Npi
      * @param addr Destination address (Up to 20 characters)
      * @exception ie.omk.smpp.SMPPException If destination address is
      * null or is invalid
      */
    public SmeAddress(int ton, int npi, String addr)
    {
	isSme = true;
	this.ton = ton;
	this.npi = npi;

	if(addr == null || addr.length() > 20) {
	    throw new SMPPException("Destination address cannot be null and "
		    + "must be < 21 chars.");
	} else {
	    this.addr = new String(addr);
	}
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
    {
	this(ton, npi, addr);
	useFlag = flag;
    }

    /** Construct a new Destination address for a distribution list.
      * The destination flag will be used by default.
      * @param addr Distribution list name (Up to 20 characters)
      * @exception ie.omk.smpp.SMPPException If <i>addr</i> is null or invalid
      */
    public SmeAddress(String addr)
    {
	isSme = false;
	useFlag = true;
	ton = npi = -1;

	if(addr == null || addr.length() > 20) {
	    throw new SMPPException("Distribution list name cannot be null "
		    + "and must be < 21 chars.");
	} else {
	    this.addr = new String(addr);
	}
    }


    /** Read in a SmeAddress from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a SmeAddress packet.
      * @see java.io.InputStream
      */
    public SmeAddress(InputStream in)
	throws IOException
    {
	this(in, false);
    }

    /** Read in a SmeAddress from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @param id If true then read an extra int to distinguish between
      * Sme addresses and distribution lists.
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a SmeAddress packet.
      * @see java.io.InputStream
      */
    public SmeAddress(InputStream in, boolean id)
	throws IOException
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
		throw new SMPPException("Input stream does not contain a "
			+ "SmeAddress struct.");
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

    /** Get the size in bytes of this packet */
    public int size()
    {
	if(isSme) {
	    if(useFlag)
		return (4 + ((addr != null) ? addr.length() : 0));
	    else
		return (3 + ((addr != null) ? addr.length() : 0));
	} else {
	    return (2 + ((addr != null) ? addr.length() : 0));
	}
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception ie.omk.smpp.SMPPException If an I/O error occurs
      * @see java.io.OutputStream
      */
    public void writeTo(OutputStream out)
	throws java.io.IOException
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

    public String toString()
    {
	if(isSme)
	    return new String("Sme(" + ton + ", " + npi + ", " + addr +")");
	else
	    return new String("Dist(" + addr + ")");
    }
}
