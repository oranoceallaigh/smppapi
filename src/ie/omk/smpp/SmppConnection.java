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

import java.io.*;
import java.net.*;
import java.util.*;
import ie.omk.smpp.message.*;
import ie.omk.smpp.net.*;
import ie.omk.debug.Debug;

/** Abstract super class of impementations of an Smpp Protocol connection
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SmppConnection
    extends java.util.Observable
    implements java.lang.Runnable
{
    /** Major version number of the SMPP Protocol implemented */
    public static final int	INTERFACE_VERSION = 0x03;

    /** Minor version number of the protocol implemented */
    public static final int	MINOR_VERSION = 0x03;

    /** Listening occurs in a separate thread. */
    Thread			rcvThread = null;

    /** Need this in the run() method to know if the thread should exit */
    boolean			unbinding = false;

    /** Address range used for routing int messages */
    String			addrRange = null;

    /** ESME System Id, used to authenticate to the SMSC */
    String			sysId = null;

    /** Password, used to authenticate to the SMSC */
    String			password = null;

    /** Identify the system type of this ESME to the SMSC */
    String			sysType = null;

    /** Address routing type of number */
    int				addrTon = 0;

    /** Address routing numbering plan indicator */
    int				addrNpi = 0;

    /** Sequence number */
    int				seqNo = 1;

    /** The network link (virtual circuit) to the SMSC */
    SmscLink			link = null;

    /** Last Request packet sent to the SMSC */
    Hashtable			outTable = null;

    /** Last Response packet received from the SMSC */
    Hashtable			inTable = null;

    /** Points to the last packet sent to the Smsc */
    SMPPPacket			lastOutward = null;

    /** For each request sent, 1 is added, for each proper ack got,
     * 1 comes off.
     */
    int				waitingAck = 0;

    /** Input stream of the virtual circuit */
    InputStream			in = null;

    /** Output stream of the virtual circuit */
    OutputStream		out = null;

    /** Identify if this ESME is currently bound to the SMSC */
    boolean			bound = false;

    /** Specify whether the listener thread will automatically ack
     * enquire_link primitives received from the Smsc
     */
    boolean			ackQryLinks = true;

    /** Should the listener thread automatically ack incoming messages?
     * Only valid for the Receiver
     */
    boolean			ackDeliverSm = false;

    /** Create a new Smpp connection.
     * @param link The network link object to the Smsc (cannot be null)
     * @exception java.lang.NullPointerException If the link is null
     */
    public SmppConnection(SmscLink link)
    {
	if(link == null)
	    throw new NullPointerException("Smsc Link cannot be null.");

	this.link = link;

	// Table of last requests and responses.  Max 150 each.
	// once the limit is reached, the tables are purged and it begins
	// again.
	outTable = new Hashtable(150);
	inTable = new Hashtable(150);

	// Create a new listener...won't do anything till start() is called
	rcvThread = new Thread(this);
    }

    /** Set the routing information for this ESME
     * @param ton The Type of Number
     * @param npi The Numbering plan indicator
     * @param range The address routing expression (Up to 40 characters)
     * @exception SMPPException If the routing expression is invalid
     */
    public void setSourceAddress(int ton, int npi, String range)
    {
	addrTon = ton;
	addrNpi = npi;

	if(range == null) {
	    addrRange = null;
	    return;
	}

	if(range.length() < 41)
	    addrRange = new String(range);
	else
	    throw new SMPPException("Address range must be < 41 chars");
    }

    /** Set the system Id for this Esme
     * @param name System Id (Up to 15 characters)
     * @exception SMPPException If the system Id is invalid
     */
    public void setSystemId(String name)
    {
	if(name == null) {
	    sysId = null;
	    return;
	}

	if(name.length() < 16)
	    sysId = new String(name);
	else
	    throw new SMPPException("System Id must be < 16 chars");
    }

    /** Set the authentication password
     * @param pass The password to use (Up to 8 characters)
     * @exception SMPPException If the password is invalid
     */
    public void setPassword(String pass)
    {
	if(pass == null) {
	    password = null;
	    return;
	}

	if(pass.length() < 9)
	    password = new String(pass);
	else
	    throw new SMPPException("Password must be < 9 chars");
    }

    /** Set the system type for this connection
     * @param type The System type (Up to 12 characters)
     * @exception SMPPException If the system type is invalid
     */
    public void setSystemType(String type)
    {
	if(type == null) {
	    sysType = null;
	    return;
	}

	if(type.length() < 13)
	    sysType = new String(type);
	else
	    throw new SMPPException("System type must be < 13 chars");
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
	return ackQryLinks;
    }

    /** Check is this connection automatically acking delivered messages
     */
    public boolean isAckingMessages()
    {
	return ackDeliverSm;
    }

    /** Get the System Id of this transmitter
     */
    public String getSystemId()
    {
	return sysId;
    }

    /** Get the password being used by this transmitter.
     */
    public String getPassword()
    {
	return password;
    }

    /** Get the system type of this transmitter.
     */
    public String getSystemType()
    {
	return sysType;
    }

    /** Send an smpp request to the SMSC.
     * @param r The request packet to send to the SMSC
     * @return The response packet returned by the SMSC
     * @exception SMPPException If an invalid response packet is returned
     * @exception java.io.IOException If a network error occurs
     */
    public void sendRequest(SMPPRequest r)
	throws IOException
    {
	if(out == null || in == null)
	    throw new IOException("Connection to the SMSC is not open.");

	if(r.getSeqNo() < seqNo-1) {
	    throw new SMPPException("Sequence numbering invalid.  Current="
		    + String.valueOf(seqNo-1)
		    + ", Packet value="
		    + r.getSeqNo());
	}

	waitingAck++;
	r.writeTo(out);
	outTable.put(new Integer(r.getSeqNo()), r);
	lastOutward = r;

	/* Take out the reading of response...listener thread should
	   notify any listeners of responses (Transmitter) and requests
	   (Receiver)

	   SMPPPacket rsp = SMPPPacket.readPacket(in);
	   if(rsp instanceof SMPPResponse)
	   {
	   this.inTable = (SMPPResponse)rsp;
	   return (SMPPResponse)rsp;
	   }
	   else
	   throw new SMPPException("No response packet on input stream.");
	 */
    }

    /** Send an smpp response packet to the SMSC
     * @param r The response packet to send to the SMSC
     * @exception SMPPException If the sequence numbering is incorrect
     * @exception java.io.IOException If a network error occurs
     * @see SmppConnection#nextPacket
     */
    public synchronized void sendResponse(SMPPResponse rsp)
	throws IOException
    {
	Integer key = null;
	SMPPRequest rq = null;

	if(out == null || in == null)
	    throw new IOException("Connections to SMSC are not valid.");

	// Make sure we got a request for the packet we're trying to ack...
	key = new Integer(rsp.getSeqNo());
	try {
	    rq = (SMPPRequest)inTable.get(key);
	} catch(ClassCastException cx) {
	    rq = null;
	}
	if(rq == null) {
	    throw new SMPPException("There was no request with sequence no. "
		    + key);
	} else if(rq.isAckd()) {
	    throw new SMPPException("The packet with sequence "
		    + key + " is already acknowledged.");
	}

	rq.ack();
	outTable.put(key, rsp);
	lastOutward = rsp;
	rsp.writeTo(out);
    }

    /** bind this connection to the SMSC.
     * Sub classes of SmppConnection must provide an implementation of this.
     * @return true if the connection bound successfully, false otherwise
     * @see SmppTransmitter#bind
     * @see SmppReceiver#bind
     */
    public abstract boolean bind() throws java.io.IOException;

    /** Unbind from the SMSC and close the network connections.
     * @return true if the packet was sent successfully
     * @exception SMPPException If already bound or a Nack arrives.
     * @exception java.io.IOException If a network error occurs.
     * @see SmppReceiver#bind
     */
    public boolean unbind()
	throws java.io.IOException
    {
	if(!bound || !link.isConnected())
	    throw new SMPPException("Not bound to SMSC yet.");

	/* If this is set, the run() method will return when an
	 * unbind response packet arrives, stopping the listener
	 * thread. (after all observers have been notified of the packet)
	 */
	unbinding = true;

	Unbind u = new Unbind(nextPacket());
	sendRequest(u);
	return true;
    }

    /** Use of this <b><i>highly</i></b> discouraged.
     * This is in case of emergency and stuff.
     * Closing the connection to the Smsc without unbinding
     * first can cause horrific trouble with runaway processes.  Don't
     * do it!
     */
    public void force_unbind()
    {
	if(!unbinding) {
	    Debug.d(this, "force_close", "Force tried before normal unbind.",
		    Debug.DBG_2);
	    throw new SMPPException("Please Try a normal unbind before "
		    + "forcing one.");
	}

	Debug.d(this, "force_unbind",
		"Attempting to force the connection shut.", Debug.DBG_4);
	try {
	    // The thread must DIE!!!!
	    if(rcvThread.isAlive())
		rcvThread.stop();

	    link.close();
	} catch(IOException ix) {
	}

	bound = false;
	return;
    }


    /** Acknowledge an EnquireLink received from the Smsc */
    public void ackEnquireLink(EnquireLink rq)
	throws java.io.IOException
    {
	EnquireLinkResp rsp = new EnquireLinkResp(rq);
	sendResponse(rsp);
	Debug.d(this, "ackEnquireLink", "Response sent", Debug.DBG_3);
    }

    /** Do a confidence check on the SMPP link to the SMSC.
     * @return true if the packet was sent successfully
     * @exception java.io.IOException If a network error occurs
     */
    public boolean enquireLink()
	throws java.io.IOException
    {
	EnquireLink s = new EnquireLink(nextPacket());

	sendRequest(s);
	Debug.d(this, "enquireLink", "Request sent", Debug.DBG_3);
	return true;
    }

    /** Get the response packet associated with the request packet rq.
     * @param rq The request packet to get the reponse for.
     */
    public synchronized SMPPResponse getResponsePacket(SMPPRequest rq)
    {
	Integer key = null;
	SMPPPacket p1, p2;

	key = new Integer(rq.getSeqNo());
	p1 = (SMPPPacket)inTable.get(key);
	p2 = (SMPPPacket)outTable.get(key);

	if(p1 == null || p2 == null)
	    return null;

	if(p1.equals(rq) && (p2 instanceof SMPPResponse))
	    return (SMPPResponse)p2;
	else if(p2.equals(rq) && (p1 instanceof SMPPResponse))
	    return (SMPPResponse)p1;
	else
	    return null;
    }

    /** Get the request packet associated with the response packet rsp.
     * @param rsp The response packet to get the request for.
     */
    public synchronized SMPPRequest getRequestPacket(SMPPResponse rsp)
    {
	Integer key = null;
	SMPPPacket p1, p2;

	key = new Integer(rsp.getSeqNo());
	p1 = (SMPPPacket)inTable.get(key);
	p2 = (SMPPPacket)outTable.get(key);

	if(p1 == null || p2 == null)
	    return null;

	if(p1.equals(rsp) && (p2 instanceof SMPPRequest))
	    return (SMPPRequest)p2;
	else if(p2.equals(rsp) && (p1 instanceof SMPPRequest))
	    return (SMPPRequest)p1;
	else
	    return null;
    }

    /** Get the Smsc-originated packet numbered seq.
     * @param The sequence number of the packet to get
     * @return null if there is no such packet
     */
    public synchronized SMPPPacket getInwardPacket(int seq)
    {
	Integer key = new Integer(seq);
	return (SMPPPacket)inTable.get(key);
    }

    /** Get the Esme-originated (ie locally) packet numbered seq.
     * @param The sequence number of the packet to get
     * @return null if there is no such packet
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
	return lastOutward;
    }

    /** Report whether the connection is bound or not.
     * @return true if the connection is bound
     */
    public boolean isbound()
    {
	return bound;
    }

    /** Reset's this connection as if before binding.
     * This loses all packets currently stored and reset's the
     * sequence numbering to the start.
     */
    public void reset()
    {
	if(bound || link.isConnected()) {
	    Debug.d(this, "reset", "Reset failed. bound="
		    + bound
		    + ", link="
		    + link.isConnected(), Debug.DBG_3);
	    throw new SMPPException("Cannot reset connection while bound or "
		    + "connected.");
	}
	if(rcvThread.isAlive()) {
	    Debug.d(this, "reset", "listener thread stopped.", Debug.DBG_2);
	    rcvThread.stop();
	}

	Debug.d(this, "reset", "SmppConnection reset", Debug.DBG_1);
	inTable.clear();
	outTable.clear();
	seqNo = 1;
    }

    /** Get the next sequence number for the next SMPP packet.
     * The local side needs to keep track of the sequence numbers
     * of the SMPP packets. As a result this function should always
     * be used to number any newly created Smpp packets.  Otherwise
     * the SMSC will become confused and return Generic Nacks.
     * And it's inaccessible outside the smpp package for obvious reasons.
     * If an application want's a sequence number, use currentPacket()
     * to get that the next valid sequence number is without affecting
     * it's value
     * @return The next valid sequence number.
     */
    int nextPacket()
    {
	// Gonna return x in a second...
	int x = seqNo;

	if(seqNo == 0x7fffffff)
	    seqNo = 0x01;
	else
	    seqNo++;

	return x;
    }

    /** Get the next valid Smpp packet sequence number.
     * This method will not affect the current value of the sequence
     * number, just allow applications read what the next value will be.
     */
    public int currentPacket()
    {
	return seqNo;
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
	SmppEvent ev = new SmppEvent(this, details, (SMPPPacket)b);

	this.setChanged();
	super.notifyObservers(ev);
    }

    public void run()
    {
	Integer key = null;
	SMPPRequest rq = null;
	SMPPPacket pak = null;
	int id = 0;

	Debug.d(this, "run", "Listener thread is up and running", Debug.DBG_4);
	while(true) {
	    try {
		try {
		    pak = SMPPPacket.readPacket(in);
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
		    bound = false;
		    try { link.close(); }
		    catch(IOException ix) { }
		    SmppConnectionDropPacket p =
			new SmppConnectionDropPacket(nextPacket());
		    p.setMessage(ex.getMessage());
		    notifyObservers(p);

		    return;
		} catch(IOException ix) {
		    /* And this too...tut tut */
		    Debug.d(this,
			    "run",
			    "IOException in thread" + ix.getMessage(),
			    Debug.DBG_3);
		    bound = false;
		    try {
			link.close();
		    } catch(IOException ex) {
		    }
		    SmppConnectionDropPacket p =
			new SmppConnectionDropPacket(nextPacket());
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
		key = new Integer(pak.getSeqNo());
		inTable.put(key, pak);

		// Set the request message to ackd if it's there...
		if(waitingAck > 0) {
		    rq = (SMPPRequest)outTable.get(key);
		    if(rq != null) {
			rq.ack();
			waitingAck--;
		    }
		}

		id = pak.getCommandId();
		if(!bound && ((id == SMPPPacket.ESME_BNDTRN_RESP)
			    || (id == SMPPPacket.ESME_BNDRCV_RESP)))
		    bound = true;

		// Check is it's an unbind response.  If it is, and
		// unbinding is set to true, it's time to exit!
		if(unbinding && id == SMPPPacket.ESME_UBD_RESP) {
		    Debug.d(this, "run", "Successfully unbound.", Debug.DBG_3);
		    bound = false;
		    notifyObservers(pak);
		    break;
		}

		// Ack a deliver_sm if appropriate
		if(ackDeliverSm && id == SMPPPacket.SMSC_DELIVER_SM) {
		    DeliverSMResp dr = new DeliverSMResp((DeliverSM)pak);
		    sendResponse(dr);
		    Debug.d(this, "run", "Ack'd deliver_sm #" + dr.getSeqNo(),
			    Debug.DBG_3);
		}

		// Automatically ack EnquireLink packets from the Smsc.
		// Prevents timeout problems
		if(ackQryLinks && id == SMPPPacket.ESME_QRYLINK) {
		    EnquireLinkResp el = new EnquireLinkResp((EnquireLink)pak);
		    sendResponse(el);
		    Debug.d(this, "run", "Ack'd enquire_link #" + el.getSeqNo(),
			    Debug.DBG_3);
		}

		// Tell all the observers about the new packet
		Debug.d(this, "run", "Notifying observers of new Packet",
			Debug.DBG_4);
		notifyObservers(pak);
	    } catch(IOException x) {
		Debug.d(this, "run", "IOException: "+x.getMessage(),
			Debug.DBG_1);
		throw new SMPPException("I/O Error in listener."
			+ x.toString());
	    }
	} // end while
    } // end run()
}
