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
import ie.omk.smpp.util.SMPPDate;

import ie.omk.debug.Debug;

/** Transfer data between the SC and an ESME. This message type is used to
 * transfer data both by the SMSC and the ESME. The command can be used as a
 * replacement for both submit_sm and deliver_sm.
 * Relevant inherited fields from SMPPPacket:<br>
 * <ul>
 *   serviceType<br>
 *   source<br>
 *   destination<br>
 *   esmClass<br>
 *   registered<br>
 *   dataCoding<br>
 * </ul>
 * @author Oran Kelly
 * @version 1.0
 */
public class DataSM
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new DataSM
      */
    public DataSM()
    {
	super(DATA_SM);
    }

    /** Construct a new DataSM with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public DataSM(int seqNum)
    {
	super(DATA_SM, seqNum);
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getBodyLength()
    {
	int len = (((serviceType != null) ? serviceType.length() : 0)
		+ ((source != null) ? source.getLength() : 3)
		+ ((destination != null) ? destination.getLength() : 3));

	// 3 1-byte integers, 1 c-string
	return (len + 4);
    }


    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException If an error occurs writing to the output
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
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
	SMPPIO.writeInt(registered ? 1 : 0, 1, out);
	SMPPIO.writeInt(dataCoding, 1, out);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
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

	// Registered delivery, data coding
	registered = (SMPPIO.bytesToInt(body, offset++, 1) == 0) ? false : true;
	dataCoding = SMPPIO.bytesToInt(body, offset++, 1);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("data_sm");
    }
}
