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


/** Abstract parent class of all SMPP Response packets.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SMPPResponse
    extends SMPPPacket
{
    /** Construct a new SMPPResponse with specified command id.
      */
    protected SMPPResponse(int id)
    {
	super(id);
    }

    /** Construct a new SMPPResponse with specified sequence number.
      * @param seqNum The sequence number to use
      */
    protected SMPPResponse(int id, int seqNum)
    {
	super(id, seqNum);
    }

    /** Create a new SMPPResponse packet in response to a BindReceiver.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public SMPPResponse(SMPPRequest q)
    {
	// Response value is Command ID with Msb set, sequence no. must match
	super(q.getCommandId() | 0x80000000, q.getSequenceNum());
    }

    /** Set the status of this command (header field)
      * @param s The value for the status
      */
    public void setCommandStatus(int s)
    {
	this.commandStatus = s;
    }
}
