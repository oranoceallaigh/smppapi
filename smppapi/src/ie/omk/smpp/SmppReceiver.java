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

package ie.omk.smpp;

import java.io.*;
import java.util.*;
import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
import ie.omk.debug.Debug;

/** Receiver implementation of the SMPP Connection.
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

    /** Create a new Smpp receiver specifying the type of communications
      * desired.
      * @param link The network link object to the Smsc (cannot be null)
      * @param async true for asyncronous communication, false for synchronous.
      * @exception java.lang.NullPointerException If the link is null
      */
    public SmppReceiver(SmscLink link, boolean async)
    {
	super(link, async);
    }

    /** Bind to the SMSC as a receiver. This method will send a bind_receiver
      * packet to the SMSC. If the network connection to the SMSC is not already
      * open, it will be opened in this method.
      * @param systemID The system ID of this ESME.
      * @param password The password used to authenticate to the SMSC.
      * @param systemType The system type of this ESME.
      * @param sourceRange The source routing information. If null, the defaults
      * at the SMSC will be used.
      * @return The bind receiver response or null if asynchronous
      * communication is used.
      * @exception ie.omk.smpp.AlreadyBoundException If the connection is
      * already bound to the SMSC.
      * @exception java.io.IOException If a network error occurs.
      * @see ie.omk.smpp.SmppConnection#bind
      */
    public BindResp bind(String systemID, String password,
	    String systemType, SmeAddress sourceRange)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	return (super.bind(systemID, password, systemType, sourceRange, false));
    }

    /** Acknowledge a DeliverSM command received from the Smsc. */
    public void ackDeliverSm(DeliverSM rq)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	DeliverSMResp rsp = new DeliverSMResp(rq);
	sendResponse(rsp);
	Debug.d(this, "ackDeliverSM", "deliver_sm_resp sent", 3);
    }
}
