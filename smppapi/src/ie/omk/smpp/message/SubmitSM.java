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

/** Submit a int message to the SMSC
  * @author Oran Kelly
  * @version 1.0
  */
public class SubmitSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new SubmitSM with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public SubmitSM(int seqNo)
    {
	super(ESME_SUB_SM, seqNo);
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

	if(cmdStatus != 0)
	    return;

	int smLength = 0;
	String delivery, valid;
	try {
	    // First the service type
	    serviceType = readCString(in);
	    source = new SmeAddress(in);
	    destination = new SmeAddress(in);

	    // ESM class, protocol Id, priorityFlag...
	    flags.esm_class = readInt(in, 1);
	    flags.protocol = readInt(in, 1);
	    flags.priority = (readInt(in, 1) == 0) ? false : true;

	    delivery = readCString(in);
	    valid = readCString(in);
	    deliveryTime = makeDateFromString(delivery);
	    expiryTime = makeDateFromString(valid);

	    // Registered delivery, replace if present, data coding, default msg
	    // and message length
	    flags.registered = (readInt(in, 1) == 0) ? false : true;
	    flags.replace_if_present = (readInt(in, 1) == 0) ? false : true;
	    flags.data_coding = readInt(in, 1);
	    flags.default_msg = readInt(in, 1);
	    smLength = readInt(in, 1);

	    message = readString(in, smLength);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "submit_sm packet");
	}
    }

    /** Set the source address
     * @param srcTon Source address Type of number
     * @param srcNpi Source address Numbering plan indicator
     * @param srcAddr Source address (Up to 20 characters)
     * @exception ie.omk.smpp.SMPPException If the Source address is invalid
     */
    //	public void setSource(int ton, int npi, String addr)
    //		{ super.setSource(new SmeAddress(ton, npi, addr)); }

    /** Set the source address
     * @see SmeAddress
     */
    public void setSource(SmeAddress d)
    {
	super.setSource(d);
    }

    /** Set the destination address
     * @param dstTon Destination address Type of number
     * @param dstNpi Destination address Numbering plan indicator
     * @param dstAddr Destination address (Up to 20 characters)
     * @exception ie.omk.smpp.SMPPException If the Destination address is invalid
     */
    //	public void setDestination(int ton, int npi, String addr)
    //		{ super.setDestination(new SmeAddress(ton, npi, addr)); }

    /** Set the destination address
     * @see SmeAddress
     */
    public void setDestination(SmeAddress d)
    {
	super.setDestination(d);
    }

    /** Set the service type */
    public void setServiceType(String s)
    {
	super.setServiceType(s);
    }

    /** Set the GSM Protocol Id */
    public void setProtocolId(int s)
    {
	super.setProtocolId(s);
    }

    /** Set or unset Priority delivery
     * @param b false=Regular delivery, true=Priority Delivery
     */
    public void setPriority(boolean b)
    {
	super.setPriority(b);
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
     * Registered delivery, Priority, Replace-if-present, GSM protocol,
     * GSM data encoding and default message Id.
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

    /** Set or clear the replace if present flag.
     * @param b false=Normal delivery, true=Replace if message is present
     * @exception ie.omk.smpp.SMPPException If invalid value is specified
     */
    public void setReplaceIfPresent(boolean b)
    {
	super.setReplaceIfPresent(b);
    }

    /** Set the GSM Data coding to use
     * @param s The Coding scheme to use
     */
    public void setDataCoding(int s)
    {
	super.setDataCoding(s);
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

    /** Get the service type */
    public String getServiceType()
    {
	return super.getServiceType();
    }

    /** Get the source address */
    public SmeAddress getSource()
    {
	return super.getSource();
    }

    /** Get the destination address */
    public SmeAddress getDestination()
    {
	return super.getDestination();
    }

    /** Get the Message flags structure */
    public MsgFlags getMessageFlags()
    {
	return super.getMessageFlags();
    }

    /** Get the scheduled delivery time */
    public Date getDeliveryTime()
    {
	return super.getDeliveryTime();
    }

    /** Get the expiry time */
    public Date getExpiryTime()
    {
	return super.getExpiryTime();
    }

    /** Get the text of the int message */
    public String getMessageText()
    {
	return super.getMessageText();
    }

    /** Get the GSM Protocol Id */
    public int getProtocolId()
    {
	return super.getProtocolId();
    }

    /** Check is this message priority delivery */
    public boolean isPriority()
    {
	return super.isPriority();
    }

    /** Check is this message registered delivery */
    public boolean isRegistered()
    {
	return super.isRegistered();
    }

    /** Check if this message should replace an existing message */
    public boolean isReplaceIfPresent()
    {
	return super.isReplaceIfPresent();
    }

    /** Get the GSM Data Coding scheme */
    public int getDataCoding()
    {
	return super.getDataCoding();
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


    /** Get the size in bytes of this packet */
    public int size()
    {
	return (super.size() + 11
		+ ((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.size() : 3)
		+ ((destination != null) ? destination.size() : 3)
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

	    writeCString(serviceType, b);
	    if(source != null) {
		source.writeTo(b);
	    } else {
		writeInt(0, 2, b);
		writeCString(null, b);
	    }
	    if(destination != null) {
		destination.writeTo(b);
	    } else {
		writeInt(0, 2, b);
		writeCString(null, b);
	    }
	    writeInt(flags.esm_class, 1, b);
	    writeInt(flags.protocol, 1, b);
	    writeInt(flags.priority ? 1 : 0, 1, b);
	    writeCString(makeDateString(deliveryTime), b);
	    writeCString(makeDateString(expiryTime), b);
	    writeInt(flags.registered ? 1 : 0, 1, b);
	    writeInt(flags.replace_if_present ? 1 : 0, 1, b);
	    writeInt(flags.data_coding, 1, b);
	    writeInt(flags.default_msg, 1, b);
	    writeInt(smLength, 1, b);
	    writeString(message, smLength, b);

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing bind_receiver packet to "
		    + "output stream");
	}
    }

    public String toString()
    {
	return new String("submit_sm");
    }
}
