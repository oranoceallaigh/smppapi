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

/** ESME response to a Deliver message request.
  * @author Oran Kelly
  * @version 1.0
  */
public class DeliverSMResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Construct a new DeliverSMResp with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public DeliverSMResp(int seqNum)
    {
	super(SMSC_DELIVER_SM_RESP, seqNum);
    }


    /** Read in a DeliverSMResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public DeliverSMResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    // XXX shouldn't fail because of number format
	    messageId = Integer.parseInt(SMPPIO.readCString(in), 16);
	} catch(NumberFormatException x) {
	    Debug.d(this, "DeliverSMResp", "Error reading message id from "
		    + "Input stream", Debug.DBG_1);
	    throw new SMPPException("Error reading message Id from input "
		    + "stream");
	}

    }

    /** Create a new DeliverSMResp packet in response to a DeliverSM.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public DeliverSMResp(DeliverSM r)
    {
	super(r);
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	String id = Integer.toHexString(getMessageId());

	return (getHeaderLen() + 1
		+ ((id != null) ? id.length() : 0));
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(Integer.toHexString(getMessageId()), out);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("deliver_sm_resp");
    }
}
