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
package ie.omk.smpp;

import ie.omk.smpp.message.SMPPPacket;
import ie.omk.debug.Debug;

/** Dummy packet used to notify Observers of an dropped network connection.
  * An instance of this class is sent by the SmppConnection listener thread
  * to any registered observers if the network connection to the Smsc
  * is unexpectedly dropped.  The listener thread will exit after nofitying
  * the observers, each of which should then do any required clean-up
  * gracefully.
  * @author Oran Kelly
  * @version 1.0
  */
public class SmppConnectionDropPacket
	extends SMPPPacket
{
// File identifier string: used for debug output
	private static String FILE = "SmppConnectionDropPacket";

	protected String message = null;
	public static final int CONNECTION_DROP		= 0xefffffff;

	public SmppConnectionDropPacket(int seq)
	{
		super(SMPPPacket.ESME_NACK, seq);
		this.message = "";

		// Command Id should not be anything near any valid Smpp message...
		cmdId = CONNECTION_DROP;

		cmdStatus = 0;
		cmdLen = 16;
	}

	public SmppConnectionDropPacket(int seq, String msg)
	{
		super(ESME_NACK, seq);

		this.message = msg;

		// Command Id should not be anything near any valid Smpp message...
		cmdId = CONNECTION_DROP;

		cmdStatus = 0;
		cmdLen = 16;
	}

	public void setMessage(String s)
	{
		this.message = s;
	}

	public String getMessage()
	{
		return message;
	}
}

