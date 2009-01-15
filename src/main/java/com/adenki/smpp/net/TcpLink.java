package com.adenki.smpp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an Smsc link over the tcp/ip protocol
 * 
 * @version $Id$
 */
public class TcpLink extends AbstractStreamLink {
    private static final String STACK_TRACE_ERR = "Stack trace:";

    private static final String SOCKET_NOT_OPEN_ERR = "Socket connection is not open";

    private static final Logger LOG = LoggerFactory.getLogger(TcpLink.class);

    /**
     * Default IP port to use if none are specified.
     */
    public static final int DEFAULT_PORT = 5016;

    /**
     * The internet address of the SMSC.
     */
    private InetAddress addr;

    /**
     * The port to connect to.
     */
    private int port;

    /**
     * The socket timeout setting.
     */
    private int sockTimeout;
    
    private Socket sock;

    /**
     * Create a new TcpLink
     * 
     * @param address
     *            IP address or hostname of SMSC
     * @throws java.net.UnknownHostException
     *             If the host is not found.
     */
    public TcpLink(String address) throws java.net.UnknownHostException {
        this(address, DEFAULT_PORT);
    }

    /**
     * Create a new TcpLink
     * 
     * @param address
     *            IP address or hostname of SMSC
     * @param port
     *            The port number to connect to
     * @throws java.net.UnknownHostException
     *             If the host is not found.
     */
    public TcpLink(String address, int port) throws java.net.UnknownHostException {
        this.addr = InetAddress.getByName(address);
        if (port < 1) {
            this.port = DEFAULT_PORT;
        } else {
            this.port = port;
        }
    }

    /**
     * Create a new TcpLink
     * 
     * @param address
     *            IP address SMSC
     * @throws java.net.UnknownHostException
     *             If the host is not found.
     */
    public TcpLink(InetAddress address) {
        this(address, DEFAULT_PORT);
    }

    /**
     * Create a new TcpLink
     * 
     * @param address
     *            IP address of SMSC
     * @param port
     *            The port number to connect to
     * @throws java.net.UnknownHostException
     *             If the host is not found.
     */
    public TcpLink(InetAddress address, int port) {
        this.addr = address;
        if (port < 1) {
            this.port = DEFAULT_PORT;
        } else {
            this.port = port;
        }
    }

    /**
     * Create a new TcpLink object around an existing socket.
     * @param socket The socket to use for communications.
     */
    public TcpLink(Socket socket) throws IOException {
        this.sock = socket;
        setInputStream(sock.getInputStream());
        setOutputStream(sock.getOutputStream());
    }
    
    /**
     * Get the address we're connected (or connecting) to.
     * 
     * @return The address of the SMSC this link is connected to.
     */
    public InetAddress getAddress() {
        return addr;
    }

    /**
     * Get the service port to connect to at the SMSC to establish a TCP
     * connection.
     * 
     * @return The service port at the SMSC to connect to.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the port at the SMSC that this link is connected to. This is the
     * remote port that this link is connected to after a successful connection
     * has been made.
     * 
     * @return The remote port this link is connected to.
     * @throws java.io.IOException
     *             If the connection is not open.
     */
    public int getConnectedPort() throws java.io.IOException {
        if (sock == null) {
            throw new IOException(SOCKET_NOT_OPEN_ERR);
        } else {
            return sock.getPort();
        }
    }

    /**
     * Get the local port number this link is connected to.
     * 
     * @return The local port number this link is connected to.
     * @throws java.io.IOException
     *             If the connection is not open.
     */
    public int getLocalPort() throws java.io.IOException {
        if (sock == null) {
            throw new IOException(SOCKET_NOT_OPEN_ERR);
        } else {
            return sock.getLocalPort();
        }
    }

    public boolean isConnected() {
        return sock != null && sock.isConnected();
    }

    /**
     * Set the socket timeout. This SmscLink implementation uses SO_TIMEOUT
     * to implement read timeouts.
     * @param timeout The timeout to set.
     * @see SmscLink#setTimeout(int)
     */
    public void setTimeout(int timeout) {
        try {
            sockTimeout = timeout;
            if (sock != null) {
                sock.setSoTimeout(sockTimeout);
            }
        } catch (SocketException x) {
            LOG.error("Failed to set timeout on socket: {} ", x.getMessage());
            if (LOG.isDebugEnabled()) {
                LOG.debug(STACK_TRACE_ERR, x);
            }
        }
    }

    public int getTimeout() {
        try {
            return sock.getSoTimeout();
        } catch (SocketException x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(STACK_TRACE_ERR, x);
            }
        }
        return -1;
    }

    public boolean isTimeoutSupported() {
        return true;
    }
    
    public void connect() throws java.io.IOException {
        if (sock != null) {
            LOG.debug("Cannot connect a link wrapped around a socket.");
            throw new IllegalStateException();
        }
        LOG.info("Opening TCP socket to {}:{}", addr, port);
        sock = new Socket();
        SocketAddress sockAddr = new InetSocketAddress(addr, port);
        sock.connect(sockAddr, sockTimeout);
        if (sockTimeout > 0) {
            sock.setSoTimeout(sockTimeout);
        }
    }

    public void disconnect() throws java.io.IOException {
        if (isConnected()) {
            LOG.info("Shutting down socket connection");
            sock.close();
            sock = null;
        }
    }

    /**
     * Get the output stream of the Socket connection to the SMSC.
     * 
     * @throws java.io.IOException
     *             If the socket connection is not open or an I/O error occurs
     *             when creating the output stream.
     * @see java.io.OutputStream
     * @see java.net.Socket#getOutputStream
     */
    protected OutputStream getOutputStream() throws java.io.IOException {
        if (sock == null) {
            throw new IOException(SOCKET_NOT_OPEN_ERR);
        } else {
            return sock.getOutputStream();
        }
    }

    /**
     * Get the input stream of the Socket connection to the SMSC.
     * 
     * @throws java.io.IOException
     *             If the socket connection is not open or an I/O error occurs
     *             when creating the input stream.
     * @see java.io.InputStream
     * @see java.net.Socket#getInputStream
     */
    protected InputStream getInputStream() throws java.io.IOException {
        if (sock == null) {
            throw new IOException(SOCKET_NOT_OPEN_ERR);
        } else {
            return sock.getInputStream();
        }
    }
}
