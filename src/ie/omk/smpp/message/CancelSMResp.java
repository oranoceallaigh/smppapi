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
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;


/** SMSC response to a cancel message request
  * @author Oran Kelly
  * @version 1.0
  */
public class CancelSMResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Construct a new CancelSMResp with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public CancelSMResp(int seqNum)
    {
	super(ESME_CANCEL_SM_RESP, seqNum);
    }

    /** Read in a CancelSMResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a CancelSMResp packet.
      * @see java.io.InputStream
      */
    public CancelSMResp(InputStream in)
    {
	super(in);
    }

    /** Create a new CancelSMResp packet in response to a CancelSM.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public CancelSMResp(CancelSM r)
    {
	super(r);
    }

    public int getCommandLen()
    {
	return (getHeaderLen());
    }

    public String toString()
    {
	return new String("cancel_sm_resp");
    }
}
