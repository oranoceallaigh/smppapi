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

/** SMSC Response to a bind receiver request
  * @author Oran Kelly
  * @version 1.0
  */
public class BindReceiverResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** System Id */
    String 				sysId;

    /** Construct a new BindReceiverResp with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public BindReceiverResp(int seqNum)
    {
	super(ESME_BNDRCV_RESP, seqNum);
	sysId = null;
    }

    /** Read in a BindReceiverResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a BindReceiverResp packet.
      * @see java.io.InputStream
      */
    public BindReceiverResp(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    sysId = SMPPIO.readCString(in);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "bind_receiver_resp packet");
	}
    }

    /** Create a new BindReceiverResp packet in response to a BindReceiver.
      * This constructor will set the sequence number and system Id
      * to their expected values.
      * @param r The Request packet the response is to
      */
    public BindReceiverResp(BindReceiver r)
    {
	super(r);

	sysId = new String(r.getSystemId());
    }

    /** Set the system Id
      * @param sysId The new System Id string (Up to 15 characters)
      * @exception ie.omk.smpp.SMPPException If the system Id is invalid
      */
    public void setSystemId(String sysId)
    {
	if(sysId == null) {
	    sysId = null;
	    return;
	}

	if(sysId.length() > 15)
	    throw new SMPPException("System ID must be < 16 chars.");
	else
	    sysId = new String(sysId);
    }

    /** Get the system Id */
    public String getSystemId()
    {
	return (sysId == null) ? null : new String(sysId);
    }


    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	// Calculated as the size of the header plus 1 null-terminator
	// for the string plus the length of the string
	return (getHeaderLen() + 1
		+ ((sysId != null) ? sysId.length() : 0));
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
    }

    public String toString()
    {
	return new String("bind_receiver_resp");
    }
}
