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

/*
 * Receiver implementation of the SMPP API
 */
package ie.omk.smpp;

import java.io.*;
import java.util.*;
import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
import ie.omk.debug.Debug;

/** smpp implementation of the Receiver smpp connection.
  * @author Oran Kelly
  * @version 1.0
  */
public class SmppReceiver
    extends ie.omk.smpp.SmppConnection
{
    /** Create a new smpp Receiver connection
     * @param link The network link to the Smsc
     */
    public SmppReceiver(SmscLink link)
    {
	super(link);
    }


    /** Bind to the SMSC as a receiver.
     * @return true if the bind is successful, false otherwise
     * @exception SMPPException If we are already bound to the SMSC, or the
     * necessary fields aren't filled in.
     * @exception java.io.IOException If a network error occurs.
     * @see SmppConnection#bind
     * @see SmppTransmitter#bind
     */
    public boolean bind()
	throws java.io.IOException
    {
	// Make sure we're not already bound
	if(bound)
	    throw new SMPPException("Already bound to SMSC as Receiver.");

	// Check the required fields are filled in
	if(sysId == null)
	    throw new SMPPException("Need a system Id to bind as.");
	if(password == null)
	    throw new SMPPException("Need a password to authenticate.");
	if(sysType == null)
	    throw new SMPPException("Need a system type to identify as.");

	if(!link.isConnected())
	    link.open();
	in = link.getInputStream();
	out = link.getOutputStream();

	BindReceiver t = new BindReceiver(nextPacket());
	t.setSystemId(this.sysId);
	t.setPassword(this.password);
	t.setSystemType(this.sysType);
	t.setInterfaceVersion(INTERFACE_VERSION);
	t.setAddressTon(this.addrTon);
	t.setAddressNpi(this.addrNpi);
	t.setAddressRange(this.addrRange);

	// Start the listener thread on the incoming socket...
	rcvThread.start();

	sendRequest(t);
	Debug.d(this, "bind", "Request sent", Debug.DBG_3);
	return true;
    }

    /** Acknowledge a DeliverSM command received from the Smsc. */
    public void ackDeliverSm(DeliverSM rq)
	throws java.io.IOException
    {
	DeliverSMResp rsp = new DeliverSMResp(rq);
	sendResponse(rsp);
	Debug.d(this, "ackDeliverSM", "Response sent", Debug.DBG_3);
    }

    /** Unbind from the SMSC.  This method stops the listener thread
     * and calls SmppConnection.unbind()
     * @return true if the unbind is successful, false otherwise
     * @exception java.io.IOException If a network error occurs
     */
    /*	public boolean unbind()
	throws java.io.IOException
	{
    // Stop the thread listening on the incoming socket...
    if(listener != null)
    listener.stop();

    return super.unbind();
    }*/
}

