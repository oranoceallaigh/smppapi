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
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.BadInterfaceVersionException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** Bind to the SMSC as receiver.
  * This message is used to bind to the SMSC as a Receiver ESME.
  * @author Oran Kelly
  * @version 1.0
  */
public class BindReceiver
    extends ie.omk.smpp.message.Bind
{
    /** Constructs a new BindReceiver.
      */
    public BindReceiver()
    {
	super(BIND_RECEIVER);
    }

    /** Read a BindReceiver packet from an InputStream.  An entire packet
      * must exist in the stream, including the header information.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's a read error.
      */
    /*public BindReceiver(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.BIND_RECEIVER)
	    throw new BadCommandIDException(SMPPPacket.BIND_RECEIVER,
		    getCommandId());
    }*/

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return ("bind_receiver");
    }
}
