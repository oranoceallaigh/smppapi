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
import ie.omk.smpp.BadCommandIDException;

/** Generic negative acknowledgment.
  * Used if the short message entity, either ESME or SMSC, does not understand a
  * message transmitted to it or if a transmitted protocol message is badly
  * formed.
  * @author Oran Kelly
  * @version 1.0
  */
public class GenericNack
    extends ie.omk.smpp.message.SMPPResponse
{
    /** Construct a new GenericNack with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public GenericNack(int seqNum)
    {
	super(ESME_NACK, seqNum);
    }

    /** Read in a GenericNack from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public GenericNack(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.ESME_NACK)
	    throw new BadCommandIDException(SMPPPacket.ESME_NACK,
		    getCommandId());
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      */
    public int getCommandLen()
    {
	return (getHeaderLen());
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("generic_nack");
    }
}
