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

import java.io.InputStream;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.debug.Debug;

/** Unbind from the SMSC. This operation does not close the network
  * connection...it is valid to issue a new bind command over the same network
  * connection to re-establish SMPP communication with the SMSC.
  * @author Oran Kelly
  * @version 1.0
  */
public class Unbind
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new Unbind.
      */
    public Unbind()
    {
	super(UNBIND);
    }

    /** Construct a new Unbind with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public Unbind(int seqNum)
    {
	super(UNBIND, seqNum);
    }

    /** Read in a Unbind from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException If an error occurs writing to the input
      * stream.
      */
    public Unbind(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.UNBIND)
	    throw new BadCommandIDException(SMPPPacket.UNBIND,
		    getCommandId());
    }

    public int getBodyLength()
    {
	return (0);
    }

    public void readBodyFrom(byte[] b, int offset)
    {
	return;
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("unbind");
    }
}
