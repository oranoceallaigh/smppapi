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

/** Cancal message.
  * This SMPP message is used to cancel a previously submitted but yet
  * undelivered short message at the SMSC.
  * @author Oran Kelly
  * @version 1.0
  */
public class CancelSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new CancelSM with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public CancelSM(int seqNum)
    {
	super(ESME_CANCEL_SM, seqNum);
    }

    /** Read in a CancelSM from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public CancelSM(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    serviceType = SMPPIO.readCString(in);
	    // XXX shouldn't fail because of number format.
	    messageId = Integer.parseInt(SMPPIO.readCString(in), 16);
	    source = new SmeAddress(in);
	    destination = new SmeAddress(in);
	} catch(NumberFormatException x) {
	    throw new SMPPException("Error parsing the message Id field from "
		    + "stream.");
	}
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	String id = Integer.toHexString(getMessageId());

	int len = (getHeaderLen()
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((id != null) ? id.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((destination != null) ? destination.size() : 3));

	// 2 c-strings
	return (len + 2);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the output
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(serviceType, out);
	SMPPIO.writeCString(Integer.toHexString(getMessageId()), out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    SMPPIO.writeInt(0, 3, out);
	}

	if(destination != null) {
	    destination.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    SMPPIO.writeInt(0, 3, out);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("cancel_sm");
    }
}
