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

/** SMSC response to a BindReceiver request.
  * @author Oran Kelly
  * @version 1.0
  */
public class BindReceiverResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** System Id of the SMSC. */
    private String sysId;

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
      * @exception java.io.IOException if there's an error reading from the
      * stream.
      */
    public BindReceiverResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if(getCommandStatus() != 0)
	    return;

	sysId = SMPPIO.readCString(in);
    }

    /** Create a new BindReceiverResp packet in response to a BindReceiver.
      * This constructor will set the sequence number to that of the
      * BindReceiver message.
      * @param r The Request packet the response is to
      */
    public BindReceiverResp(BindReceiver r)
    {
	super(r);

	sysId = new String(r.getSystemId());
    }

    /** Set the system Id. The System Id in a BindReceiverResp is the system id
      * of the SMSC.
      * @param sysId The new System Id string (Up to 15 characters)
      * @exception ie.omk.smpp.SMPPException If the system Id is invalid
      */
    public void setSystemId(String sysId)
	throws ie.omk.smpp.SMPPException
    {
	if(sysId == null) {
	    this.sysId = null;
	    return;
	}

	if(sysId.length() > 15)
	    throw new SMPPException("System ID must be < 16 chars.");
	else
	    this.sysId = sysId;
    }

    /** Get the system Id */
    public String getSystemId()
    {
	return (sysId);
    }


    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	return (getHeaderLen()
		+ ((sysId != null) ? sysId.length() : 1));
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the output
      * stream.
      * @exception ie.omk.smpp.SMPPException If an I/O error occurs
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(sysId, out);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("bind_receiver_resp");
    }
}
