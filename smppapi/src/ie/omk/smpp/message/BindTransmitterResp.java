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
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.util.SMPPIO;
import org.apache.log4j.Logger;

/** SMSC response to a BindTransmitter request.
  * @author Oran Kelly
  * @version 1.0
  */
public class BindTransmitterResp
    extends ie.omk.smpp.message.BindResp
{
    /** Construct a new BindTransmitterResp.
      */
    public BindTransmitterResp()
    {
	super(BIND_TRANSMITTER_RESP);
    }

    /** Read in a BindTransmitterResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @throws java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public BindTransmitterResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.BIND_TRANSMITTER_RESP)
	    throw new BadCommandIDException(SMPPPacket.BIND_TRANSMITTER_RESP,
		    getCommandId());
    }*/


    /** Create a new BindTransmitterResp packet in response to a
      * BindTransmitter. This constructor will set the sequence number to that
      * of the packet it is in response to.
      * @param r The Request packet the response is to
      */
    public BindTransmitterResp(BindTransmitter r)
    {
	super(r);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("bind_transmitter_resp");
    }
}
