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
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

import ie.omk.debug.Debug;

/** Replace a message.
  * This message submits a short message to the SMSC replacing a previously
  * submitted message.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
  *   messageId
  *   source
  *   deliveryTime
  *   expiryTime
  *   registered
  *   defaultMsg
  *   message
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class ReplaceSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new ReplaceSM.
      */
    public ReplaceSM()
    {
	super(REPLACE_SM);
    }

    /** Construct a new ReplaceSM with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public ReplaceSM(int seqNum)
    {
	super(REPLACE_SM, seqNum);
    }

    /** Read in a ReplaceSM from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public ReplaceSM(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.REPLACE_SM)
	    throw new BadCommandIDException(SMPPPacket.REPLACE_SM,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	int smLength = 0;
	String delivery, valid;

	messageId = SMPPIO.readCString(in);
	source = new Address(in);

	delivery = SMPPIO.readCString(in);
	valid = SMPPIO.readCString(in);
	if (delivery != null)
	    deliveryTime = new SMPPDate(delivery);
	if (valid != null)
	    expiryTime = new SMPPDate(valid);

	registered = (SMPPIO.readInt(in, 1) == 0) ? false : true;
	defaultMsg = SMPPIO.readInt(in, 1);
	smLength = SMPPIO.readInt(in, 1);

	if (smLength > 0) {
	    message = new byte[smLength];
	    for (int i = 0; i < smLength; )
		i += in.read(message, i, (smLength - i));
	}
    }*/

    public int getBodyLength()
    {
	int len = (((messageId != null) ? messageId.length() : 0)
		+ ((source != null) ? source.getLength() : 3)
		+ ((deliveryTime != null) ?
		    deliveryTime.toString().length() : 0)
		+ ((expiryTime != null) ?
		    expiryTime.toString().length() : 0)
		+ ((message != null) ? message.length : 0));

	// 3 1-byte integers, 3 c-strings
	return (len + 3 + 3);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	int smLength = 0;
	if(message != null)
	    smLength = message.length;

	SMPPIO.writeCString(getMessageId(), out);
	if(source != null) {
	    source.writeTo(out);
	} else {
	    // Write ton=0(null), npi=0(null), address=\0(nul)
	    new Address(GSMConstants.GSM_TON_UNKNOWN,
		    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
	}

	String dt = (deliveryTime == null) ? null : deliveryTime.toString();
	String et = (expiryTime == null) ? null : expiryTime.toString();

	SMPPIO.writeCString(dt, out);
	SMPPIO.writeCString(et, out);
	SMPPIO.writeInt(registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(defaultMsg, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	if (message != null)
	    out.write(message);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	int smLength = 0;
	String delivery, valid;

	messageId = SMPPIO.readCString(body, offset);
	offset += messageId.length() + 1;

	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	delivery = SMPPIO.readCString(body, offset);
	offset += delivery.length() + 1;
	if (delivery.length() > 0)
	    deliveryTime = new SMPPDate(delivery);

	valid = SMPPIO.readCString(body, offset);
	offset += valid.length() + 1;
	if (valid.length() > 0)
	    expiryTime = new SMPPDate(valid);

	registered =
		(SMPPIO.bytesToInt(body, offset++, 1) == 0) ? false : true;
	defaultMsg = SMPPIO.bytesToInt(body, offset++, 1);
	smLength = SMPPIO.bytesToInt(body, offset++, 1);

	if (smLength > 0) {
	    message = new byte[smLength];
	    System.arraycopy(body, offset, message, 0, smLength);
	}
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("replace_sm");
    }
}
