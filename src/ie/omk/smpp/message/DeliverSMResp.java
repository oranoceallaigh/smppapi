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
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** ESME response to a deliver_sm message
  * @author Oran Kelly
  * @version 1.0
  */
public class DeliverSMResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Construct a new DeliverSMResp with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public DeliverSMResp(int seqNo)
    {
	super(SMSC_DELIVER_SM_RESP, seqNo);
    }


    /** Read in a DeliverSMResp from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a DeliverSMResp packet.
     * @see java.io.InputStream
     */
    public DeliverSMResp(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	try {
	    messageId = Integer.parseInt(readCString(in), 16);
	} catch(IOException x) {
	    Debug.d(this, "DeliverSMResp", "Input stream does not contain a "
		    + "deliver_sm_resp", Debug.DBG_1);
	    throw new SMPPException("Input Stream does not contain a "
		    + "deliver_sm_resp packet");
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

    /** Set the message Id
     * @param messageId The message Id to use (Up to 8 characters)
     * @exception ie.omk.smpp.SMPPException If the messageId is invalid
     */
    public void setMessageId(int id)
    {
	super.setMessageId(id);
    }

    /** Get the message Id */
    public int getMessageId()
    {
	return super.getMessageId();
    }

    /** Get the size in bytes of this packet */
    public int size()
    {
	String id = Integer.toHexString(getMessageId());

	return (super.size() + 1
		+ ((id != null) ? id.length() : 0));
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
	    super.writeTo(b);

	    writeCString(Integer.toHexString(getMessageId()), b);

	    b.writeTo(out);
	} catch(IOException x) {
	    Debug.d(this, "writeTo", "Error writing packet to output",
		    Debug.DBG_1);
	    throw new SMPPException("Error writing deliver_sm_resp packet to "
		    + "output stream");
	}
    }

    public String toString()
    {
	return new String("deliver_sm_resp");
    }
}
