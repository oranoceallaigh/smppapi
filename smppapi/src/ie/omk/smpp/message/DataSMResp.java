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
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.smpp.message;

import java.io.*;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** Response to a data_sm.
  * @author Oran Kelly
  * @version 1.0
  */
public class DataSMResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Construct a new DataSMResp.
      */
    public DataSMResp()
    {
	super(DATA_SM_RESP);
    }

    /** Construct a new DataSMResp with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public DataSMResp(int seqNum)
    {
	super(DATA_SM_RESP, seqNum);
    }

    /** Create a new DataSMResp packet in response to a DataSM.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public DataSMResp(DataSM r)
    {
	super(r);
    }

    public int getBodyLength()
    {
	return (((messageId != null) ? messageId.length() : 0) + 1);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException If an error occurs writing to the output
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(getMessageId(), out);
    }

    public void readBodyFrom(byte[] b, int offset)
    {
	messageId = SMPPIO.readCString(b, offset);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("data_sm_resp");
    }
}
