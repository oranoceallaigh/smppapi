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
import ie.omk.smpp.util.SMPPDate;
import ie.omk.debug.Debug;

/** Submit a message to the SMSC
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new SubmitSM with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public SubmitSM(int seqNum)
    {
	super(ESME_SUB_SM, seqNum);
    }

    /** Read in a SubmitSM from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a SubmitSM packet.
      * @see java.io.InputStream
      */
    public SubmitSM(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	int smLength = 0;
	String delivery, valid;
	try {
	    // First the service type
	    serviceType = SMPPIO.readCString(in);
	    source = new SmeAddress(in);
	    destination = new SmeAddress(in);

	    // ESM class, protocol Id, priorityFlag...
	    flags.esm_class = SMPPIO.readInt(in, 1);
	    flags.protocol = SMPPIO.readInt(in, 1);
	    flags.priority = (SMPPIO.readInt(in, 1) == 0) ? false : true;

	    delivery = SMPPIO.readCString(in);
	    valid = SMPPIO.readCString(in);
	    deliveryTime = new SMPPDate(delivery);
	    expiryTime = new SMPPDate(valid);

	    // Registered delivery, replace if present, data coding, default msg
	    // and message length
	    flags.registered = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	    flags.replace_if_present = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	    flags.data_coding = SMPPIO.readInt(in, 1);
	    flags.default_msg = SMPPIO.readInt(in, 1);
	    smLength = SMPPIO.readInt(in, 1);

	    message = SMPPIO.readString(in, smLength);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "submit_sm packet");
	}
    }

    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	return (getHeaderLen() + 11
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((destination != null) ? destination.size() : 3)
		+ ((deliveryTime != null) ?
		    deliveryTime.toString().length() : 0)
		+ ((expiryTime != null) ?
		    expiryTime.toString().length() : 0)
		+ ((message != null) ? message.length() : 0));
    }


    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception ie.omk.smpp.SMPPException If an I/O error occurs
      * @see java.io.OutputStream
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	int smLength = 0;
	if(message != null)
	    smLength = message.length();

	SMPPIO.writeCString(serviceType, out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    SMPPIO.writeInt(0, 3, out);
	}
	if(destination != null) {
	    destination.writeTo(out);
	} else {
	    SMPPIO.writeInt(0, 3, out);
	}

	String dt = (deliveryTime == null) ? "" : deliveryTime.toString();
	String et = (expiryTime == null) ? "" : expiryTime.toString();

	SMPPIO.writeInt(flags.esm_class, 1, out);
	SMPPIO.writeInt(flags.protocol, 1, out);
	SMPPIO.writeInt(flags.priority ? 1 : 0, 1, out);
	SMPPIO.writeCString(dt, out);
	SMPPIO.writeCString(et, out);
	SMPPIO.writeInt(flags.registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(flags.replace_if_present ? 1 : 0, 1, out);
	SMPPIO.writeInt(flags.data_coding, 1, out);
	SMPPIO.writeInt(flags.default_msg, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	SMPPIO.writeString(message, smLength, out);
    }

    public String toString()
    {
	return new String("submit_sm");
    }
}
