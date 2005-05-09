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

import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.EventDispatcher;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.ReceiverStartEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.event.SimpleEventDispatcher;
import ie.omk.smpp.message.Bind;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.BindTransceiver;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.InvalidParameterValueException;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SMPPRequest;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.net.SmscLink;
import ie.omk.smpp.net.TcpLink;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.DefaultSequenceScheme;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.PropertyNotFoundException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.SequenceNumberScheme;
import ie.omk.smpp.version.SMPPVersion;
import ie.omk.smpp.version.VersionException;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** SMPP client connection (ESME). An SMPP Connection represents any kind of
 * connection to the SMSC, be it a transmitter, receiver or transceiver. It also
 * supports both synchronous and asynchronous modes of communication.
 * Synchronous mode is only useful for very simple applications that use
 * single-threading. Asynchronous mode is recommended for more complex
 * applications, especially those that will be running many threads.
 * <p><b>Important Note</b>: if you wish to use synchronous mode in a
 * multi-threaded environment, it is the <u>application's</u> responsiblity to
 * ensure there is only one thread executing a call to either or both of the
 * <code>sendRequest</code> and <code>readNextPacket</code> methods. If there
 * are concurrent calls to these methods executing, there is a strong
 * possibility of the incorrect packet being returned in a particular thread and
 * both the API and the application getting confused. These are the only methods
 * that contain such a race condition.</p>
 * @author Oran Kelly
 * @version 1.0
 */
public class Connection
    implements java.lang.Runnable
{
    /** Get the logger for this Connection. */
    protected Log logger = LogFactory.getLog(Connection.class);

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

    /** Queue of incoming packets to deliver to application before reading from
     * the network. The queue is used only in syncrhonized mode. In the case
     * where an application has sent a request to the SMSC and is blocked
     * waiting a response and the SMSC initiates a request of it's own (an
     * unbind request or an enquire_link), the API will cache the request packet
     * and wait for the response to it's packet. Any other type of packet will
     * be added to the packetQueue and subsequent calls to
     * <code>readNextPacket</code> will clear this queue.
     */
    private ArrayList		packetQueue = null;

    /** Object used to notify observers of SMPP events.
     */
    private EventDispatcher eventDispatcher = null;

    /** Byte buffer used in readNextPacketInternal. */
    private byte[]		buf = new byte[300];

    /** Sequence numbering scheme to use for this connection. */
    private SequenceNumberScheme seqNumScheme = new DefaultSequenceScheme();

    /** The network link (virtual circuit) to the SMSC */
    private SmscLink		link = null;

    /** SMPP protocol version number.
     */
    protected SMPPVersion	interfaceVersion = SMPPVersion.getDefaultVersion();

    /** Does the remote end support optional parameters?
     * According to the SMPPv3.4 specification, if the SMSC does not return
     * the sc_interface_version optional parameter in its bind response
     * packet, then we must assume it does not support optional parameters.
     */
    protected boolean supportOptionalParams = true;
    
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

    /** The default alphabet to use for this connection.
     */
    protected AlphabetEncoding defaultAlphabet = null;

    /** Initialise a new SMPP connection object. This is a convenience
     * constructor that will create a new {@link ie.omk.smpp.net.TcpLink} object
     * using the host name and port provided. The connection created will use
     * synchronous communications.
     * @param host the hostname of the SMSC.
     * @param port the port to connect to. If 0, use the default SMPP port
     * number.
     */
    public Connection(String host, int port)
	throws java.net.UnknownHostException
    {
	this (new TcpLink(host, port), false);
    }

    /** Initialise a new SMPP connection object. This is a convenience
     * constructor that will create a new {@link ie.omk.smpp.net.TcpLink} object
     * using the host name and port provided.
     * @param host the hostname of the SMSC.
     * @param port the port to connect to. If 0, use the default SMPP port
     * number.
     * @param async true for asyncronous communication, false for synchronous.
     */
    public Connection(String host, int port, boolean async) throws java.net.UnknownHostException {
	this (new TcpLink(host, port), async);
    }

    /** Initialise a new SMPP connection object. The connection will use
     * synchronous communications.
     * @param link The network link object to the Smsc (cannot be null)
     */
    public Connection(SmscLink link)
    {
	this (link, false);
    }

    /** Initialise a new SMPP connection object, specifying the type of
     * communication desired. See the {@link Connection} class description for
     * some required knowledge on using the Connection in syncrhonous mode.
     * @param link The network link object to the Smsc (cannot be null)
     * @param async true for asyncronous communication, false for synchronous.
     */
    public Connection(SmscLink link, boolean async)
    {
	this.link = link;
	this.asyncComms = async;

	if (asyncComms) {
	    initAsyncComms();
	} else {
	    initSyncComms();
	}
    }

    private void initAsyncComms() {
	String className = "";
	try {
	    className = APIConfig.getInstance().getProperty(APIConfig.EVENT_DISPATCHER_CLASS);
	    if (className != null && !"".equals(className)) {
		Class cl = Class.forName(className);
		eventDispatcher = (EventDispatcher)cl.newInstance();
	    } else {
		logger.info("EventDispatcher property value is empty.");
	    }
	} catch (PropertyNotFoundException x) {
	    logger.debug("No event dispatcher specified in properties. Using default.");
	} catch (ClassNotFoundException x) {
	    logger.error("Cannot locate event dispatcher class " + className, x);
	} catch (Exception x) {
	    // Something wrong with the implementation.
	    StringBuffer errorMessage = new StringBuffer();
	    errorMessage.append("Event dispatcher implementation is ")
		.append("incorrect.\n")
		.append(className)
		.append(" should implement ie.omk.smpp.event.EventDispatcher ")
		.append("and have a default constructor.\n")
		.append("Falling back to default implementation after ")
		.append("receiving exception.");
	    logger.error(errorMessage.toString(), x);
	} finally {
	    if (eventDispatcher == null)
		eventDispatcher = new SimpleEventDispatcher();
	}

	// Initialise the event dispatcher
	eventDispatcher.init();

	// Create the receiver daemon thread.
	createRecvThread();
    }

    private void initSyncComms() {
	packetQueue = new ArrayList();
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

    /** Set the default alphabet of the SMSC this <code>Connection</code> is
     * communicating with. Each SMSC has its own default alphabet it uses. When
     * messages arrive and announce themselves with a data coding value of zero,
     * that means the message is encoded in the SMSC's default alphabet. The
     * smppapi assumes the GSM default alphabet as it's default alphabet.  By
     * setting the default alphabet on the <code>Connection</code> all packets
     * returned by {@link #newInstance(int)} will use the Connection's default
     * alphabet plus any packets read from the wire with a data coding value of
     * zero will have their default alphabet initialised appropriately.
     * @param alphabet the alphabet to use as the default for this connection
     * (may be <code>null</code> in which case the API falls back to using its
     * own internal default).
     */
    public void setDefaultAlphabet(AlphabetEncoding alphabet) {
	this.defaultAlphabet = alphabet;
    }

    /** Get the current alphabet this <code>Connection</code> is using as its
     * default.
     * @return the default alphabet for this <code>Connection</code>.
     */
    public AlphabetEncoding getDefaultAlphabet() {
	return (defaultAlphabet);
    }

    /** Set the state of this ESME.
      * @see ie.omk.smpp.Connection#getState
      */
    private synchronized void setState(int state)
    {
	logger.info("Setting state " + state);
	this.state = state;
    }

    /** Set the SMPP version this connection will use. Setting the version is
     * only a valid operation before the connection is bound. Any attempt to set
     * the version after binding to the SMSC will result in an exception being
     * thrown.
     * @param version the SMPP version to use.
     * @throws ie.omk.smpp.version.VersionException if an attempt is made to set
     * the version of the connection after binding to the SMSC.
     * @see ie.omk.smpp.version.SMPPVersion
     */
    public void setVersion(SMPPVersion version) throws VersionException {
	if (getState() != UNBOUND)
	    throw new VersionException("Cannot set SMPP version after binding");

	if (version == null)
	    this.interfaceVersion = SMPPVersion.getDefaultVersion();
	else
	    this.interfaceVersion = version;
    }

    /** Get the SMPP version in use by this connection. The version in use by
     * the connection <b>may</b> be different to that specified before the bind
     * operation as binding to the SMSC may result in an alternative SMPP
     * version being negotiated. For instance, if the client sends a bind packet
     * to the SMSC specifying that it supports SMPP version 3.4 but the SMSC
     * returns a bind_resp stating it supports version 3.3, the Connection
     * automatically sets it's internal version to use down to 3.3.
     */
    public SMPPVersion getVersion() {
	return (interfaceVersion);
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


    /** Close the underlying network link to the SMSC. This method calls the
     * underlying link's <code>close</code> method to actually shutdown
     * the network connection to the SMSC.
     * @throws ie.omk.smpp.IllegalStateException if an attempt is made to close
     * the connection while bound to the SMSC.
     * @throws java.io.IOException if an I/O exception occurs while trying to
     * close the link.
     * @see ie.omk.smpp.net.SmscLink#close
     */
    public void closeLink() throws IOException {
	if (getState() != UNBOUND)
	    throw new IllegalStateException("Cannot close the link while bound to the SMSC");

	if (this.link.isConnected()) {
	    logger.info("Shutting down the network link");
	    this.link.close();
	} else {
	    logger.debug("closeLink called on an unopen connection");
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
     * version is 3.4. The bind operation may negotiate an eariler version of
     * the protocol if the SC does not understand the version sent by the ESME.
     * This API will not support any version eariler than SMPP v3.3. The
     * interface version is encoded as follows:
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
        this.supportOptionalParams = interfaceVersion.isSupportOptionalParams();
    }


    /** Set the behaviour of automatically acking ENQUIRE_LINK's from the SMSC.
      * By default, the listener thread will automatically ack an enquire_link
      * message from the Smsc so as not to lose the connection.  This
      * can be turned off with this method.
      * @param b true to activate automatic acknowledgment, false to disable
      */
    public void autoAckLink(boolean b)
    {
	this.ackQryLinks = b;
    }

    /** Set the behaviour of automatically acking Deliver_Sm's from the Smsc.
      * By default the listener thread will <b>not</b> acknowledge a message.
      * @param b true to activate this function, false to deactivate.
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
      * No fields in the SMPPRequest packet will be altered except possibly the
      * sequence number. The sequence number will be assigned the next number as
      * defined by this Connection's sequence numbering scheme. If the sequence
      * numbering scheme class is <code>null</code> for this Connection, no
      * number will be assigned. By default, the
      * {@link ie.omk.smpp.util.DefaultSequenceScheme} class is used to assign
      * sequence numbers to packets.<br />
      * <b>IMPORTANT</b>: You <i>must</i> use the <code>bind</code> and
      * <code>unbind</code> methods to carry out those operations. Attempting
      * to send an bind or unbind packet using this method will result in an
      * <code>UnsupportedOperationException</code> being thrown.
      * @param r The request packet to send to the SMSC
      * @return The response packet returned by the SMSC, or null if
      * asynchronous communication is being used.
      * @throws java.lang.NullPointerException if <code>r</code> is null.
      * @throws java.net.SocketTimeoutException If a socket timeout occurs while
      * waiting for a response packet. (Only in synchronized mode).
      * @throws java.io.IOException If an I/O error occurs while writing the
      * request packet to the network connection.
      * @throws ie.omk.smpp.UnsupportedOperationException If this connection
      * type does not support operation <code>r</code>. For example, a receiver
      * link does not support the submit_sm operation.
      * @throws ie.omk.smpp.AlreadyBoundException If the request type is a bind
      * packet and this connection is already bound.
      * @throws ie.omk.smpp.message.SMPPProtocolException If synchronous
      * communications is in use and the incoming response packet violates the
      * SMPP specification, this exception will be thrown.
      * @see #setSeqNumScheme
      */
    public SMPPResponse sendRequest(SMPPRequest r)
	throws java.net.SocketTimeoutException, java.io.IOException, AlreadyBoundException, VersionException, SMPPProtocolException, UnsupportedOperationException
    {
	SMPPResponse resp = null;
	int id = r.getCommandId();

	if (this.state != BOUND)
	    throw new NotBoundException("Must be bound to the SMSC before "
                    + "sending packets");

	    // Force applications to use bind and unbind
        if (id == SMPPPacket.BIND_RECEIVER || id == SMPPPacket.BIND_TRANSCEIVER
                || id == SMPPPacket.BIND_TRANSMITTER || id == SMPPPacket.UNBIND) {
            throw new UnsupportedOperationException("You must use the bind and unbind methods to send those requests");
        }
        
	// Very few request types allowed by a receiver connection.
	if (connectionType == RECEIVER) {
	    if (id != SMPPPacket.ENQUIRE_LINK)
		throw new UnsupportedOperationException(
			"Operation not permitted over receiver connection");
	}

	return (sendRequestInternal(r));
    }

    /** Send a request to the SMSC.
     * XXX complete javadoc for this method.
     * @throws ie.omk.smpp.version.VersionException if the version in use
     * does not support the request being sent.
     */
    protected synchronized SMPPResponse sendRequestInternal(SMPPRequest r)
    throws java.net.SocketTimeoutException, java.io.IOException, AlreadyBoundException, VersionException, SMPPProtocolException {
	SMPPResponse resp = null;
	int id = r.getCommandId();

	if (link == null)
	    throw new IOException("No SMSC connection.");

	// Check the command is supported by the interface version..
	if (!this.interfaceVersion.isSupported(id)) {
	    throw new VersionException("Command ID 0x"
		    + Integer.toHexString(id)
		    + " is not supported by SMPP "
		    + this.interfaceVersion);
	}

	if (id == SMPPPacket.BIND_TRANSMITTER
		|| id == SMPPPacket.BIND_RECEIVER
		|| id == SMPPPacket.BIND_TRANSCEIVER) {
	    if (this.state != UNBOUND)
		throw new AlreadyBoundException("Already bound to the SMSC");

	    openLink();

	    setState(BINDING);
	    if (asyncComms) {
		if (rcvThread == null)
		    createRecvThread();

        // Set the socket timeout to the bind timeout
        try {
            long bindTimeout = APIConfig.getInstance().getLong(APIConfig.BIND_TIMEOUT, 0L);
            if (bindTimeout > 0L) {
                this.link.setTimeout(bindTimeout);
                if (logger.isDebugEnabled()) {
                    logger.debug("Set bind timeout to " + bindTimeout);
                }
            }
        } catch (UnsupportedOperationException x) {
            logger.warn("Link does not support read timeouts - bind timeout will not work");
        }
        
		if (!rcvThread.isAlive())
		    rcvThread.start();
	    }
	} else if (id == SMPPPacket.UNBIND) {
	    if (!asyncComms && packetQueue.size() > 0)
		throw new IllegalStateException("Cannot unbind while there are incoming packets "
			+ "waiting responses");

	    if (this.state != BOUND)
		throw new IllegalStateException("Not currently bound");

	    setState(UNBINDING);
	}

	// XXX temporary...this should be removed once it's safe to assume that
	// no packet can be submitted that has been constructed outside the
	// confines of this connection's factory method.
	if (r.getSequenceNum() == 0 && seqNumScheme != null)
	    r.setSequenceNum(seqNumScheme.nextNumber());

	link.write(r, this.supportOptionalParams);
	if (!asyncComms)
	    resp = waitForResponsePacket(r);

	return ((SMPPResponse)resp);
    }

    /** Wait for a response packet from the SMSC. A response packet with the
     * same sequence number as <code>req</code> will be waited for from the
     * SMSC. If an unexpected packet arrives, either a response packet with a
     * different sequence number or a request packet, it will be queued for
     * later retrieval by the application via <code>readNextPacket</code>.
     * @param req the request packet to wait for a response to.
     * @return A response packet with the same sequence number as
     * <code>req</code>.
     * @throws java.net.SocketTimeoutException if the read on the socket times
     * out.
     * @see #readNextPacket
     * @see java.net.SocketTimeoutException
     */
    protected synchronized SMPPResponse waitForResponsePacket(SMPPPacket req)
    throws java.net.SocketTimeoutException, java.io.IOException, SMPPProtocolException {
	try {
	    int id = -1;
	    SMPPPacket resp = null;

	    int expectedSeq = req.getSequenceNum();
	    while (true) {
		resp = readNextPacketInternal();
		id = resp.getCommandId();

		if ((id & 0x80000000) != 0
			&& resp.getSequenceNum() == expectedSeq) {
		    break;
		} else {
		    logger.info("Queuing unexpected sequence numbered packet.");
		    if (logger.isDebugEnabled()) {
			StringBuffer b = new StringBuffer("Expected:")
			    .append(Integer.toString(expectedSeq))
			    .append(" but got ")
			    .append(Integer.toString(resp.getSequenceNum()))
                            .append(" type: ")
                            .append(Integer.toString(resp.getCommandId()));
			logger.debug(b.toString());
		    }
		    packetQueue.add(resp);
		}
	    }

	    return ((SMPPResponse)resp);
	} catch (java.net.SocketTimeoutException x) {
	    // Must set our state and re-throw the exception..
	    logger.error("Received a socket timeout exception", x);
	    throw x;
	}
    }

    /** Determine if there are packets available for reading using
     * <code>readNextPacket</code>. This method is only valid for synchronous
     * communications...it will always return 0 if asynchronous mode is in
     * use.
     * @return 0 if there is no packet available for reading, 1 if there is data
     * available but the call to <code>readNextPacket</code> may block or 2 if
     * there is a full packet available.
     */
    public int packetAvailable() {
	int ret = 0;

	if (!asyncComms) {
	    if (packetQueue.size() > 0)
		ret = 2;
	    else if (link.available() > 0)
		ret = 1;
	}

	return (ret);
    }

    /** Send an smpp response packet to the SMSC
      * @param resp The response packet to send to the SMSC
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
	    link.write(resp, this.supportOptionalParams);
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
     * <p>The connection object will negotiate the appropriate version for the
     * protocol link at bind time. If the SMSC returns the SC_INTERFACE_VERSION
     * optional parameter in its bind response packet, the
     * <code>Connection</code> will read it. If the version stated by the SMSC
     * is older than the current version setting of the <code>Connection</code>
     * then the <code>Connection</code>'s version will be downgraded to that of
     * the SMSC's. Otherwise, the current version will be left alone. An SMSC
     * supporting only version 3.3 of the API will never be able to return this
     * optional parameter (as they were only introduced in version 3.4). The
     * <code>Connection</code> will therefore assume the version to be 3.3 if it
     * does not receive the SC_INTERFACE_VERSION in the bind response packet.
     * If your SMSC does support a higher version but does not supply the
     * SC_INTERFACE_VERSION in its bind response, you will need to use
     * {@link #setInterfaceVersion} after the bind operation is complete.</p>
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
     * @throws ie.omk.smpp.VersionException if an attempt is made
     * to bind as transceiver while using SMPP version 3.3.
     * @throws ie.omk.smpp.InvalidParameterValueException If any of systemID,
     * password, system type or address range are outside allowed bounds or the
     * TON or NPI is invalid.
     * @throws java.io.IOException If an I/O error occurs while writing the bind
     * packet to the output stream.
     * @throws ie.omk.smpp.AlreadyBoundException If the Connection is already
     * bound.
     * @throws ie.omk.smpp.SMPPProtocolExcpetion if synchronous communication is
     * in use and the incoming response packet violates the SMPP protocol.
     */
    public BindResp bind(int type,
	    String systemID,
	    String password,
	    String systemType)
	throws java.io.IOException, InvalidParameterValueException, IllegalArgumentException, AlreadyBoundException, VersionException, SMPPProtocolException
    {
	return (this.bind(type, systemID, password, systemType, 0, 0, null));
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
     * @throws ie.omk.smpp.VersionException if an attempt is made
     * to bind as transceiver while using SMPP version 3.3.
     * @throws ie.omk.smpp.InvalidParameterValueException If any of systemID,
     * password, system type or address range are outside allowed bounds.
     * @throws java.io.IOException If an I/O error occurs while writing the bind
     * packet to the output stream.
     * @throws ie.omk.smpp.AlreadyBoundException If the Connection is already
     * bound.
     * @throws ie.omk.smpp.message.SMPPProtocolException If synchronous comms is
     * in use and the incoming bind response packet violates the SMPP
     * specification, this exception is thrown.
     */
    public BindResp bind(int type,
	    String systemID,
	    String password,
	    String systemType,
	    int typeOfNum,
	    int numberPlan,
	    String addrRange)
	throws java.io.IOException, InvalidParameterValueException, IllegalArgumentException, AlreadyBoundException, VersionException, SMPPProtocolException
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
		throw new VersionException("Cannot bind as "
			+ "transceiver "
			+ interfaceVersion.toString());
	    }
	    bindReq = new BindTransceiver();
	    break;

	default:
	    throw new IllegalArgumentException("No such connection type.");
	}

	this.connectionType = type;
	logger.info("Binding to the SMSC as type " + type);

	bindReq.setVersion(interfaceVersion);
	bindReq.setSystemId(systemID);
	bindReq.setPassword(password);
	bindReq.setSystemType(systemType);
	bindReq.setAddressTon(typeOfNum);
	bindReq.setAddressNpi(numberPlan);
	bindReq.setAddressRange(addrRange);
	return ((BindResp)sendRequestInternal(bindReq));
    }


    /** Unbind from the SMSC. This method will unbind the SMPP protocol
     * connection from the SMSC. No further SMPP operations will be possible
     * once unbound. If the calling application is using sync mode, it should be
     * sure there are no incoming packets awaiting a response (check using
     * {@link #packetAvailable}), otherwise an
     * <code>IllegalStateException</code> may be thrown. Note that this method
     * will <b>not</b> close the underlying network connection.
     * @return The Unbind response packet, or null if asynchronous
     * communication is being used.
     * @throws ie.omk.smpp.NotBoundException if the connection is not yet
     * bound.
     * @throws ie.omk.smpp.message.SMPPProtocolException If synchronous comms is
     * in use and the incoming unbind_resp packet violates the SMPP
     * specification, this exception is thrown.
     * @throws java.io.IOException If an I/O error occurs while writing the
     * unbind request or reading the unbind response.
     */
    public UnbindResp unbind()
	throws java.io.IOException, NotBoundException, SMPPProtocolException
    {
	if((state != BOUND) || !(link.isConnected()))
	    throw new NotBoundException();

	try {
	    logger.info("Unbinding from the SMSC");
	    Unbind u = (Unbind)newInstance(SMPPPacket.UNBIND);
	    return ((UnbindResp)sendRequestInternal(u));
	} catch (VersionException x) {
	    // impossible...every version supports unbind!
	    throw new SMPPRuntimeException("Internal smppapi error");
	} catch (BadCommandIDException x) {
	    // similarly impossible!
	    throw new SMPPRuntimeException("Internal smppapi error");
	}
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
		if (rcvThread != null)
		    logger.error("Listener thread has not died.");

		rcvThread = null;
	    }

	    link.close();
	} catch(Throwable t) {
	    logger.warn("Exception when trying to force unbind", t);
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
      * @throws ie.omk.smpp.message.SMPPProtocolException If synchronous
      * communications is in use and the incoming enquire_link_resp packet
      * violates the SMPP specification, this exception is thrown.
      */
    public EnquireLinkResp enquireLink()
	throws java.io.IOException, SMPPProtocolException
    {
	try {
	    EnquireLink s = (EnquireLink)newInstance(SMPPPacket.ENQUIRE_LINK);
	    SMPPResponse resp = sendRequest(s);
	    logger.debug("enquire_link request sent.");
	    if (resp != null)
		logger.debug("enquire_link_response received.");
	    return ((EnquireLinkResp)resp);
	} catch (BadCommandIDException x) {
	    throw new SMPPRuntimeException("Internal smppapi error");
	}
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
      * @throws ie.omk.smpp.message.SMPPProtocolException if the incoming data
      * violates the SMPP protocol specifications.
      */
    public SMPPPacket readNextPacket()
	throws java.io.IOException, InvalidOperationException, SMPPProtocolException
    {
	if (asyncComms) {
	    throw new InvalidOperationException("Asynchronous comms in use.");
	} else {
	    if (packetQueue.size() > 0)
		return ((SMPPPacket)packetQueue.remove(0));
	    else
		return (readNextPacketInternal());
	}
    }


    /** Read the next packet from the SMSC link. Internal version...handles
      * special case packets like bind responses and unbind request and
      * responses.
      * @return The read SMPP packet, or null if the connection timed out.
      * @throws java.io.IOException If an I/O error occurs while reading from
      * the network connection.
      */
    private synchronized SMPPPacket readNextPacketInternal()
	throws java.io.IOException, SMPPProtocolException
    {
	try {
	    SMPPPacket pak = null;
	    int id = -1, st = -1;

	    this.buf = link.read(this.buf);
	    id = SMPPIO.bytesToInt(this.buf, 4, 4);
	    pak = PacketFactory.newInstance(id);

	    if (pak != null) {
		pak.readFrom(this.buf, 0);
		if (logger.isDebugEnabled()) {
		    StringBuffer b = new StringBuffer("Packet Received: ");
		    int l = pak.getLength();
		    int s = pak.getCommandStatus();
		    int n = pak.getSequenceNum();
		    b.append("id:").append(Integer.toHexString(id));
		    b.append(" len:").append(Integer.toString(l));
		    b.append(" st:").append(Integer.toString(s));
		    b.append(" sq:").append(Integer.toString(n));
		    logger.debug(b.toString());
		}

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

		if (st == 0) {
		    // Fix up the alphabet for this connection type if the
		    // packet needs it. DCS value 0 means the alphabet is in the
		    // default encoding of the SMSC, which varies depending on
		    // implementation.
		    if (defaultAlphabet != null && pak.getDataCoding() == 0)
			pak.setAlphabet(defaultAlphabet);
		}
	    }

	    return (pak);
	} catch (BadCommandIDException x) {
	    throw new SMPPProtocolException("Unrecognised command received", x);
	}
    }

    /** Handle an incoming bind response packet. Method is called by a few
     * methods in this class that read from the incoming connection.
     */
    private void handleBindResp(BindResp resp) {
	int st = resp.getCommandStatus();

    // Throw an exception if we're not in a BINDING state..
    if (state != BINDING) {
        throw new IllegalStateException("A bind response was received in bound state " + state);
    }

    if (st != 0) {
        // Bind failed. Close the network link and return.
        
        if (logger.isDebugEnabled()) {
            logger.debug("Bind response indicates failure. Setting internal state to unbound.");
        }

        try {
    	    setState(UNBOUND);
            this.link.close();
        } catch (IOException x) {
            if (logger.isWarnEnabled()) {
                logger.warn("I/O Exception shutting down link after failed bind.", x);
            }
        }

        return;
    }

    // Alright so, we're bound to the SMSC..
    setState(BOUND);

	// Read the version of the protocol supported at the SMSC.
	Number n = (Number)resp.getOptionalParameter(Tag.SC_INTERFACE_VERSION);
	if (n != null) {
	    SMPPVersion smscVersion = SMPPVersion.getVersion(n.intValue());
	    if (logger.isDebugEnabled()) {
		logger.debug("SMSC reports its supported SMPP version as "
			+ smscVersion.toString());
	    }

	    // Downgrade this connection's version if the SMSC's version is
	    // lower.
	    if (smscVersion.isOlder(this.interfaceVersion)) {
		logger.info("Downgrading this connection's SMPP version to "
			+ smscVersion.toString());
		setInterfaceVersion(smscVersion);
	    }
	} else {
        // Spec requires us to assume the SMSC does not support optional
        // parameters
        this.supportOptionalParams = false;
        logger.warn("Disabling optional parameter support as no sc_interface_version parameter was received");
	}

        // Set the link timeout
        try {
            long linkTimeout = APIConfig.getInstance().getLong(APIConfig.LINK_TIMEOUT);
            link.setTimeout(linkTimeout);
            
            if (logger.isDebugEnabled()) {
                logger.debug("Set the link timeout to " + linkTimeout);
            }
        } catch (PropertyNotFoundException x) {
            if (logger.isDebugEnabled()) {
                logger.debug("No link timeout specified in configuration");
            }
        } catch (java.lang.UnsupportedOperationException x) {
            logger.warn("Configuration specified a link timeout but the link implementation does not support it");
        }
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
        try {
            if (state == UNBINDING && resp.getCommandStatus() == 0) {
                logger.info("Successfully unbound");
                setState(UNBOUND);
                this.link.close();
            }
        } catch (IOException x) {
        }
    }

    /** Set the event dispatcher for this connection object. Before using the
     * new event dispatcher, this method will call
     * {@link EventDispatcher#init} to initialise the dispatcher. It will then
     * iterate through all the observers registered with the current event
     * dispatcher and register them with the new one.
     * <p>It is not a particularly good idea to set the event dispatcher after
     * communications have begun. However, the observer copy is guarded against
     * multi-threaded access to the current event dispatcher. During the copy,
     * however, events will continue to be delievered via the current
     * dispatcher. Only <b>after</b> the copy is complete will the new event
     * dispatcher become the active one and events begin being delivered by
     * it.</p>
     * <p>The caller of this method can be sure that, once this method returns,
     * all new events will be handled by the new event dispatcher. However,
     * there may be events that occurred before, or during the operation of, the
     * call to this method which will be delivered by the old dispatcher. Once
     * the new event dispatcher is in place, the {@link EventDispatcher#destroy}
     * method will be called on the old dispatcher.</p>
     */
    public void setEventDispatcher(EventDispatcher eventDispatcher) {
	if (eventDispatcher == null)
	    throw new NullPointerException("Event dispatcher cannot be null");

	eventDispatcher.init();

	// Copy all current observers to the new event dispatcher..
	synchronized (this.eventDispatcher) {
	    Iterator iter = this.eventDispatcher.observerIterator();
	    while (iter.hasNext())
		eventDispatcher.addObserver((ConnectionObserver)iter.next());
	}

	EventDispatcher old = this.eventDispatcher;

	// ..and swap out the old dispatcher.
	this.eventDispatcher = eventDispatcher;

	// Clean up the old dispatcher.
	old.destroy();
    }

    /** Add a connection observer to receive SMPP events from this connection.
     * If this connection is not using asynchronous communication, this method
     * call has no effect.
     * @param ob the ConnectionObserver implementation to add.
     */
    public void addObserver(ConnectionObserver ob) {
	if (eventDispatcher != null)
	    eventDispatcher.addObserver(ob);
    }

    /** Remove a connection observer from this Connection.
     */
    public void removeObserver(ConnectionObserver ob) {
	if (eventDispatcher != null)
	    eventDispatcher.removeObserver(ob);
    }


    /** Listener thread method for asynchronous communication.
      */
    public void run()
    {
	SMPPPacket pak = null;
	int smppEx = 0, id = 0, st = 0;
	SMPPEvent exitEvent = null;
	int tooManyIOEx = 5;

	logger.info("Receiver thread started");

	APIConfig cfg = APIConfig.getInstance();
	try {
	    tooManyIOEx =
		cfg.getInt(APIConfig.TOO_MANY_IO_EXCEPTIONS);
	} catch (PropertyNotFoundException x) {
	    // just stick with the default
	    logger.debug("Didn't find I/O exception config. Using default of "
		    + tooManyIOEx);
	}

	eventDispatcher.notifyObservers(this, new ReceiverStartEvent(this));
	try {
	    while (state != UNBOUND) {
		try {
		    pak = readNextPacketInternal();
		    if (pak == null) {
			// XXX Send an event to the application??
			continue;
		    }
		} catch (SocketTimeoutException x) {
            if (state == BINDING) {
                // bind timeout has expired
                logger.error("Socket timeout in BINDING state.");
                exitEvent = new ReceiverExitEvent(this, null, state);
                ((ReceiverExitEvent)exitEvent).setReason(ReceiverExitEvent.BIND_TIMEOUT);
                setState(UNBOUND);
            } else {
                // is it okay to ignore this ??
                logger.info("Ignoring SocketTimeoutException");
            }
            
            continue;
		} catch (EOFException x) {
		    // The network connection has disappeared! Wah!
		    logger.error("EOFException received in daemon thread.", x);

		    // Will be caught by the general handler lower in this
		    // method.
		    throw x;
		} catch (IOException x) {
		    logger.warn("I/O Exception caught", x);
		    ReceiverExceptionEvent ev =
			new ReceiverExceptionEvent(this, x, state);
		    eventDispatcher.notifyObservers(this, ev);
		    smppEx++;
		    if (smppEx > tooManyIOEx) {
			logger.warn("Too many IOExceptions in receiver thread", x);
			throw x;
		    }

		    continue;
		}

		// Reset smppEx back to zero if we reach here, as packet
		// reception was successful.
		smppEx = 0;

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
			ackEnquireLink((EnquireLink)pak);
		    break;
		}

		// Tell all the observers about the new packet
		logger.info("Notifying observers of packet received");
		eventDispatcher.notifyObservers(this, pak);
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
	    eventDispatcher.notifyObservers(this, exitEvent);


	// Clean up the event dispatcher.
	eventDispatcher.destroy();
    }

    /**
     * @deprecated #ackEnquireLink
     */
    public void ackLinkQuery(EnquireLink el)
	throws java.io.IOException
    {
	ackEnquireLink(el);
    }

    /** Get a new instance of an SMPP packet. The packet will be initialised so
     * that it uses the same SMPP version as this connection and it's sequence
     * number will be initialised to using this connection's sequence numbering
     * scheme.
     * @param commandId the SMPP command ID of the packet to retrieve.
     * @return a subclass of {@link ie.omk.smpp.message.SMPPPacket}
     * corresponding to SMPP command <code>commandId</code>.
     * @throws ie.omk.smpp.BadCommandIDException if the command ID is not
     * recognised.
     * @throws ie.omk.smpp.NotSupportedException if the Connection is currently
     * using an SMPP version which does not support SMPP command
     * <code>commandId</code>.
     */
    public SMPPPacket newInstance(int commandId) throws BadCommandIDException, VersionException {

	if (!this.interfaceVersion.isSupported(commandId))
	    throw new VersionException("Command is not supported in this SMPP version");

	SMPPPacket response = PacketFactory.newInstance(commandId);
	response.setVersion(this.interfaceVersion);
	if (this.seqNumScheme != null)
	    response.setSequenceNum(this.seqNumScheme.nextNumber());

	if (defaultAlphabet != null)
	    response.setAlphabet(defaultAlphabet, 0);

	return (response);
    }
}
