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
package ie.omk.smpp.net;

import java.io.*;
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Implmentation of the Smsc link over the x.25 network protocol.
  * This class has not yet been implemented seeing as there are no
  * x.25 libraries for Java!
  * @author Oran Kelly
  * @version 0.0
  */
public class X25Link
	extends ie.omk.smpp.net.SmscLink
{
// File identifier string: used for debug output
	private static String FILE = "X25Link";

	public X25Link(String address)
	{
		throw new SMPPException("No x.25 networking module implemented yet!");
	}

	public X25Link(String address, int port)
	{
		throw new SMPPException("No x.25 networking module implemented yet!");
	}

	/** Does nothing */
	public void open()
		throws IOException
	{
		throw new SMPPException("No x.25 networking module implemented yet!");
	}

	/** Does nothing */
	public void close()
		throws IOException
	{
		throw new SMPPException("No x.25 networking module implemented yet!");
	}

	/** Does nothing */
	public OutputStream getOutputStream()
		throws IOException
	{
		throw new SMPPException("No x.25 networking module implemented yet!");
	}
	
	/** Does nothing */
	public InputStream getInputStream()
		throws IOException
	{
		throw new SMPPException("No x.25 networking module implemented yet!");
	}

	/** Does nothing */
	public boolean isConnected()
	{
		return false;
	}
}

