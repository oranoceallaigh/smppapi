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

import java.net.SocketTimeoutException;

import java.util.ArrayList;
import java.util.Iterator;

import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.ReceiverStartEvent;

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
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPRequest;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitMulti;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import ie.omk.smpp.net.SmscLink;
import ie.omk.smpp.net.TcpLink;

import ie.omk.smpp.util.DefaultSequenceScheme;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SequenceNumberScheme;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** SMPP client connection (ESME).
  * @author Oran Kelly
  * @version 1.0
  */
public class Connection
    implements java.lang.Runnable
{
    /** Get the logger for this Connection. */
    protected Logger logger = Logger.getLogger("ie.omk.smpp.Connection");

    /** SMPP Transmitter connection type. */
    public static final int	TRANSMITTER = 1;

    /** SMPP Receiver connection type. */
    public static final int	RECEIVER = 2;

    /** SMPP Transciever connection type. */
    public static final int	TRANSCEIVER = 3;

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

    /** Type of this SMPP connection. */
    private int			connectionType = 0;

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

    /** Sequence numbering scheme to use for this connection. */
    private SequenceNumberScheme seqNumScheme = new DefaultSequenceScheme();

    /** The network link (virtual circuit) to the SMSC */
    private SmscLink		link = null;

    /** SMPP protocol version number.
     */
    protected SMPPVersion	interfaceVersion = SMPPVersion.V33;

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


    /** Initialise a new SMPP connection object. This is a convenience
     * constructor that will create a new {@link ie.omk.smpp.net.TcpLink} object
     * using the host name and port provided.
     * @param host the hostname of the SMSC.
     * @param port the port to connect to. If 0, use the default SMPP port
     * number.
     */
    public Connection(String host, int port)
	throws java.net.UnknownHostException
    {
	this (new TcpLink(host, port), false);
    }

    /** Initialise a new SMPP connection object.
      * @param link The network link object to the Smsc (cannot be null)
      */
    public Connection(SmscLink link)
    {
	this (link, false);
    }

    /** Initialise a new SMPP connection object, specifying the type of
     * communication desired.
     * @param link The network link object to the Smsc (cannot be null)
     * @param async true for asyncronous communication, false for synchronous.
     */
    public Connection(SmscLink link, boolean async)
    {
	this.link = link;
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
	logger.info("Creating receiver thread");
	rcvThread = new Thread(this, "ReceiverDaemon");
	rcvThread.setDaemon(true);
    }

    /** Set the state of this ESME.
      * @see ie.omk.smpp.Connection#getState
      */
    private synchronized void setState(int state)
    {
	logger.info("Setting state " + state);
	this.state = state;
    }

    /** Get the current state of the ESME. One of UNBOUND, BINDING, BOUND or
      * UNBINDING.
      */
    public synchronized int getState()
    {
	return (this.state);
    }

    /** Method to open the link to the SMSC. This method will connect the
     * underlying SmscLink object if necessary and reset the sequence numbering
     * scheme to the beginning.
     * @throws java.io.IOException if an i/o error occurs while opening the
     * connection.
     */
    protected void openLink()
	throws java.io.IOException
    {
	if (!this.link.isConnected()) {
	    logger.info("Opening network link.");
	    this.link.open();
	    if (this.seqNumScheme != null)
		this.seqNumScheme.reset();
	} else {
	    logger.debug("openLink called, link already open");
	}
    }


    /** Get the interface version.
     * @see #setInterfaceVersion(SMPPVersion)
     */
    public SMPPVersion getInterfaceVersion()
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
     */
    public void setInterfaceVersion(SMPPVersion interfaceVersion)
    {
	logger.info("setInterfaceVersion " + interfaceVersion);
	this.interfaceVersion = interfaceVersion;
    }


    /** Set the behaviour of automatically acking ENQUIRE_LINK's from the SMSC.
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

    /** Acknowledge a DeliverSM command received from the Smsc.
     * @param rq The deliver_sm request to respond to.
     * @throws java.io.IOException If an I/O error occurs writing the response
     * packet to the network connection.
     */
    public void ackDeliverSm(DeliverSM rq)
	throws java.io.IOException
    {
	DeliverSMResp rsp = new DeliverSMResp(rq);
	sendResponse(rsp);
	logger.info("deliver_sm_resp sent.");
    }

    /** Send an smpp request to the SMSC.
      * No fields in the SMPPRequest packet will be altered except for the
      * sequence number. The sequence number of the packet will be set by this
      * method according to the numbering maintained by this Connection
      * object. The numbering policy is to start at 1 and increment by 1 for
      * each packet sent.
      * @param r The request packet to send to the SMSC
      * @return The response packet returned by the SMSC, or null if
      * asynchronous communication is being used.
      * @throws java.lang.NullPointerException if <code>r</code> is null.
      * @throws java.net.SocketTimeoutException If a socket timeout occurs while
      * waiting for a response packet. (Only in synchronized mode).
      * @throws java.io.IOException If an I/O error occurs while writing the
      * request packet to the network connection.
      * @throws ie.omk.smpp.UnsupportedOperationException if the negotiated
      * version of the SMPP link does not support the request type
      * <code>r</code>.
      * @throws ie.omk.smpp.AlreadyBoundException If the request type is a bind
      * packet and this connection is already bound.
      */
    public SMPPResponse sendRequest(SMPPRequest r)
	throws java.net.SocketTimeoutException, java.io.IOException, UnsupportedOperationException, AlreadyBoundException
    {
	if (link == null)
	    throw new IOException("Connection to the SMSC is not open.");

	int id = r.getCommandId();

	// Check the command is supported by the interface version..
	if (!this.interfaceVersion.isSupported(id)) {
	    throw new UnsupportedOperationException("Command ID 0x"
		    + Integer.toHexString(id)
		    + " is not supported by SMPP "
		    + this.interfaceVersion);
	}
	
	// Very few request types allowed by a receiver connection.
	if (connectionType == RECEIVER) {
	    if (id != SMPPPacket.ENQUIRE_LINK && id != SMPPPacket.UNBIND)
		throw new UnsupportedOperationException(
			"Operation not permitted "
			+ "over receiver connection");
	}

	SMPPPacket resp = null;

	if (this.seqNumScheme != null)
	    r.setSequenceNum(this.seqNumScheme.nextNumber());

	// Special packet handling..
	if (id == SMPPPacket.BIND_TRANSMITTER
		|| id == SMPPPacket.BIND_RECEIVER
		|| id == SMPPPacket.BIND_TRANSCEIVER) {
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

	try {
	    id = -1;
	    link.write(r);

	    if (!asyncComms) {
		resp = readNextPacketInternal();
		id = resp.getCommandId();
		if(!(resp instanceof SMPPResponse)) {
		    logger.warn("Packet received from the SMSC is not a response");
		}
	    }
	} catch (java.net.SocketTimeoutException x) {
	    // Must set our state and re-throw the exception..
	    logger.error("Received a socket timeout exception", x);
	    setState(UNBOUND);
	    throw x;
	}

	// Special!
	if (id == SMPPPacket.BIND_TRANSMITTER_RESP
		|| id == SMPPPacket.BIND_RECEIVER_RESP
		|| id == SMPPPacket.BIND_TRANSCEIVER_RESP) {
	    handleBindResp((BindResp)resp);
	}

	return ((SMPPResponse)resp);
    }

    /** Send an smpp response packet to the SMSC
      * @param r The response packet to send to the SMSC
      * @throws ie.omk.smpp.NoSuchRequestException if the response contains a
      * sequence number of a request this connection has not seen.
      * @throws java.io.IOException If an I/O error occurs while writing the
      * response packet to the output stream.
      */
    public void sendResponse(SMPPResponse resp)
	throws java.io.IOException
    {
	Integer key = null;

	if (link == null)
	    throw new IOException("Connection to SMSC is not valid.");

	try {
	    link.write(resp);
	} catch (java.net.SocketTimeoutException x) {
	    logger.warn("Got a socket timeout exception", x);
	    setState(UNBOUND);
	    throw x;
	}

	if (resp.getCommandId() == SMPPPacket.UNBIND_RESP
		&& resp.getCommandStatus() == 0)
	    setState(UNBOUND);
    }

    /** Bind this connection to the SMSC. An application must bind to an SMSC as
     * one of transmitter, receiver or transceiver. Binding as transmitter
     * allows general manipulation of messages at the SMSC including submitting
     * messages for delivery, cancelling, replacing and querying the state of
     * previously submitted messages. Binding as a receiver allows an
     * application to receive all messages previously queued for delivery to
     * it's address. The transceiver mode, which was added in version 3.4 of the
     * SMPP protocol, combines the functionality of both transmitter and
     * receiver into one connection type.
     * <p>Note that it is only necessary to supply values for
     * <code>type, systemID</code> and <code>password</code>. The other
     * arguments may be left at null (or zero, as applicable).</p>
     * @param type connection type to use, either {@link #TRANSMITTER},
     * {@link #RECEIVER} or {@link #TRANSCEIVER}.
     * @param systemID the system ID to identify as to the SMSC.
     * @param password password to use to authenticate to the SMSC.
     * @param systemType the system type to bind as.
     * @return the bind response packet.
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
     */
    public BindResp bind(int type,
	    String systemID,
	    String password,
	    String systemType)
	throws java.io.IOException, UnsupportedOperationException, StringTooLongException, InvalidTONException, InvalidNPIException, IllegalArgumentException, AlreadyBoundException
    {
	try {
	    return (this.bind(type, systemID, password, systemType, 
			0, 0, null));
	} catch (InvalidTONException x) {
	    logger.warn("Invalid TON in bind", x);
	    return (null);
	} catch (InvalidNPIException x) {
	    logger.warn("Invalid NPI in bind", x);
	    return (null);
	}
    }

    /** Bind this connection to the SMSC. An application must bind to an SMSC as
     * one of transmitter, receiver or transceiver. Binding as transmitter
     * allows general manipulation of messages at the SMSC including submitting
     * messages for delivery, cancelling, replacing and querying the state of
     * previously submitted messages. Binding as a receiver allows an
     * application to receive all messages previously queued for delivery to
     * it's address. The transceiver mode, which was added in version 3.4 of the
     * SMPP protocol, combines the functionality of both transmitter and
     * receiver into one connection type.
     * <p>Note that it is only necessary to supply values for
     * <code>type, systemID</code> and <code>password</code>. The other
     * arguments may be left at null (or zero, as applicable).</p>
     * @param type connection type to use, either {@link #TRANSMITTER},
     * {@link #RECEIVER} or {@link #TRANSCEIVER}.
     * @param systemID the system ID to identify as to the SMSC.
     * @param password password to use to authenticate to the SMSC.
     * @param systemType the system type to bind as.
     * @param typeOfNum the TON of the address to bind as.
     * @param numberPlan the NPI of the address to bind as.
     * @param addrRange the address range regular expression to bind as.
     * @return the bind response packet.
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
     */
    public BindResp bind(int type,
	    String systemID,
	    String password,
	    String systemType,
	    int typeOfNum,
	    int numberPlan,
	    String addrRange)
	throws java.io.IOException, UnsupportedOperationException, StringTooLongException, InvalidTONException, InvalidNPIException, IllegalArgumentException, AlreadyBoundException
    {
	Bind bindReq = null;

	switch (type) {
	case TRANSMITTER:
	    bindReq = new BindTransmitter();
	    break;
	    
	case RECEIVER:
	    bindReq = new BindReceiver();
	    break;

	case TRANSCEIVER:
	    if (this.interfaceVersion.isOlder(SMPPVersion.V34)) {
		throw new UnsupportedOperationException("Cannot bind as "
			+ "transceiver in SMPP "
			+ interfaceVersion);
	    }
	    bindReq = new BindTransceiver();
	    break;

	default:
	    throw new IllegalArgumentException("No such connection type.");
	}

	logger.info("Binding to the SMSC as type " + type);

	bindReq.setSystemId(systemID);
	bindReq.setPassword(password);
	bindReq.setSystemType(systemType);
	bindReq.setAddressTon(typeOfNum);
	bindReq.setAddressNpi(numberPlan);
	bindReq.setAddressRange(addrRange);
	try {
	    bindReq.setInterfaceVersion(this.interfaceVersion.getVersionID());
	} catch (BadInterfaceVersionException x) {
	    // This cannot be...we're using our own enumeration!!
	    throw new RuntimeException("Please notify project admin that "
		    + "there's a coding error in Connection.java!"
		    + "\nAnd attach this stack trace.");
	}

	return ((BindResp)sendRequest(bindReq));
    }


    /** Unbind from the SMSC. This method will unbind the SMPP protocol
     * connection from the SMSC. No further SMPP operations will be possible
     * once unbound, a new bind packet will need to be send to the SMSC. Note
     * that this method will <b>not</b> close the underlying network connection.
     * @return The Unbind response packet, or null if asynchronous
     * communication is being used.
     * @throws ie.omk.smpp.NotBoundException if the connection is not yet
     * bound.
     * @throws java.io.IOException If an I/O error occurs while writing the
     * unbind request or reading the unbind response.
     */
    public UnbindResp unbind()
	throws java.io.IOException, NotBoundException
    {
	if((state != BOUND) || !(link.isConnected()))
	    throw new NotBoundException();

	/* If this is set, the run() method will return when an
	 * unbind response packet arrives, stopping the listener
	 * thread. (after all observers have been notified of the packet)
	 */
	setState(UNBINDING);

	logger.info("Unbinding from the SMSC");
	Unbind u = new Unbind();
	return ((UnbindResp)sendRequest(u));
    }

    /** Unbind from the SMSC. This method is used to acknowledge an unbind
      * request from the SMSC.
      * @throws ie.omk.smpp.NotBoundException if the connection is not currently
      * bound.
      * @throws ie.omk.smpp.AlreadyBoundException if no unbind request has
      * been received from the SMSC.
      * @throws java.io.IOException If an I/O error occurs while writing the
      * response packet to the network connection.
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

    /** Force the SMPP connection down.
     * Only use this method once it's full sure that graceful unbinding and
     * disconnection isn't going to work. This method cleans up it's internal
     * state, forcing the network connection closed and terminating the receiver
     * thread if necessary.
     * <p>If you end up having to use this method to terminate a Connection, it
     * is advisable not to attempt to reuse the connection at all. Create a new
     * object and start from scratch. Use of this method indicates something
     * seriously wrong!</p>
     */
    public void force_unbind()
    {
	logger.warn("Attempting to force SMPP connection down.");
	try {
	    setState(UNBOUND);

	    // The thread must DIE!!!!
	    if(rcvThread != null && rcvThread.isAlive()) {
		try {
		    // Wait to see if the thread will terminate due to the state
		    // becoming UNBOUND.
		    Thread.sleep(1000);
		} catch (InterruptedException x) {
		    logger.debug("Interrupted exception waiting on receiver to die", x);
		}
		if (rcvThread.isAlive())
		    logger.error("Listener thread has not died.");

		rcvThread = null;
	    }

	    link.close();
	} catch(IOException ix) {
	    logger.warn("Forced unbind caused IO exception", ix);
	}
	return;
    }

    /** Acknowledge an EnquireLink received from the Smsc
      * @throws java.io.IOException If an I/O error occurs while writing to the
      * network connection.
      */
    public void ackEnquireLink(EnquireLink rq)
	throws java.io.IOException
    {
	EnquireLinkResp resp = new EnquireLinkResp(rq);
	sendResponse(resp);
	logger.info("enquire_link_resp sent.");
    }

    /** Do a confidence check on the SMPP link to the SMSC.
      * @return The Enquire link response packet or null if asynchronous
      * communication is in use.
      * @throws java.io.IOException If an I/O error occurs while writing to the
      * network connection.
      */
    public EnquireLinkResp enquireLink()
	throws java.io.IOException
    {
	EnquireLink s = new EnquireLink();
	SMPPResponse resp = sendRequest(s);
	logger.debug("enquire_link request sent.");
	if (resp != null)
	    logger.debug("enquire_link_response received.");
	return ((EnquireLinkResp)resp);
    }

    /** Get the type of this SMPP connection. The connection type is one of
     * TRANSMITTER, RECEIVER or TRANSCEIVER.
     */
    public int getConnectionType()
    {
	return (connectionType);
    }

    /** Report whether the connection is bound or not.
      * @return true if the connection is bound.
      */
    public boolean isBound()
    {
	return (state == BOUND);
    }

    /** Reset this connection's sequence numbering to the beginning. The
     * underlying SequenceNumberScheme's reset method is called to start from
     * that sequence's 'beginning'.
     * @throws ie.omk.smpp.AlreadyBoundException if the connection is
     * currently bound to the SMSC.
     */
    public void reset()
	throws AlreadyBoundException
    {
	if(state == BOUND) {
	    logger.warn("Attempt to reset sequence numbering on a bound connection");
	    throw new AlreadyBoundException("Cannot reset connection while bound");
	}

	if (this.seqNumScheme != null)
	    this.seqNumScheme.reset();

	logger.info("Sequence numbering reset.");
    }

    /** Set the sequence numbering scheme for this connection. A sequence
     * numbering scheme determines what sequence number each SMPP packet will
     * have. By default, {@link ie.omk.smpp.util.DefaultSequenceScheme} is used,
     * which will begin with sequence number 1 and increase the number by 1 for
     * each packet thereafter.
     * <p>If the application sets the <code>scheme</code> to null, it is
     * responsible for maintaining and setting the sequence number for each SMPP
     * request it sends to the SMSC.
     * @see ie.omk.smpp.util.SequenceNumberScheme
     * @see ie.omk.smpp.message.SMPPPacket#setSequenceNum
     */
    public void setSeqNumScheme(SequenceNumberScheme scheme) {
	this.seqNumScheme = scheme;
    }

    /** Get the current sequence numbering scheme object being used by this
     * connection.
     */
    public SequenceNumberScheme getSeqNumScheme() {
	return (this.seqNumScheme);
    }

    /** Read in the next packet from the SMSC link.
      * If asynchronous communications is in use, calling this method results in
      * an SMPPException as the listener thread will be hogging the input stream
      * of the socket connection.
      * @return The next SMPP packet from the SMSC.
      * @throws java.io.IOException If an I/O error occurs while reading from
      * the network connection.
      * @throws ie.omk.smpp.InvalidOperationException If asynchronous comms
      * is in use.
      */
    public SMPPPacket readNextPacket()
	throws java.io.IOException, InvalidOperationException
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
      * @throws java.io.IOException If an I/O error occurs while reading from
      * the network connection.
      */
    private SMPPPacket readNextPacketInternal()
	throws java.io.IOException
    {
	SMPPPacket pak = null;
	int id = -1, st = -1;

	this.buf = link.read(this.buf);
	id = SMPPIO.bytesToInt(this.buf, 4, 4);
	if (logger.isDebugEnabled())
	    logger.debug("Packet received: "
		    + Integer.toHexString(id));

	pak = PacketFactory.newPacket(id);

	if (pak != null) {
	    pak.readFrom(this.buf, 0);

	    // Special case handling for certain packet types..
	    st = pak.getCommandStatus();
	    switch (pak.getCommandId()) {
	    case SMPPPacket.BIND_TRANSMITTER_RESP:
	    case SMPPPacket.BIND_RECEIVER_RESP:
	    case SMPPPacket.BIND_TRANSCEIVER_RESP:
		handleBindResp((BindResp)pak);
		break;

	    case SMPPPacket.UNBIND_RESP:
		handleUnbindResp((UnbindResp)pak);
		break;

	    case SMPPPacket.UNBIND:
		handleUnbind((Unbind)pak);
		break;
	    }
	}

	return (pak);
    }

    /** Handle an incoming bind response packet. Method is called by a few
     * methods in this class that read from the incoming connection.
     */
    private void handleBindResp(BindResp resp) {
	if (state == BINDING && resp.getCommandStatus() == 0)
	    setState(BOUND);
    }

    /** Handle an incoming unbind packet.
     */
    private void handleUnbind(Unbind req) {
	logger.info("SMSC requested unbind");
	setState(UNBINDING);
    }

    /** Handle an incoming unbind response packet.
     */
    private void handleUnbindResp(UnbindResp resp) {
	if (state == UNBINDING && resp.getCommandStatus() == 0) {
	    logger.info("Successfully unbound");
	    setState(UNBOUND);
	}
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

    /** Remove a connection observer from this Connection.
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
	    logger.debug("singleObserver was removed before packet notification");
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
	    logger.debug("singleObserver was removed before packet notification");
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

	logger.info("Receiver thread started");
	notifyObservers(new ReceiverStartEvent(this));
	try {
	    while (state != UNBOUND) {
		try {
		    pak = readNextPacketInternal();
		    if (pak == null) {
			// XXX Send an event to the application??
			continue;
		    }
		} catch (SocketTimeoutException x) {
		    // is it okay to ignore this ??
		    logger.info("Ignoring SocketTimeoutException");
		} catch (IOException x) {
		    // catch EOFException before this
		    logger.info("I/O Exception caught", x);
		    ReceiverExceptionEvent ev =
			new ReceiverExceptionEvent(this, x, state);
		    smppEx++;
		    if (smppEx > 5) {
			logger.warn("Too many IO exceptions in receiver thread", x);
			throw x;
		    }
		}

		id = pak.getCommandId();
		st = pak.getCommandStatus();

		// Handle special case packets..
		switch (id) {
		case SMPPPacket.DELIVER_SM:
		    if (ackDeliverSm)
			ackDeliverSm((DeliverSM)pak);
		    break;

		case SMPPPacket.ENQUIRE_LINK:
		    if (ackQryLinks)
			ackLinkQuery((EnquireLink)pak);
		    break;
		}

		// Tell all the observers about the new packet
		logger.info("Notifying observers of packet received");
		notifyObservers(pak);
	    } // end while

	    // Notify observers that the thread is exiting with no error..
	    exitEvent = new ReceiverExitEvent(this, null, state);
	} catch (Exception x) {
	    logger.error("Fatal exception in receiver thread", x);
	    exitEvent = new ReceiverExitEvent(this, x, state);
	    setState(UNBOUND);
	} finally {
	    // make sure other code doesn't try to restart the rcvThread..
	    rcvThread = null;
	}

	if (exitEvent != null)
	    notifyObservers(exitEvent);
    }

    /**
     * @deprecated #ackEnquireLink
     */
    public void ackLinkQuery(EnquireLink el)
	throws java.io.IOException
    {
	ackEnquireLink(el);
    }
}
