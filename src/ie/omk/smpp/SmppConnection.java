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

import java.io.*;
import java.net.*;
import java.util.*;
import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
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
    extends java.util.Observable
    implements java.lang.Runnable
{
    /** Version number of the SMPP Protocol implemented. */
    public static final int	INTERFACE_VERSION = 0x33;

    /** Connection state: not bound to the SMSC. */
    public static final int	UNBOUND = 0;

    /** Connection state: waiting for successful acknowledgement to bind
      * request.
      */
    public static final int	BINDING = 1;

    /** Connection state: bound to the SMSC. */
    public static final int	BOUND = 2;

    /** Connection state: waiting for successful acknowledgement to unbind
      * request.
      */
    public static final int	UNBINDING = 3;

    /** Packet listener thread for Asyncronous comms. */
    protected Thread		rcvThread = null;

    /** Milliseconds to timeout while waiting on I/O in listener thread. */
    protected long		timeout = 100;

    /** Address range used for routing messages */
    protected String		addrRange = null;

    /** ESME System Id, used to authenticate to the SMSC */
    protected String		sysId = null;

    /** Password, used to authenticate to the SMSC */
    protected String		password = null;

    /** Identify the system type of this ESME to the SMSC */
    protected String		sysType = null;

    /** Address routing type of number */
    protected int		addrTon = 0;

    /** Address routing numbering plan indicator */
    protected int		addrNpi = 0;

    /** Sequence number */
    private int			seqNum = 1;

    /** Sequence numbering lock object. */
    private Object		seqNumLock = new Object();

    /** The network link (virtual circuit) to the SMSC */
    protected SmscLink		link = null;

    /** Last Request packet sent to the SMSC */
    protected Hashtable		outTable = null;

    /** Last Response packet received from the SMSC */
    protected Hashtable		inTable = null;

    /** Points to the last packet sent to the Smsc */
    protected SMPPPacket	lastOutward = null;

    /** For each request sent, 1 is added, for each proper ack got,
      * 1 comes off.
      */
    protected int		waitingAck = 0;

    /** Current state of the SMPP connection.
      * Possible states are UNBOUND, BINDING, BOUND and UNBINDING.
      */
    private int			state = UNBOUND;

    /** Specify whether the listener thread will automatically ack
      * enquire_link primitives received from the Smsc
      */
    protected boolean		ackQryLinks = true;

    /** Should the listener thread automatically ack incoming messages?
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

	// Table of last requests and responses.  Max 150 each.
	// once the limit is reached, the tables are purged and it begins
	// again.
	outTable = new Hashtable(150);
	inTable = new Hashtable(150);
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
	asyncComms = async;

	if (asyncComms)
	    rcvThread = new Thread(this);
    }

    /** Set the state of this ESME.
      * @see ie.omk.smpp.SmppConnection#getState
      */
    protected synchronized void setState(int state)
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

    /** Set the routing information for this ESME
      * @param ton The Type of Number
      * @param npi The Numbering plan indicator
      * @param range The address routing expression (Up to 40 characters)
      * @exception ie.omk.smpp.StringTooLongException if the source routing
      * expression is too long.
      * @see ie.omk.smpp.util.GSMConstants
      */
    public void setSourceAddress(int ton, int npi, String range)
	throws ie.omk.smpp.SMPPException
    {
	this.addrTon = ton;
	this.addrNpi = npi;

	if(range == null) {
	    this.addrRange = null;
	    return;
	}

	if(range.length() < 41)
	    this.addrRange = range;
	else
	    throw new StringTooLongException(40);
    }

    /** Set the system Id for this Esme
      * @param name System Id (Up to 15 characters)
      * @exception ie.omk.smpp.StringTooLongException if the system id is too
      * long.
      */
    public void setSystemId(String name)
	throws ie.omk.smpp.SMPPException
    {
	if(name == null) {
	    this.sysId = null;
	    return;
	}

	if(name.length() < 16)
	    this.sysId = name;
	else
	    throw new StringTooLongException(15);
    }

    /** Set the authentication password
      * @param pass The password to use (Up to 8 characters)
      * @exception ie.omk.smpp.StringTooLongException if the password is too
      * long.
      */
    public void setPassword(String pass)
	throws ie.omk.smpp.SMPPException
    {
	if(pass == null) {
	    this.password = null;
	    return;
	}

	if(pass.length() < 9)
	    this.password = pass;
	else
	    throw new StringTooLongException(8);
    }

    /** Set the system type for this connection
      * @param type The System type (Up to 12 characters)
      * @exception ie.omk.smpp.StringTooLongException if the system type is too
      * long.
      */
    public void setSystemType(String type)
	throws ie.omk.smpp.SMPPException
    {
	if(type == null) {
	    this.sysType = null;
	    return;
	}

	if(type.length() < 13)
	    this.sysType = type;
	else
	    throw new StringTooLongException(12);
    }

    /** Set the behaviour of automatically acking QRYLINK's from the SMSC.
      * By default, the listener thread will automatically ack an enquire_link
      * message from the Smsc so as not to lose the connection.  This
      * can be turned off with this method.
      * @param true to activate automatic acknowledgment, false to disable
      */
    public synchronized void autoAckLink(boolean b)
    {
	this.ackQryLinks = b;
    }

    /** Set the behaviour of automatically acking Deliver_Sm's from the Smsc.
      * By default the listener thread will <b>not</b> acknowledge a message.
      * @param true to activate this function, false to deactivate.
      */
    public synchronized void autoAckMessages(boolean b)
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

    /** Get the System Id of this transmitter
      */
    public String getSystemId()
    {
	return (sysId);
    }

    /** Get the password being used by this transmitter.
      */
    public String getPassword()
    {
	return (password);
    }

    /** Get the system type of this transmitter.
      */
    public String getSystemType()
    {
	return (sysType);
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
	OutputStream out = link.getOutputStream();
	InputStream in = link.getInputStream();

	r.setSequenceNum(nextPacket());

	synchronized (link) {
	    r.writeTo(out);
	    waitingAck++;
	    outTable.put(new Integer(r.getSequenceNum()), r);
	    lastOutward = r;

	    if (!asyncComms) {
		resp = SMPPPacket.readPacket(in);
		if(resp instanceof SMPPResponse) {
		    waitingAck--;
		    inTable.put(new Integer(resp.getSequenceNum()), resp);
		} else {
		    Debug.d(this, "sendRequest", "Response received from "
			+ "SMSC is not an SMPPResponse!", Debug.DBG_1);
		}
	    }
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
	SMPPRequest rq = null;

	if (link == null)
	    throw new IOException("Connection to SMSC is not valid.");

	// Make sure we got a request for the packet we're trying to ack...
	key = new Integer(resp.getSequenceNum());
	try {
	    rq = (SMPPRequest)inTable.get(key);
	} catch(ClassCastException cx) {
	    rq = null;
	}

	if (rq == null)
	    throw new NoSuchRequestException();

	Object lock = null;
	OutputStream out = link.getOutputStream();
	if (asyncComms)
	    lock = out;
	else
	    lock = link;

	synchronized (lock) {
		rq.ack();
		outTable.put(key, resp);
		lastOutward = resp;
		resp.writeTo(out);
	}
    }

    /** bind this connection to the SMSC.
      * Sub classes of SmppConnection must provide an implementation of this.
      * @return The bind transmitter or bind receiver response or null if
      * asynchronous communications is in use.
      * @exception java.io.IOException If a communications error occurs
      * @exception ie.omk.smpp.SMPPExceptione XXX when?
      * @see ie.omk.smpp.SmppTransmitter#bind
      * @see ie.omk.smpp.SmppReceiver#bind
      */
    public abstract SMPPResponse bind()
	throws java.io.IOException, ie.omk.smpp.SMPPException;

    /** Unbind from the SMSC and close the network connections.
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

	Unbind u = new Unbind(1);
	SMPPResponse resp = sendRequest(u);
	if (!asyncComms) {
	    if (resp.getCommandId() == SMPPPacket.ESME_UBD_RESP
		    && resp.getCommandStatus() == 0)
		setState(UNBOUND);
	}
	return ((UnbindResp)resp);
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
	    Debug.d(this, "force_close", "Force tried before normal unbind.",
		    Debug.DBG_2);
	    throw new AlreadyBoundException("Try a normal unbind first.");
	}

	Debug.d(this, "force_unbind",
		"Attempting to force the connection shut.", Debug.DBG_4);
	try {
	    // The thread must DIE!!!!
	    if(rcvThread != null && rcvThread.isAlive()) {
		setState(UNBOUND);
		try {
		    Thread.sleep(timeout * 2);
		} catch (InterruptedException x) {
		}
		if (rcvThread.isAlive())
		    System.err.println("ERROR! Listener thread has not died.");
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
	Debug.d(this, "ackEnquireLink", "Response sent", Debug.DBG_3);
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
	EnquireLink s = new EnquireLink(1);

	SMPPResponse resp = sendRequest(s);
	Debug.d(this, "enquireLink", "Request sent", Debug.DBG_3);
	return ((EnquireLinkResp)resp);
    }

    /** Get the response packet associated with the request packet rq.
      * @param rq The request packet to get the reponse for.
      * @return The associated response packet, or null if there isn't one.
      */
    public synchronized SMPPResponse getResponsePacket(SMPPRequest rq)
    {
	Integer key = null;
	SMPPPacket p1, p2;

	key = new Integer(rq.getSequenceNum());
	p1 = (SMPPPacket)inTable.get(key);
	p2 = (SMPPPacket)outTable.get(key);

	if(p1 == null || p2 == null)
	    return (null);

	if(p1.equals(rq) && (p2 instanceof SMPPResponse))
	    return (SMPPResponse)p2;
	else if(p2.equals(rq) && (p1 instanceof SMPPResponse))
	    return (SMPPResponse)p1;
	else
	    return (null);
    }

    /** Get the request packet associated with the response packet resp.
      * @param resp The response packet to get the request for.
      * @return The associated request packet or null if there isn't one.
      */
    public synchronized SMPPRequest getRequestPacket(SMPPResponse resp)
    {
	Integer key = null;
	SMPPPacket p1, p2;

	key = new Integer(resp.getSequenceNum());
	p1 = (SMPPPacket)inTable.get(key);
	p2 = (SMPPPacket)outTable.get(key);

	if(p1 == null || p2 == null)
	    return (null);

	if(p1.equals(resp) && (p2 instanceof SMPPRequest))
	    return (SMPPRequest)p2;
	else if(p2.equals(resp) && (p1 instanceof SMPPRequest))
	    return (SMPPRequest)p1;
	else
	    return (null);
    }

    /** Get the Smsc-originated packet with the specified sequence number.
      * @param The sequence number of the packet to get
      * @return The packet or null if there is no such packet
      */
    public synchronized SMPPPacket getInwardPacket(int seq)
    {
	Integer key = new Integer(seq);
	return (SMPPPacket)inTable.get(key);
    }

    /** Get the Esme-originated (ie locally) packet with the specified sequence
      * number.
      * @param The sequence number of the packet to get
      * @return The packet or null if there is no such packet
      */
    public synchronized SMPPPacket getOutwardPacket(int seq)
    {
	Integer key = new Integer(seq);
	return (SMPPPacket)outTable.get(key);
    }

    /** Returns the last packet sent to the Smsc.
      */
    public synchronized SMPPPacket getLastOutwardPacket()
    {
	// XXX Means nothing anymore to be syncrhonized on this. Outward table
	// is being removed anyway!
	return (lastOutward);
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
	if((state == BOUND) || link.isConnected()) {
	    Debug.d(this, "reset", "Reset failed. state="
		    + state
		    + ", link="
		    + link.isConnected(), Debug.DBG_3);
	    throw new AlreadyBoundException("Cannot reset connection "
		    + "while bound");
	}

	Debug.d(this, "reset", "SmppConnection reset", Debug.DBG_1);
	inTable.clear();
	outTable.clear();
	synchronized (seqNumLock) {
	    seqNum = 1;
	}
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
      * @param timeout Milliseconds to wait for a packet before returning.
      * @return The next SMPP packet from the SMSC. null if no packet arrives
      * within timeout milliseconds.
      * @exception java.io.IOException If an I/O error occurs.
      * @exception ie.omk.smpp.SMPPException If asynchronous comms is in use.
      */
    public SMPPPacket readNextPacket(long timeout)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	if (asyncComms)
	    throw new InvalidOperationException("Asynchronous comms in use.");

	Date start = new Date();
	InputStream in = link.getInputStream();
	SMPPPacket pak = null;

	synchronized (link) {
	    while ((new Date().getTime() - start.getTime()) < timeout) {
		if (in.available() < 16) {
		    try {
			Thread.sleep(5);
		    continue;
		    } catch (InterruptedException x) {
			break;
		    }
		}

		pak = SMPPPacket.readPacket(in);
	    }
	}

	return (pak);
    }

    /** Add an observer to receive SmppEvents from this connection.
      * If asynchronous communication is not turned on, this method call has no
      * effect.
      */
    public void addObserver(Observer ob)
    {
	if (asyncComms)
	    super.addObserver(ob);
    }

    /** Notify all observers that a new packet has arrived.
      * @param b The packet that just arrived.
      */
    public void notifyObservers(Object b)
    {
	if(! (b instanceof SMPPPacket)) {
	    throw new IllegalArgumentException("Notify Observers needs an "
		    + "SMPPPacket class.");
	}

	// Create a new SmppEvent and notify observers of it....
	Object details = SmppEvent.detailFactory((SMPPPacket)b);
	SmppEvent ev = new SmppEvent(this, (SMPPPacket)b, details);

	this.setChanged();
	super.notifyObservers(ev);
    }

    // XXX Fix exception handling in this method!
    public void run()
    {
	Integer key = null;
	SMPPRequest rq = null;
	SMPPPacket pak = null;

	Debug.d(this, "run", "Listener thread is up and running", Debug.DBG_4);
	while (state != UNBOUND) {
	    try {
		try {
		    pak = SMPPPacket.readPacket(link.getInputStream());
		    if (pak == null)
			continue;
		} catch(SMPPException ix) {
		    /* Don't mind this.  Just try and read another one.. */
		    Debug.d(this,
			    "run",
			    "Smpp Exception trying to read a packet."
				+ ix.getMessage(),
			    Debug.DBG_3);
		    pak = null;
		    continue;
		} catch(EOFException ex) { 
		    /* This, on the other hand, is a Bad Thing */
		    Debug.d(this,
			    "run",
			    "EOFException in thread. " + ex.getMessage(),
			    Debug.DBG_3);
		    setState(UNBOUND);
		    try { link.close(); }
		    catch(IOException ix) { }
		    SmppConnectionDropPacket p =
			new SmppConnectionDropPacket(0xffffffff);
		    p.setMessage(ex.getMessage());
		    notifyObservers(p);

		    return;
		} catch(IOException ix) {
		    /* And this too...tut tut */
		    Debug.d(this,
			    "run",
			    "IOException in thread" + ix.getMessage(),
			    Debug.DBG_3);
		    setState(UNBOUND);
		    try {
			link.close();
		    } catch(IOException ex) {
		    }
		    SmppConnectionDropPacket p =
			new SmppConnectionDropPacket(0xffffffff);
		    p.setMessage(ix.getMessage());
		    notifyObservers(p);
		}


		if(pak == null)
		    continue;
		Debug.d(this, "run", "Packet recv:" + pak.getClass().getName(),
			Debug.DBG_2);

		// Add the received packet to the table
		if(inTable.size() == 150)
		    inTable.clear();
		key = new Integer(pak.getSequenceNum());
		inTable.put(key, pak);

		// Set the request message to ackd if it's there...
		if(waitingAck > 0) {
		    rq = (SMPPRequest)outTable.get(key);
		    if(rq != null) {
			rq.ack();
			waitingAck--;
		    }
		}

		int id = pak.getCommandId();
		int st = pak.getCommandStatus();

		// Special case packets...
		try {
		    switch (id) {
		    case SMPPPacket.ESME_BNDTRN_RESP:
		    case SMPPPacket.ESME_BNDRCV_RESP:
			if (state == BINDING && st == 0)
			    setState(BOUND);
			break;

		    case SMPPPacket.ESME_UBD_RESP:
			if (state == UNBINDING && st == 0) {
			    Debug.d(this, "run", "Successfully unbound.",
				    Debug.DBG_3);
			    setState(UNBOUND);
			}
			break;

		    case SMPPPacket.SMSC_DELIVER_SM:
			if (ackDeliverSm) {
			    DeliverSMResp dr =
				new DeliverSMResp((DeliverSM)pak);
			    sendResponse(dr);
			    Debug.d(this, "run", "Ack'd deliver_sm #"
				    + dr.getSequenceNum(),
				    Debug.DBG_3);
			}
			break;

		    case SMPPPacket.ESME_QRYLINK:
			if(ackQryLinks) {
			    EnquireLinkResp el =
				new EnquireLinkResp((EnquireLink)pak);
			    sendResponse(el);
			    Debug.d(this, "run", "Ack'd enquire_link #"
				    + el.getSequenceNum(),
				    Debug.DBG_3);
			}
			break;
		    }
		} catch (SMPPException x) {
		    // XXX Handle properly
		    System.err.print("[SmppConnection.run]\n\t");
		    x.printStackTrace(System.err);
		}

		// Tell all the observers about the new packet
		Debug.d(this, "run", "Notifying observers of new Packet",
			Debug.DBG_4);
		notifyObservers(pak);
	    } catch(IOException x) {
		Debug.d(this, "run", "IOException: "+x.getMessage(),
			Debug.DBG_1);
	    }
	} // end while
    } // end run()
}
