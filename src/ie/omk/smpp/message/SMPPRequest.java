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

import java.io.InputStream;

/** Abstract parent class of all Smpp request packets
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SMPPRequest
    extends SMPPPacket
{
    /** false if this packet has been ack'd, true if it has */
    protected boolean			isAckd = false;

    /** Construct a new SMPPRequest with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public SMPPRequest(int id, int seqNo)
    {
	super(id, seqNo);
    }

    /** Read in a SMPPRequest from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a SMPPRequest packet.
     * @see java.io.InputStream
     */
    public SMPPRequest(InputStream in)
    {
	super(in);
    }

    /** Check has this request been acknowledged or not.
     */
    public final boolean isAckd()
    {
	return isAckd;
    }

    /** Set this request packet to acknowledged.
     */
    public final void ack()
    {
	isAckd = true;
    }
}
