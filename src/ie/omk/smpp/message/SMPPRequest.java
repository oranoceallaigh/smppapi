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

import java.io.InputStream;

/** Abstract parent class of all SMPP request packets.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SMPPRequest
    extends SMPPPacket
{
    /** false if this packet has been ack'd, true if it has */
    protected boolean			isAckd = false;

    /** Construct a new SMPPRequest with specified id.
      */
    public SMPPRequest(int id)
    {
	super(id);
    }

    /** Construct a new SMPPRequest with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public SMPPRequest(int id, int seqNum)
    {
	super(id, seqNum);
    }

    /** Read in a SMPPRequest from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public SMPPRequest(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);
    }

    /** Check has this request been acknowledged or not.
      */
    public final boolean isAckd()
    {
	return (isAckd);
    }

    /** Set this request packet to acknowledged.
      */
    public final void ack()
    {
	isAckd = true;
    }
}
