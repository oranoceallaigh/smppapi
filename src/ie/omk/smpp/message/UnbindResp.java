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

/** Confirm an unbind request
  * @author Oran Kelly
  * @version 1.0
  */
public class UnbindResp
	extends ie.omk.smpp.message.SMPPResponse
{
// File identifier string: used for debug output
	private static String FILE = "UnbindResp";

	/** Construct a new UnbindResp with specified sequence number.
	  * @param seqNo The sequence number to use
	  */
	public UnbindResp(int seqNo)
	{
		super(ESME_UBD_RESP, seqNo);
	}

	/** Read in a UnbindResp from an InputStream.  A full packet,
	  * including the header fields must exist in the stream.
	  * @param in The InputStream to read from
	  * @exception ie.omk.smpp.SMPPException If the stream does not
	  * contain a UnbindResp packet.
	  * @see java.io.InputStream
	  */
	public UnbindResp(InputStream in)
	{
		super(in);
	}

	/** Create a new UnbindResp packet in response to a BindReceiver.
	  * This constructor will set the sequence number to it's expected value.
	  * @param r The Request packet the response is to
	  */
	public UnbindResp(Unbind r)
	{
		super(r);
	}

	public String toString()
	{
		return new String("unbind_resp");
	}
}

