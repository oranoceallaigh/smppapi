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
import ie.omk.debug.Debug;


/** Check the link status.
  * This message can originate from either an ESME or the SMSC. It is used to
  * check that the entity at the other end of the link is still alive and
  * responding to messages. Usually used by the SMSC after a period of
  * inactivity to decide whether to close the link.
  * @author Oran Kelly
  * @version 1.0
  */
public class EnquireLink
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Construct a new EnquireLink with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public EnquireLink(int seqNum)
    {
	super(ESME_QRYLINK, seqNum);
    }

    /** Read in a EnquireLink from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public EnquireLink(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.ESME_QRYLINK)
	    throw new BadCommandIDException(SMPPPacket.ESME_QRYLINK,
		    getCommandId());
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
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
	return new String("enquire_link");
    }
}
