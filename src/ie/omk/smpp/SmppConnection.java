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
import java.net.*;
import java.util.*;

import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.ReceiverStartEvent;

import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
import ie.omk.smpp.util.*;
import ie.omk.debug.Debug;

/** SMPP client connection (ESME).
  * This is an abstract class which provides the base functionality for
  * SmppTransmitter and SmppReceiver.
  * @see ie.omk.smpp.SmppTransmitter
  * @see ie.omk.smpp.SmppReceiver
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SmppConnection
    implements java.lang.Runnable
{
    /** Connection state: not bound to the SMSC. */
    public static final int	UNBOUND = 0;

    /** Connection state: waiting for successful acknowledgement to bind
      * request.
      */
    public static final int	BINDING = 1;

    /** Connection state: bound to the SMSC. */
    public static final int	BOUND = 2;

    /** Connection state: waiting for successful acknowledgement to unbind
      * request or waiting for application to respond to unbind request.
      */
    public static final int	UNBINDING = 3;

    /** Packet listener thread for Asyncronous comms. */
    private Thread		rcvThread = null;

    /** The first ConnectionObserver added to this object. */
    private ConnectionObserver	singleObserver = null;

    /** The list of ConnectionObservers on this object. This list does
     * <b>NOT</b> include the first observer referenced by
     * <code>singleObserver</code>.
     */
    private ArrayList		observers = null;

    /** Byte buffer used in readNextPacketInternal. */
    private byte[]		buf = new byte[300];

    /** Sequence number */
    private int			seqNum = 1;

    /** Sequence numbering lock object. */
    private Object		seqNumLock = new Object();

    /** The network link (virtual circuit) to the SMSC */
    private SmscLink		link = null;

    /** SMPP protocol version number. This may be negotiated by the bind
     * operation.
     */
    protected int		interfaceVersion = 0x33;

    /** Current state of the SMPP connection.
      * Possible states are UNBOUND, BINDING, BOUND and UNBINDING.
      */
    private int			state = UNBOUND;

    /** Specify whether the listener thread will automatically ack
      * enquire_link primitives received from the Smsc
      */
    protected boolean		ackQryLinks = true;

    /** Automatically acknowledge incoming deliver_sm messages.
      * Only valid for the Receiver
      */
    protected boolean		ackDeliverSm = false;

    /** Is the user using synchronous are async communication?. */
    protected boolean		asyncComms = false;

    /** Create a new Smpp connection.
      * @param link The network link object to the Smsc (cannot be null)
      * @exception java.lang.NullPointerException If the link is null
      */
    protected SmppConnection(SmscLink link)
    {
	if(link == null)
	    throw new NullPointerException("Smsc Link cannot be null.");

	this.link = link;
    }

    /** Create a new Smpp connection specifying the type of communications
      * desired.
      * @param link The network link object to the Smsc (cannot be null)
      * @param async true for asyncronous communication, false for synchronous.
      * @exception java.lang.NullPointerException If the link is null
      */
    protected SmppConnection(SmscLink link, boolean async)
    {
	this(link);
	this.asyncComms = async;

	if (asyncComms) {
	    this.observers = new ArrayList();
	    createRecvThread();
	}
    }

    /** Create the receiver thread if asynchronous communications is on, does
     * nothing otherwise.
     */
    private void createRecvThread()
    {
	rcvThread = new Thread(this);
	rcvThread.setDaemon(true);
    }

    /** Set the state of this ESME.
      * @see ie.omk.smpp.SmppConnection#getState
      */
    private synchronized void setState(int state)
    {
	this.state = state;
    }

    /** Get the current state of the ESME. One of UNBOUND, BINDING, BOUND or
      * UNBINDING.
      */
    public synchronized int getState()
    {
	return (this.state);
    }

    /** Method to open the link to the SMSC.
      * @exception java.io.IOException if an i/o error occurs.
      */
    protected void openLink()
	throws java.io.IOException
    {
	if (!this.link.isConnected()) {
	    Debug.d(this, "openLink", "Opening network link.", 1);
	    this.link.open();
	}
    }


    /** Get the interface version value as an integer. The version is encoded
     * as a hexadecimal integer. {@link #setInterfaceVersion(int)} describes
     * the current accepted values.
     * @see #setInterfaceVersion(int)
     */
    public int getInterfaceVersion()
    {
	return (this.interfaceVersion);
    }

    /** Set the desired interface version for this connection. The default
     * version is 3.3 (XXX soon to be 3.4). The bind operation may negotiate
     * an eariler version of the protocol if the SC does not understand the
     * version sent by the ESME. This API will not support any version eariler
     * than SMPP v3.3. The interface version is encoded as follows:
     * <table border="1" cellspacing="1" cellpadding="1">
     *   <tr><th>SMPP version</th><th>Version value</th></tr>
     *   <tr><td>v3.4</td><td>0x34</td></tr>
     *   <tr><td>v3.3</td><td>0x33</td></tr>
     *   <tr>
     *     <td colspan="2" align="center"><i>All other values reserved.</i></td>
     *   </tr>
     * </table>
     * @exception UnsupportedSMPPVersionException if <code>version</code> is
     * unsupported by this implementation.
     */
    public void setInterfaceVersion(int version)
	throws ie.omk.smpp.UnsupportedSMPPVersionException
    {
	if (version != 0x33 || version != 0x34)
	    throw new UnsupportedSMPPVersionException(version);
	else
	    this.interfaceVersion = version;
    }


    /** Set the behaviour of automatically acking QRYLINK's from the SMSC.
      * By default, the listener thread will automatically ack an enquire_link
      * message from the Smsc so as not to lose the connection.  This
      * can be turned off with this method.
      * @param true to activate automatic acknowledgment, false to disable
      */
    public void autoAckLink(boolean b)
    {
	this.ackQryLinks = b;
    }

    /** Set the behaviour of automatically acking Deliver_Sm's from the Smsc.
      * By default the listener thread will <b>not</b> acknowledge a message.
      * @param true to activate this function, false to deactivate.
      */
    public void autoAckMessages(boolean b)
    {
	this.ackDeliverSm = b;
    }

    /** Check is this connection automatically acking Enquire link requests.
      */
    public boolean isAckingLinks()
    {
	return (ackQryLinks);
    }

    /** Check is this connection automatically acking delivered messages
      */
    public boolean isAckingMessages()
    {
	return (ackDeliverSm);
    }

    /** Send an smpp request to the SMSC.
      * No fields in the SMPPRequest packet will be altered except for the
      * sequence number. The sequence number of the packet will be set by this
      * method according to the numbering maintained by this SmppConnection
      * object. The numbering policy is to start at 1 and increment by 1 for
      * each packet sent.
      * @param r The request packet to send to the SMSC
      * @return The response packet returned by the SMSC, or null if
      * asynchronous communication is being used.
      * @exception java.io.IOException If a network error occurs
      */
    public SMPPResponse sendRequest(SMPPRequest r)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if (link == null)
	    throw new IOException("Connection to the SMSC is not open.");

	SMPPPacket resp = null;

	r.setSequenceNum(nextPacket());

	// Special packet handling..
	int id = r.getCommandId();
	if (id == SMPPPacket.ESME_BNDTRN || id == SMPPPacket.ESME_BNDRCV) {
	    if (this.state != UNBOUND)
		throw new AlreadyBoundException();

	    openLink();

	    setState(BINDING);
	    if (asyncComms) {
		if (rcvThread == null)
		    createRecvThread();

		if (!rcvThread.isAlive())
		    rcvThread.start();
	    }
	}

	id = -1;
	link.write(r);

	if (!asyncComms) {
	    resp = readNextPacketInternal();
	    id = resp.getCommandId();
	    if(!(resp instanceof SMPPResponse)) {
		Debug.d(this, "sendRequest", "packet received from "
		    + "SMSC is not an SMPPResponse!", 1);
	    }
	}

	// Special!
	if (id == SMPPPacket.ESME_BNDTRN_RESP
		|| id == SMPPPacket.ESME_BNDRCV_RESP) {
	    if (resp.getCommandStatus() == 0)
		setState(BOUND);
	}

	return ((SMPPResponse)resp);
    }

    /** Send an smpp response packet to the SMSC
      * @param r The response packet to send to the SMSC
      * @exception ie.omk.smpp.NoSuchRequestException if the response contains a
      * sequence number of a request this connection has not seen.
      * @exception java.io.IOException If a network error occurs
      */
    public void sendResponse(SMPPResponse resp)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	Integer key = null;

	if (link == null)
	    throw new IOException("Connection to SMSC is not valid.");

	link.write(resp);

	if (resp.getCommandId() == SMPPPacket.ESME_UBD_RESP
		&& resp.getCommandStatus() == 0)
	    setState(UNBOUND);
    }

    /** bind this connection to the SMSC.
      * @param systemID The system ID of this ESME.
      * @param password The password used to authenticate to the SMSC.
      * @param systemType The system type of this ESME.
      * @param sourceRange The source routing information. If null, the defaults
      * at the SMSC will be used.
      * @param transmitter true to bind as transmitter, false to bind as
      * receiver.
      * @return The bind transmitter or bind receiver response or null if
      * asynchronous communications is in use.
      * @exception java.io.IOException If a communications error occurs
      * @exception ie.omk.smpp.SMPPException XXX when?
      * @see ie.omk.smpp.SmppTransmitter#bind
      * @see ie.omk.smpp.SmppReceiver#bind
      */
    public BindResp bind(String systemID, String password,
	    String systemType, SmeAddress sourceRange, boolean transmitter)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	Bind bindReq = null;

	// Make sure we're not already bound
	if (state != UNBOUND)
	    throw new AlreadyBoundException();

	// Open the network connection if necessary
	openLink();

	if (transmitter)
	    bindReq = new BindTransmitter();
	else
	    bindReq = new BindReceiver();

	bindReq.setSystemId(systemID);
	bindReq.setPassword(password);
	bindReq.setSystemType(systemType);
	bindReq.setInterfaceVersion(interfaceVersion);
	if (sourceRange != null) {
	    bindReq.setAddressTon(sourceRange.getTON());
	    bindReq.setAddressNpi(sourceRange.getNPI());
	    bindReq.setAddressRange(sourceRange.getAddress());
	}

	Debug.d(this, "bind", "bind request sent", 3);

	return ((BindResp)sendRequest(bindReq));
    }


    /** Unbind from the SMSC. This method constructs and sends an unbind request
      * packet to the SMSC.
      * @return The Unbind response packet, or null if asynchronous
      * communication is being used.
      * @exception ie.omk.smpp.NotBoundException if the connection is not yet
      * bound.
      * @exception java.io.IOException If a network error occurs.
      * @see ie.omk.smpp.SmppTransmitter#bind
      * @see ie.omk.smpp.SmppReceiver#bind
      */
    public UnbindResp unbind()
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if((state != BOUND) || !(link.isConnected()))
	    throw new NotBoundException();

	/* If this is set, the run() method will return when an
	 * unbind response packet arrives, stopping the listener
	 * thread. (after all observers have been notified of the packet)
	 */
	setState(UNBINDING);

	Unbind u = new Unbind();
	SMPPResponse resp = sendRequest(u);
	if (!asyncComms) {
	    if (resp.getCommandId() == SMPPPacket.ESME_UBD_RESP
		    && resp.getCommandStatus() == 0)
		setState(UNBOUND);
	}
	return ((UnbindResp)resp);
    }

    /** Unbind from the SMSC. This method can be used to acknowledge an unbind
      * request from the SMSC.
      * @exception ie.omk.smpp.NotBoundException if the link is currently not
      * connected.
      * @exception ie.omk.smpp.AlreadyBoundException if no unbind request has
      * been received from the SMSC.
      * @exception java.io.IOException If a network error occurs.
      * @see ie.omk.smpp.SmppTransmitter#bind
      * @see ie.omk.smpp.SmppReceiver#bind
      */
    public void unbind(UnbindResp ubr)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if (state != UNBINDING)
	    throw new NotBoundException("Link is not connected.");

	if (!(link.isConnected()))
	    throw new AlreadyBoundException("No unbind request received.");

	sendResponse(ubr);
    }

    /** Use of this <b><i>highly</i></b> discouraged.
      * This is in case of emergency and stuff.
      * Closing the connection to the Smsc without unbinding
      * first can cause horrific trouble with runaway processes.  Don't
      * do it!
      */
    public void force_unbind()
	throws ie.omk.smpp.SMPPException
    {
	if(state != UNBINDING) {
	    Debug.warn(this, "force_close",
		    "Force tried before normal unbind.");
	    throw new AlreadyBoundException("Try a normal unbind first.");
	}

	Debug.d(this, "force_unbind",
		"Attempting to force the connection shut.", 4);
	try {
	    // The thread must DIE!!!!
	    if(rcvThread != null && rcvThread.isAlive()) {
		setState(UNBOUND);
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException x) {
		}
		if (rcvThread.isAlive())
		    Debug.warn(this, "force_unbind",
			    "ERROR! Listener thread has not died.");
	    }

	    link.close();
	} catch(IOException ix) {
	}
	return;
    }


    /** Acknowledge an EnquireLink received from the Smsc
      * @exception java.io.IOException If a communications error occurs.
      * @exception ie.omk.smpp.SMPPExceptione XXX when?
      */
    public void ackEnquireLink(EnquireLink rq)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	EnquireLinkResp resp = new EnquireLinkResp(rq);
	sendResponse(resp);
	Debug.d(this, "ackEnquireLink", "responding..", 3);
    }

    /** Do a confidence check on the SMPP link to the SMSC.
      * @return The Enquire link response packet or null if asynchronous
      * communication is in use.
      * @exception java.io.IOException If a network error occurs
      * @exception ie.omk.smpp.SMPPExceptione XXX when?
      */
    public EnquireLinkResp enquireLink()
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	EnquireLink s = new EnquireLink();
	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "enquireLink", "sent enquire_link", 3);
	if (resp != null)
	    Debug.d(this, "enquireLink", "response received", 3);

	return ((EnquireLinkResp)resp);
    }

    /** Report whether the connection is bound or not.
      * @return true if the connection is bound
      */
    public boolean isBound()
    {
	return (state == BOUND);
    }

    /** Reset's this connection as if before binding.
      * This loses all packets currently stored and reset's the
      * sequence numbering to the start.
      * @exception ie.omk.smpp.AlreadyBoundException if the connection is
      * currently bound to the SMSC.
      */
    public void reset()
	throws ie.omk.smpp.SMPPException
    {
	if(state == BOUND) {
	    Debug.warn(this, "reset", "Attempt to reset a bound connection.");
	    throw new AlreadyBoundException("Cannot reset connection "
		    + "while bound");
	}

	synchronized (seqNumLock) {
	    seqNum = 1;
	}
	Debug.d(this, "reset", "SmppConnection reset", 1);
    }

    /** Get the next sequence number for the next SMPP packet.
      * @return The next valid sequence number for this connection.
      */
    private int nextPacket()
    {
	synchronized (seqNumLock) {
	    if (seqNum == 0x7fffffff)
		seqNum = 1;

	    return (seqNum++);
	}
    }

    /** Get the current packet sequence number.
      * This method will not affect the current value of the sequence
      * number, just allow applications read what the current value is.
      */
    public int getSeqNum()
    {
	return (seqNum);
    }


    /** Read in the next packet from the SMSC link.
      * If asynchronous communications is in use, calling this method results in
      * an SMPPException as the listener thread will be hogging the input stream
      * of the socket connection.
      * @return The next SMPP packet from the SMSC.
      * @exception java.io.IOException If an I/O error occurs.
      * @exception ie.omk.smpp.InvalidOperationException If asynchronous comms
      * is in use.
      */
    public SMPPPacket readNextPacket()
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if (asyncComms)
	    throw new InvalidOperationException("Asynchronous comms in use.");
	else
	    return (readNextPacketInternal());
    }


    /** Read the next packet from the SMSC link. Internal version...handles
      * special case packets like bind responses and unbind request and
      * responses.
      * @return The read SMPP packet, or null if the connection timed out.
      */
    private SMPPPacket readNextPacketInternal()
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPPacket pak = null;
	int st = -1;

	this.buf = link.read(this.buf);
	pak = PacketFactory.newPacket(this.buf);

	if (pak != null) {
	    Debug.d(this, "readNextPacketInternal",
		    "Packet received: " + pak.getCommandId(), 6);

	    // Special case handling for certain packet types..
	    st = pak.getCommandStatus();
	    switch (pak.getCommandId()) {
	    case SMPPPacket.ESME_BNDTRN_RESP:
	    case SMPPPacket.ESME_BNDRCV_RESP:
		if (state == BINDING && st == 0)
		    setState(BOUND);
		break;

	    case SMPPPacket.ESME_UBD_RESP:
		if (state == UNBINDING && st == 0) {
		    Debug.d(this, "readNextPacketInternal",
			    "Successfully unbound.", 3);
		    setState(UNBOUND);
		}
		break;

	    case SMPPPacket.ESME_UBD:
		Debug.d(this, "readNextPacketInternal",
			"SMSC requested unbind.", 2);
		setState(UNBINDING);
		break;
	    }
	}

	return (pak);
    }


    /** Add a connection observer to receive SMPP events from this connection.
     * If this connection is not using asynchronous communication, this method
     * call has no effect.
     * @param ob the ConnectionObserver implementation to add.
     */
    public void addObserver(ConnectionObserver ob)
    {
	if (!asyncComms)
	    return;

	synchronized (observers) {
	    if (singleObserver == ob || observers.contains(ob))
		return;

	    if (singleObserver == null)
		singleObserver = ob;
	    else
		observers.add(ob);
	}
    }

    /** Remove a connection observer from this SmppConnection.
     */
    public void removeObserver(ConnectionObserver ob)
    {
	if (!asyncComms)
	    return;

	synchronized (observers) {
	    if (observers.contains(ob))
		observers.remove(observers.indexOf(ob));
	    else if (ob == singleObserver)
		singleObserver = null;
	}
    }


    /** Notify observers of a packet received.
     * @param pak the received packet.
     */
    protected void notifyObservers(SMPPPacket pak)
    {
	// Due to multi-threading, singleObserver could be set to null (by
	// removeObserver) after we've checked that it's not. No action is
	// necessary if this happens...it just means the observer, which has
	// been removed, will not get the event.
	try {
	    if (singleObserver != null)
		singleObserver.packetReceived(this, pak);
	} catch (NullPointerException x) {
	}

	if (!observers.isEmpty()) {
	    Iterator i = observers.iterator();
	    while (i.hasNext())
		((ConnectionObserver)i.next()).packetReceived(this, pak);
	}
    }

    /** Notify observers of an SMPP control event.
     * @param event the control event to send notification of.
     */
    protected void notifyObservers(SMPPEvent event)
    {
	// Due to multi-threading, singleObserver could be set to null (by
	// removeObserver) after we've checked that it's not. No action is
	// necessary if this happens...it just means the observer, which has
	// been removed, will not get the event.
	try {
	    if (singleObserver != null)
		singleObserver.update(this, event);
	} catch (NullPointerException x) {
	}

	if (!observers.isEmpty()) {
	    Iterator i = observers.iterator();
	    while (i.hasNext())
		((ConnectionObserver)i.next()).update(this, event);
	}
    }

    /** Listener thread method for asynchronous communication.
      */
    public void run()
    {
	SMPPPacket pak = null;
	int smppEx = 0, id = 0, st = 0;
	SMPPEvent exitEvent = null;

	Debug.d(this, "run", "Listener thread started", 4);
	notifyObservers(new ReceiverStartEvent(this));
	try {
	    while (state != UNBOUND) {
		try {
		    pak = readNextPacketInternal();
		    if (pak == null) {
			// XXX Send an event to the application??
			continue;
		    }
		} catch (SMPPException x) {
		    ReceiverExceptionEvent ev =
			new ReceiverExceptionEvent(this, x, state);
		    smppEx++;
		    if (smppEx > 10) {
			Debug.d(this, "run", "Too many SMPP exceptions in "
				+ "receiver thread. Terminating.", 2);
			throw x;
		    }
		}

		id = pak.getCommandId();
		st = pak.getCommandStatus();

		// Handle special case packets..
		switch (id) {
		case SMPPPacket.SMSC_DELIVER_SM:
		    if (ackDeliverSm)
			ackDelivery((DeliverSM)pak);
		    break;

		case SMPPPacket.ESME_QRYLINK:
		    if (ackQryLinks)
			ackLinkQuery((EnquireLink)pak);
		    break;
		}

		// Tell all the observers about the new packet
		Debug.d(this, "run", "Notifying observers..", 4);
		notifyObservers(pak);
	    } // end while

	    // Notify observers that the thread is exiting with no error..
	    exitEvent = new ReceiverExitEvent(this, null, state);
	} catch (Exception x) {
	    Debug.d(this, "run", "Exception: " + x.getMessage(), 2);
	    exitEvent = new ReceiverExitEvent(this, x, state);
	    setState(UNBOUND);
	} finally {
	    // make sure other code doesn't try to restart the rcvThread..
	    rcvThread = null;
	}

	if (exitEvent != null)
	    notifyObservers(exitEvent);
    }

    private void ackDelivery(DeliverSM dm)
    {
	try {
	    Debug.d(this, "ackDelivery", "Auto acking deliver_sm "
		    + dm.getSequenceNum(), 4);
	    DeliverSMResp dmr = new DeliverSMResp(dm);
	    sendResponse(dmr);
	} catch (SMPPException x) {
	    Debug.d(this, "ackDelivery", "SMPP exception acking deliver_sm "
		    + dm.getSequenceNum(), 3);
	} catch (IOException x) {
	    Debug.d(this, "ackDelivery", "IO exception acking deliver_sm "
		    + dm.getSequenceNum(), 3);
	}
    }

    private void ackLinkQuery(EnquireLink el)
    {
	try {
	    Debug.d(this, "ackLinkEnquire", "Auto acking enquire_link "
		    + el.getSequenceNum(), 4);
	    EnquireLinkResp elr = new EnquireLinkResp(el);
	    sendResponse(elr);
	} catch (SMPPException x) {
	    Debug.d(this, "ackLinkEnquire", "SMPP exception acking "
		    + "enquire_link " + el.getSequenceNum(), 3);
	} catch (IOException x) {
	    Debug.d(this, "ackLinkEnquire", "IO exception acking enquire_link "
		    + el.getSequenceNum(), 3);
	}
    }
}
