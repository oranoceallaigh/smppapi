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
package ie.omk.smpp.message;

import java.io.*;

import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.SMPPException;

import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;

import ie.omk.debug.Debug;

/** Cancal message.
  * This SMPP message is used to cancel a previously submitted but yet
  * undelivered short message at the SMSC.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
  *   serviceType<br>
  *   messageId<br>
  *   source<br>
  *   destination<br>
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class CancelSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new CancelSM.
      */
    public CancelSM()
    {
	super(CANCEL_SM);
    }

    /** Construct a new CancelSM with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public CancelSM(int seqNum)
    {
	super(CANCEL_SM, seqNum);
    }

    /** Read in a CancelSM from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public CancelSM(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.CANCEL_SM)
	    throw new BadCommandIDException(SMPPPacket.CANCEL_SM,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	serviceType = SMPPIO.readCString(in);
	messageId = SMPPIO.readCString(in);
	source = new Address(in);
	destination = new Address(in);
    }*/

    public int getBodyLength()
    {
	int len = (((serviceType != null) ? serviceType.length() : 0)
		+ ((messageId != null) ? messageId.length() : 0)
		+ ((source != null) ? source.getLength() : 3)
		+ ((destination != null) ? destination.getLength() : 3));

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
	SMPPIO.writeCString(getMessageId(), out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    new Address(GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	}

	if(destination != null) {
	    destination.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    new Address(GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	}
    }
    public void readBodyFrom(byte[] body, int offset)
    {
	serviceType = SMPPIO.readCString(body, offset);
	offset += serviceType.length() + 1;

	messageId = SMPPIO.readCString(body, offset);
	offset += messageId.length() + 1;

	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	destination = new Address();
	destination.readFrom(body, offset);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("cancel_sm");
    }
}
