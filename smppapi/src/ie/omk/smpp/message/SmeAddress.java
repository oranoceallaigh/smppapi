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
import java.lang.reflect.*;
import java.util.*;
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Defines a destination address structure as used by SubmitMulti
  * and QueryMsgDetails
  * @author Oran Kelly
  * @version 1.0
  */
public class SmeAddress
{
    public static final int GSM_TON_UNKNOWN			= 0;
    public static final int GSM_TON_INTERNATIONAL		= 1;
    public static final int GSM_TON_NATIONAL			= 2;
    public static final int GSM_TON_NETWORK			= 3;
    public static final int GSM_TON_SUBSCRIBER			= 4;
    public static final int GSM_TON_ALPHANUMERIC		= 5;
    public static final int GSM_TON_ABBREVIATED			= 6;
    public static final int GSM_TON_RESERVED_EXTN		= 7;

    public static final int GSM_NPI_UNKNOWN			= 0;
    public static final int GSM_NPI_ISDN			= 1;
    public static final int GSM_NPI_E164			= 1;
    public static final int GSM_NPI_X121			= 3;
    public static final int GSM_NPI_TELEX			= 4;
    public static final int GSM_NPI_NATIONAL			= 8;
    public static final int GSM_NPI_PRIVATE			= 9;
    public static final int GSM_NPI_ERMES			= 10;
    public static final int GSM_NPI_RESERVED_EXTN		= 15;

    // These store the names for the Ton and Npis
    static Hashtable			tonTable = null;
    static Hashtable			npiTable = null;


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
	if(tonTable == null && npiTable == null)
	    makeNameTables();

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
	makeNameTables();

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
	    int x = SMPPPacket.readInt(in, 1);
	    if(x == 1)
		isSme = true;
	    else if(x == 2)
		isSme = false;
	    else
		throw new SMPPException("Input stream does not contain a "
			+ "SmeAddress struct.");
	}

	if(isSme) {
	    ton =  SMPPPacket.readInt(in, 1);
	    npi =  SMPPPacket.readInt(in, 1);
	    addr = SMPPPacket.readCString(in);
	} else {
	    addr = SMPPPacket.readCString(in);
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
    {
	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    if(isSme) {
		if(useFlag) SMPPPacket.writeInt(1, 1, b);
		SMPPPacket.writeInt((int)ton, 1, b);
		SMPPPacket.writeInt((int)npi, 1, b);
		SMPPPacket.writeCString(addr, b);
	    } else {
		// dest_flag is 2 for a distribution list
		SMPPPacket.writeInt(2, 1, b);
		SMPPPacket.writeCString(addr, b);
	    }

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing SmeAddress packet to "
		    + "output stream");
	}
    }

    /** Convert an Npi value into it's String name.
     * For example, a value of 0 for npi will return a String containing
     * "GSM_NPI_UNKNOWN"
     * @param npi The Npi value to get a name for.
     * @return The String name of this npi
     */
    public static String getNpiName(int npi)
    {
	// Make sure the tables are initialised
	if(npiTable == null && tonTable == null)
	    makeNameTables();

	if(npiTable == null)
	    return String.valueOf(npi);

	String s = (String) npiTable.get(new Integer(npi));
	if(s == null)
	    return String.valueOf(npi);
	else
	    return s;
    }

    /** Convert an Ton value into it's String name.
     * For example, a value of 0 for ton will return a String containing
     * "GSM_TON_UNKNOWN"
     * @param ton The Ton value to get a name for.
     * @return The String name of this ton
     */
    public static String getTonName(int ton)
    {
	// Make sure the tables are initialised
	if(tonTable == null && npiTable == null)
	    makeNameTables();

	if(tonTable == null)
	    return String.valueOf(ton);
	String s = (String) tonTable.get(new Integer(ton));
	if(s == null)
	    return String.valueOf(ton);
	else
	    return s;
    }

    /** Get all the names of the Numbering Plan Indicator available.
     */
    public static String[] getNpiNames()
    {
	return getNames(false);
    }

    /** Get all the names of the Type of Numbers available.
     */
    public static String[] getTonNames()
    {
	return getNames(true);
    }

    /** Get the names of either the Ton or Npis...support function
     * for getTonNames and getNpiNames
     * @param ton true to get Ton names, false for Npi names
     */
    static String[] getNames(boolean ton)
    {
	// Make sure the tables are initialised
	if(tonTable == null && npiTable == null)
	    makeNameTables();

	Hashtable table;
	if(ton)
	    table = tonTable;
	else
	    table = npiTable;

	if(table == null)
	    return null;

	String s[] = new String[table.size()];
	int cur = 0;

	Enumeration e = table.elements();
	while(e.hasMoreElements())
	    s[cur++] = (String)e.nextElement();

	return s;
    }

    /** Get the value of a Numbering Plan Indicator from it's string name
     * @return -1 If the Npi name is not recognised
     */
    public static int getNpiValue(String name)
    {
	if(name == null)
	    throw new NullPointerException();
	else
	    return getValue(name, false);
    }

    /** Get the value of a Type of Number from it's string name
     * @return -1 If the Ton name is not recognised
     */
    public static int getTonValue(String name)
    {
	if(name == null)
	    throw new NullPointerException();
	else
	    return getValue(name, true);
    }

    /** Get the value of a Ton or Npi from it's string name
     * @param name The name of the ton or npi to look up
     * @param ton true to look up Ton names, false for Npi names
     * @return -1 If the Ton or Npi name is not found
     */
    static int getValue(String name, boolean ton)
    {
	// Make sure the tables are initialised
	if(tonTable == null && npiTable == null)
	    makeNameTables();

	Hashtable table;
	if(ton)
	    table = tonTable;
	else
	    table = npiTable;

	if(table == null)
	    return -1;

	// Search through the table sequentially, if the String is found
	// in the table then it's corresponding value is returned
	Enumeration e = table.keys();
	while(e.hasMoreElements()) {
	    Integer i = (Integer)e.nextElement();
	    String s = (String)table.get(i);
	    if(s.equals(name)) {
		Debug.d(new SmeAddress(), "getValue", "name="+s+", val="+i, Debug.DBG_5);
		return i.intValue();
	    }
	}

	return -1;
    }

    /* This function uses the java.lang.reflect package to fill in 2
     * hashtables with the names of the Ton and Npi integers.  The key
     * to look up is the value of the Ton or Npi
     * This makes it easy to add in new Ton and Npi types.
     */
    static void makeNameTables()
    {
	tonTable = new Hashtable(10);
	npiTable = new Hashtable(10);

	// This is unfortunatly gonna make a little bit of recursion,
	// but only one deep...make sure the ton and npi tables are
	// not null before this is done...
	Field f[] = new SmeAddress().getClass().getFields();
	Object o = null;

	// Right then, just loop through all static fields in the class
	// and put names with _TON_ in them in the ton table, similar for npi
	for(int loop=0; loop<f.length; loop++) {
	    try {
		o = f[loop].get(null);
	    } catch(NullPointerException x) {
		continue;
	    } catch(IllegalAccessException x) {
		continue;
	    }

	    if(!(o instanceof Integer))
		continue;

	    String s = f[loop].getName();
	    if(s.indexOf("_TON_") != -1)
		tonTable.put((Integer)o, s);
	    else if(s.indexOf("_NPI") != -1)
		npiTable.put((Integer)o, s);
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
