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
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.smpp.message;

import java.io.*;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** SMSC response to a Bind request.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class BindResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** System Id */
    private String sysId = null;

    /** Construct a new BindResp.
      */
    protected BindResp(int id)
    {
	super(id);
    }

    /** Read in a BindResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public BindResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandStatus() != 0)
	    return;

	sysId = SMPPIO.readCString(in);
    }*/


    /** Create a new BindResp packet in response to a
      * Bind request. This constructor will set the sequence number to that
      * if the packet it is in response to.
      * @param req The Request packet the response is to
      */
    public BindResp(Bind req)
    {
	super(req);
    }

    /** Set the system Id
      * @param sysId The new System Id string (Up to 15 characters)
      * @exception ie.omk.smpp.StringTooLongException if the system id is too
      * long.
      */
    public void setSystemId(String sysId)
	throws ie.omk.smpp.SMPPException
    {
	if(sysId == null) {
	    this.sysId = null;
	    return;
	}

	if(sysId.length() < 16)
	    this.sysId = sysId;
	else
	    throw new StringTooLongException(15);
    }

    /** Get the system Id */
    public String getSystemId()
    {
	return (sysId);
    }


    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getBodyLength()
    {
	// Length of system ID plus a nul terminator.
	return (((sysId != null) ? sysId.length() : 0) + 1);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the output
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(sysId, out);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	sysId = SMPPIO.readCString(body, offset);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("bind_resp");
    }
}
