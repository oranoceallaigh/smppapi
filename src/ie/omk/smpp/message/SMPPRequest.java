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
    protected SMPPRequest(int id)
    {
	super(id);
    }

    /** Construct a new SMPPRequest with specified sequence number.
      * @param seqNum The sequence number to use
      */
    protected SMPPRequest(int id, int seqNum)
    {
	super(id, seqNum);
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
