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

import java.io.*;
import ie.omk.debug.Debug;


/** Do a liveness check on the SMSC-ESME connection
  * @author Oran Kelly
  * @version 1.0
  */
public class EnquireLink
	extends ie.omk.smpp.message.SMPPRequest
{
// File identifier string: used for debug output
	private static String FILE = "EnquireLink";

	/** Construct a new EnquireLink with specified sequence number.
	  * @param seqNo The sequence number to use
	  */
	public EnquireLink(int seqNo)
	{
		super(ESME_QRYLINK, seqNo);
	}

	/** Read in a EnquireLink from an InputStream.  A full packet,
	  * including the header fields must exist in the stream.
	  * @param in The InputStream to read from
	  * @exception ie.omk.smpp.SMPPException If the stream does not
	  * contain a EnquireLink packet.
	  * @see java.io.InputStream
	  */
	public EnquireLink(InputStream in)
	{
		super(in);
	}

	public String toString()
	{
		return new String("enquire_link");
	}
}

