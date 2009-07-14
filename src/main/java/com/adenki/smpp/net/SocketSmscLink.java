package com.adenki.smpp.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * @version $Id:$
 */
public class SocketSmscLink extends AbstractByteChannelSmscLink {
    private static final Logger LOG =
        LoggerFactory.getLogger(SocketSmscLink.class);

    private Socket socket;
    private InetAddress address;
    private int port;
    
    /**
     * Create a new <tt>SocketChannelSmscLink</tt> that communicates
     * over a given socket.
     * @param socket The socket to communicate with.
     * @throws IOException If <tt>socket</tt> is not currently connected.
     */
    public SocketSmscLink(Socket socket) throws IOException {
        this.socket = socket;
        if (socket.isConnected()) {
            address = socket.getInetAddress();
            port = socket.getPort();
        } else {
            throw new IOException("Socket must be connected.");
        }
    }
    
    /**
     * Create a new <tt>SocketChannelSmscLink</tt> that will communicate
     * with a server at the given Internet address and port.
     * @param address The server address to connect to.
     * @param port The port on the server to connect to.
     */
    public SocketSmscLink(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
    
    @Override
    public void connect() throws IOException {
        super.connect();
        if (socket == null) {
            socket = new Socket(address, port);
        }
        SocketChannel channel = socket.getChannel();
        setChannels(channel, channel);
    }
    
    public void disconnect() throws IOException {
        socket.close();
        socket = null;
        setChannels(null, null);
    }

    public void flush() throws IOException {
    }

    public int getTimeout() {
        try {
            return socket.getSoTimeout();
        } catch (SocketException x) {
            throw new SMPPRuntimeException("Cannot read SO_TIMEOUT", x);
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public boolean isTimeoutSupported() {
        return true;
    }

    public void setTimeout(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException x) {
            LOG.error("Failed to set timeout on socket: {} ", x.getMessage());
            if (LOG.isDebugEnabled()) {
                LOG.debug("", x);
            }
        }
    }
    
    /**
     * Get the socket that this <tt>SmscLink</tt> is connected to.
     * @return The socket that this <tt>SmscLink</tt> is connected to,
     * or <tt>null</tt> if the link is not currently connected.
     */
    public Socket getSocket() {
        return socket;
    }
}
