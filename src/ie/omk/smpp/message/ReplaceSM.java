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
import java.util.*;
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Replace an existing int message with another
  * @author Oran Kelly
  * @version 1.0
  */
public class ReplaceSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new ReplaceSM with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public ReplaceSM(int seqNo)
    {
	super(ESME_REPLACE_SM, seqNo);
    }

    /** Read in a ReplaceSM from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a ReplaceSM packet.
     * @see java.io.InputStream
     */
    public ReplaceSM(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	int smLength = 0;
	String delivery, valid;
	flags = new MsgFlags();
	try {
	    messageId = Integer.parseInt(readCString(in), 16);
	    source = new SmeAddress(in);

	    delivery = readCString(in);
	    valid = readCString(in);
	    deliveryTime = makeDateFromString(delivery);
	    expiryTime = makeDateFromString(valid);

	    flags.registered = (readInt(in, 1) == 0) ? false : true;
	    flags.default_msg = readInt(in, 1);
	    smLength = readInt(in, 1);

	    message = readString(in, smLength);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "replace_sm packet.");
	}
    }

    /** Set the message Id
     * @param messageId The message Id to use (Up to 8 chars)
     * @exception ie.omk.smpp.SMPPException If the message Id is invalid
     */
    public void setMessageId(int id)
    {
	super.setMessageId(id);
    }

    /** Set Source address to match against in SMSC
     * @param sourceTon Source Address Ton
     * @param sourceNpi Source Address Npi
     * @param sourceAddr Source Address (up to 20 characters)
     * @exception ie.omk.smpp.SMPPException If the address is invalid
     */
    public void setSource(int ton, int npi, String addr)
    {
	super.setSource(new SmeAddress(ton, npi, addr));
    }

    /** Set the source address
     * @see SmeAddress
     */
    public void setSource(SmeAddress d)
    {
	super.setSource(d);
    }

    /** Set the delivery time
     * @param d The Date to schedule delivery for (must be in UTC)
     */
    public void setDeliveryTime(Date d)
    {
	super.setDeliveryTime(d);
    }

    /** Set the expiry time for the int message
     * @param d The Date the message should expire at (must be in UTC)
     */
    public void setExpiryTime(Date d)
    {
	super.setExpiryTime(d);
    }

    /** Set the relevant message flags.  The following flags are relevant
     * for this type of packet:
     * Registered delivery and default message Id.
     * @param flags The MsgFlags structure to read from.
     */
    public void setMessageFlags(MsgFlags f)
    {
	super.setMessageFlags(f);
    }

    /** Set registered delivery.
     * @param b false=Normal delivery, true=Registered Delivery
     * @exception ie.omk.smpp.SMPPException If invalid value is specified
     */
    public void setRegistered(boolean b)
    {
	super.setRegistered(b);
    }

    /** Set the default message to send
     * @param s Default message to send, range must be 1 - 100 (0x64)
     * @exception ie.omk.smpp.SMPPException If message id is out of range
     */
    public void setDefaultMsg(int s)
    {
	super.setDefaultMsg(s);
    }

    /** Set the text of the message to send
     * @param s The text of the message.  (Up to 160 characters may be sent)
     * @exception ie.omk.smpp.SMPPException If the mesasge is too long
     */
    public void setMessageText(String s)
    {
	super.setMessageText(s);
    }

    /** Get the message Id */
    public int getMessageId()
    {
	return super.getMessageId();
    }

    /** Get the flags structure of this message */
    public MsgFlags getMessageFlags()
    {
	return super.getMessageFlags();
    }

    /** Get the source address */
    public SmeAddress getSource()
    {
	return super.getSource();
    }

    /** Get the schedule delivery time */
    public Date getDeliveryTime()
    {
	return super.getDeliveryTime();
    }

    /** Get the validity period */
    public Date getExpiryTime()
    {
	return super.getExpiryTime();
    }

    /** Check is this message registered delivery */
    public boolean isRegistered()
    {
	return super.isRegistered();
    }

    /** Get the default message Id */
    public int getDefaultMsgId()
    {
	return super.getDefaultMsgId();
    }

    /** Get the length of the text of the message */
    public int getMessageLen()
    {
	return super.getMessageLen();
    }

    /** Get the text of the int message */
    public String getMessageText()
    {
	return super.getMessageText();
    }


    /** Get the size in bytes of this packet */
    public int size()
    {
	String id = Integer.toHexString(getMessageId());

	return (super.size() + 6
		+ ((id != null) ? id.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((deliveryTime != null) ?
		    makeDateString(deliveryTime).length() : 0)
		+ ((expiryTime != null) ?
		    makeDateString(expiryTime).length() : 0)
		+ ((message != null) ? message.length() : 0));
    }

    /** Write a byte representation of this packet to an OutputStream
     * @param out The OutputStream to write to
     * @exception ie.omk.smpp.SMPPException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public void writeTo(OutputStream out)
    {
	int smLength = 0;
	if(message != null)
	    smLength = message.length();

	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    super.writeTo(b);

	    writeCString(Integer.toHexString(getMessageId()), b);
	    if(source != null) {
		source.writeTo(b);
	    } else {
		writeInt(0, 2, b);
		writeCString(null, b);
	    }

	    writeCString(makeDateString(deliveryTime), b);
	    writeCString(makeDateString(expiryTime), b);
	    writeInt(flags.registered ? 1 : 0, 1, b);
	    writeInt(flags.default_msg, 1, b);
	    writeInt(smLength, 1, b);
	    writeString(message, smLength, b);

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing replace_sm packet to "
		    + "output stream");
	}
    }

    public String toString()
    {
	return new String("replace_sm");
    }
}
