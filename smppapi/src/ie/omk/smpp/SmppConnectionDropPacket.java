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
    protected String errorMsg = null;

    /** Invented command id for a connection drop */
    public static final int CONNECTION_DROP = 0xefffffff;

    public SmppConnectionDropPacket(int seq)
    {
	super(SMPPPacket.ESME_NACK, seq);
	this.errorMsg = "";

	// Command Id should not be anything near any valid Smpp message...
	commandId = CONNECTION_DROP;

	commandStatus = 0;
    }

    public SmppConnectionDropPacket(int seq, String msg)
    {
	super(ESME_NACK, seq);

	this.errorMsg = msg;

	// Command Id should not be anything near any valid Smpp message...
	commandId = CONNECTION_DROP;
	commandStatus = 0;
    }

    public void setErrorMessage(String s)
    {
	this.errorMsg = s;
    }

    public String getErrorMessage()
    {
	return (errorMsg);
    }

    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getCommandLen()
    {
	return (getHeaderLen());
    }

    /** Invalid. An SmppConnectionDropPacket cannot be encoded to an output
      * stream as it is NOT a valid SMPP message. This method just throws an
      * SMPPException if it is called.
      */
    protected void encodeBody(java.io.OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	throw new SMPPException("Cannot encode an SmppConnectionDropPacket!");
    }
}
