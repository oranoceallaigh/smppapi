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


/** Deliver message.
  * This message is sent from the SMSC to a Receiver ESME to deliver a short
  * message. It is also used to notify an ESME that submitted a message using
  * registered delivery that a message has reached it's end point successfully.
  * Relevant inherited fields from SMPPPacket:<br>
  * <ul>
	serviceType<br>
	source<br>
	destination<br>
	esmClass<br>
	protocolID<br>
	priority<br>
	deliveryTime<br>
	expiryTime<br>
	registered<br>
	replaceIfPresent<br>
	dataCoding<br>
	defaultMsg<br>
	message<br>
  * </ul>
  * @author Oran Kelly
  * @version 1.0
  */
public class DeliverSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new DeliverSM.
      */
    public DeliverSM()
    {
	super(DELIVER_SM);
    }

    /** Construct a new DeliverSM with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public DeliverSM(int seqNum)
    {
	super(DELIVER_SM, seqNum);
    }

    /** Read in a DeliverSM from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public DeliverSM(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.DELIVER_SM)
	    throw new BadCommandIDException(SMPPPacket.DELIVER_SM,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	int smLength = 0;
	String delivery, valid;

	// First the service type
	serviceType = SMPPIO.readCString(in);

	// Get the source address
	source = new Address(in);

	// Get the destination address
	destination = new Address(in);

	// ESM class, protocol Id, priorityFlag...
	esmClass = SMPPIO.readInt(in, 1);
	protocolID = SMPPIO.readInt(in, 1);
	priority = SMPPIO.readInt(in, 1);

	// These should both just be nul bytes...
	delivery = SMPPIO.readCString(in);
	valid = SMPPIO.readCString(in);

	// Registered delivery, replace if present, data coding, default msg
	// and message length
	registered = (SMPPIO.readInt(in, 1) == 0 ? false : true);
	replaceIfPresent = (SMPPIO.readInt(in, 1) == 0 ? false : true);
	dataCoding = SMPPIO.readInt(in, 1);
	defaultMsg = SMPPIO.readInt(in, 1);
	smLength = SMPPIO.readInt(in, 1);

	if (smLength > 0) {
	    message = new byte[smLength];
	    for (int i = 0; i < smLength; )
		i += in.read(message, i, (smLength - i));
	}
    }*/

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getBodyLength()
    {
	int len = (((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.getLength() : 3)
		+ ((destination != null) ? destination.getLength() : 3)
		+ ((message != null) ? message.length : 0));

	// 8 1-byte integers, 3 c-strings
	return (len + 8 + 3);
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

	SMPPIO.writeCString(serviceType, out);

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
	SMPPIO.writeInt(esmClass, 1, out);
	SMPPIO.writeInt(protocolID, 1, out);
	SMPPIO.writeInt(priority, 1, out);

	// Delivery time, expiry time both null fields
	SMPPIO.writeCString(null, out);
	SMPPIO.writeCString(null, out);

	SMPPIO.writeInt((registered ? 1 : 0), 1, out);
	SMPPIO.writeInt((replaceIfPresent) ? 1 : 0, 1, out);
	SMPPIO.writeInt(dataCoding, 1, out);
	SMPPIO.writeInt(defaultMsg, 1, out);
	SMPPIO.writeInt(smLength, 1, out);
	if (message != null)
	    out.write(message);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	int smLength = 0;
	String delivery, valid;

	// First the service type
	serviceType = SMPPIO.readCString(body, offset);
	offset += serviceType.length() + 1;

	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	destination = new Address();
	destination.readFrom(body, offset);
	offset += destination.getLength();

	// ESM class, protocol Id, priorityFlag...
	esmClass = SMPPIO.bytesToInt(body, offset++, 1);
	protocolID = SMPPIO.bytesToInt(body, offset++, 1);
	priority = SMPPIO.bytesToInt(body, offset++, 1);

	delivery = SMPPIO.readCString(body, offset);
	offset += delivery.length() + 1;
	if (delivery.length() > 0)
	    deliveryTime = new SMPPDate(delivery);

	valid = SMPPIO.readCString(body, offset);
	offset += valid.length() + 1;
	if (valid.length() > 0)
	    expiryTime = new SMPPDate(valid);

	// Registered delivery, replace if present, data coding, default msg
	// and message length
	registered =
		(SMPPIO.bytesToInt(body, offset++, 1) == 0) ? false : true;
	replaceIfPresent =
		(SMPPIO.bytesToInt(body, offset++, 1) == 0) ? false : true;
	dataCoding = SMPPIO.bytesToInt(body, offset++, 1);
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
	if(esmClass == 4 || esmClass == 16)
	    return new String("delivery receipt");
	else
	    return new String("deliver_sm");
    }
}
