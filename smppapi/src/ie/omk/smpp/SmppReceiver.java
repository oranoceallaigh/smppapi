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

package ie.omk.smpp;

import java.io.IOException;

import ie.omk.smpp.message.AlertNotification;
import ie.omk.smpp.message.Bind;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.BindReceiverResp;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.BindTransceiver;
import ie.omk.smpp.message.BindTransceiverResp;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.message.CancelSM;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DataSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.GenericNack;
import ie.omk.smpp.message.ParamRetrieve;
import ie.omk.smpp.message.ParamRetrieveResp;
import ie.omk.smpp.message.QueryLastMsgs;
import ie.omk.smpp.message.QueryLastMsgsResp;
import ie.omk.smpp.message.QueryMsgDetails;
import ie.omk.smpp.message.QueryMsgDetailsResp;
import ie.omk.smpp.message.QuerySM;
import ie.omk.smpp.message.QuerySMResp;
import ie.omk.smpp.message.ReplaceSM;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SubmitMulti;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import ie.omk.smpp.net.SmscLink;

import ie.omk.debug.Debug;

/** Receiver implementation of the SMPP Connection.
  * @author Oran Kelly
  * @version 1.0
  * @deprecated Use the {@link Connection} parent class instead.
  */
public class SmppReceiver
    extends ie.omk.smpp.Connection
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
      * @throws java.lang.NullPointerException If the link is null
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
     * @throws java.lang.IllegalArgumentException if a bad <code>type</code>
     * value is supplied.
     * @throws ie.omk.smpp.UnsupportedOperationException if an attempt is made
     * to bind as transceiver while using SMPP version 3.3.
     * @throws ie.omk.smpp.StringTooLongException If any of systemID, password,
     * system type or address range are outside allowed bounds.
     * @throws ie.omk.smpp.InvalidTONException If the TON is invalid.
     * @throws ie.omk.smpp.InvalidNPIException If the NPI is invalid.
     * @throws java.io.IOException If an I/O error occurs while writing the bind
     * packet to the output stream.
     * @throws ie.omk.smpp.AlreadyBoundException If the Connection is already
     * bound.
     * @see ie.omk.smpp.Connection#bind
     * @deprecated see class description.
     */
    public BindResp bind(String systemID, String password,
	    String systemType, Address sourceRange)
	throws java.io.IOException, UnsupportedOperationException, StringTooLongException, InvalidTONException, InvalidNPIException, IllegalArgumentException, AlreadyBoundException
    {
	return (super.bind(
		    RECEIVER,
		    systemID,
		    password,
		    systemType,
		    sourceRange.getTON(),
		    sourceRange.getNPI(),
		    sourceRange.getAddress()));
    }
}
