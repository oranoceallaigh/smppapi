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

import java.io.IOException;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.BadInterfaceVersionException;
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.util.SMPPIO;
import org.apache.log4j.Logger;

/** Bind to the SMSC as a transceiver.
  * @author Oran Kelly
  * @version 1.0
  */
public class BindTransceiver
    extends ie.omk.smpp.message.Bind
{
    /** Construct a new BindTransceiver.
      * @param seqNum The sequence number to use
      */
    public BindTransceiver()
    {
	super(BIND_TRANSCEIVER);
    }

    /** Read in a BindTransceiver from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @throws java.io.IOException if there's a problem reading from the
      * input stream.
      */
    /*public BindTransceiver(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != BIND_TRANSCEIVER)
	    throw new BadCommandIDException(BIND_TRANSCEIVER,
		    getCommandId());
    }*/

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("bind_transceiver");
    }
}
