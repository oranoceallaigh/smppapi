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
package ie.omk.smpp;

import java.io.OutputStream;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;

public class Address implements java.io.Serializable
{
    private int ton = GSMConstants.GSM_TON_UNKNOWN;

    private int npi = GSMConstants.GSM_NPI_UNKNOWN;

    private String address = "";

    public Address()
    {
    }

    public Address(int ton, int npi, String address)
    {
	this.ton = ton;
	this.npi = npi;
	this.address = address;
    }

    
    public int getTON()
    {
	return (ton);
    }

    public void setTON(int ton)
    {
	this.ton = ton;
    }

    public int getNPI()
    {
	return (npi);
    }

    public void setNPI(int npi)
    {
	this.npi = npi;
    }

    public String getAddress()
    {
	return (address);
    }

    public void setAddress(String address)
    {
	this.address = (address != null) ? address : "";
    }

    public int hashCode()
    {
	// XXX Okay, I had an algorithm for combining hash codes but I've
	// forgotten it...so the below crap is made up until I find that
	// algorithm again..
	int hc1 = new Integer(ton).hashCode();
	int hc2 = new Integer(npi).hashCode();
	int hc3 = address.hashCode();

	return (new Integer(hc1 * hc2 * hc3).hashCode());
    }

    public boolean equals(Object obj)
    {
	if (obj instanceof Address) {
	    Address a = (Address)obj;
	    return ((a.ton == ton)
		    && (a.npi == npi)
		    && (a.address.equals(address)));
	} else {
	    return (false);
	}
    }

    public int getLength()
    {
	return (3 + address.length());
    }

    public void writeTo(OutputStream out)
	throws java.io.IOException
    {
	SMPPIO.writeInt(ton, 1, out);
	SMPPIO.writeInt(npi, 1, out);
	SMPPIO.writeCString(address, out);
    }

    public void readFrom(byte[] addr, int offset)
    {
	ton = SMPPIO.bytesToInt(addr, offset++, 1);
	npi = SMPPIO.bytesToInt(addr, offset++, 1);
	address = SMPPIO.readCString(addr, offset);
    }

    public String toString()
    {
	return (Integer.toString(ton) + ":" + Integer.toString(npi) + ":"
		+ address);
    }

    /** Test driver function. This method checks that serialization and
     * deserialization of instances of this class result in byte arrays and new
     * packets of consistently the same size. It does the same as the NullTest
     * and FullTest packet test classes.
     */
    /*public static final void main(String[] args)
    {
	try {
	    System.out.println("Null test:");
	    Address a = new Address();
	    java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
	    a.writeTo(os);
	    byte[] b = os.toByteArray();

	    if (b.length == a.getLength())
		System.out.println("\tpass 1.");
	    else
		System.out.println("\tfail 1.");

	    Address a1 = new Address();
	    a1.readFrom(b, 0);

	    if (b.length == a1.getLength())
		System.out.println("\tpass 2.");
	    else
		System.out.println("\tfail 2.");
	} catch (Exception x) {
	    System.out.println("\texception:");
	    x.printStackTrace(System.out);
	}

	try {
	    System.out.println("\nFilled test:");
	    Address a = new Address(2, 2, "4745879345");
	    java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
	    a.writeTo(os);
	    byte[] b = os.toByteArray();

	    if (b.length == a.getLength())
		System.out.println("\tpass 1.");
	    else
		System.out.println("\tfail 1.");

	    Address a1 = new Address();
	    a1.readFrom(b, 0);

	    if (b.length == a1.getLength())
		System.out.println("\tpass 2.");
	    else
		System.out.println("\tfail 2.");
	} catch (Exception x) {
	    System.out.println("\texception:");
	    x.printStackTrace(System.out);
	}
    }*/
}
