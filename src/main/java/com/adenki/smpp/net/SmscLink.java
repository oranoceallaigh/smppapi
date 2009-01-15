package com.adenki.smpp.net;

import java.io.IOException;

import com.adenki.smpp.message.SMPPPacket;

/**
 * Interface for the network link to an SMSC. 
 * @version $Id$
 */
public interface SmscLink {
    /**
     * Initiate the connection to the SMSC. If this link is already connected,
     * this method should do nothing.
     * @throws IOException
     */
    void connect() throws IOException;
    
    /**
     * Disconnect from the SMSC. If this link is already closed,
     * this method should do nothing.
     * @throws IOException
     */
    void disconnect() throws IOException;
    
    /**
     * Determine if the underlying link is connected to the SMSC.
     * @return <code>true</code> if connected, <code>false</code> otherwise.
     */
    boolean isConnected();
    
    /**
     * Send an SMPP packet to the SMSC.
     * @param packet The packet to send.
     * @param withOptionalParams <code>true</code> to send the packet&apos;s
     * optional parameters during the write, <code>false</code> to omit the
     * optional parameters.
     * @throws IOException
     */
    void write(SMPPPacket packet, boolean withOptionalParams) throws IOException;
    
    /**
     * If the underlying link implements some form of output buffering, then
     * this method should flush the buffer. If the link does not do any form
     * of buffering, this method should do nothing.
     * @throws IOException
     */
    void flush() throws IOException;
    
    /**
     * Read the next SMPP packet from the underlying link. This method should
     * block until it has fully read all of the bytes for the next packet,
     * barring any time out. The byte array supplied to the method call will
     * be used to store the packet&apos;s bytes, and that same array will be
     * returned. However, if the buffer is not large enough to hold the
     * packet, a <b>new</b> byte array will be created, the packet stored in it
     * and this new array will be returned.
     * @param buffer A byte array to use to store the packet data.
     * @return <code>buffer</code> will be returned if it is large enough to
     * hold all of the packet&apos;s data, otherwise a new array is created
     * and returned with the packet data.
     * @throws IOException
     * @throws ReadTimeoutException
     */
    SMPPPacket read() throws IOException;
    
    /**
     * Get the current timeout for the underlying link. If read timeouts
     * are not supported, calls to this method should throw a
     * {@link com.adenki.smpp.UnsupportedOperationException}.
     * @return The current timeout, specified in milliseconds.
     * @throws com.adenki.smpp.UnsupportedOperationException If read timeouts
     * are not supported.
     * @see #setTimeout(int)
     */
    int getTimeout();

    /**
     * Set the read timeout for the underlying link. If a blocked read takes
     * longer than the specified <code>timeout</code>, then a
     * {@link ReadTimeoutException} should be thrown. Supporting read timeouts
     * is optional for SmscLink implementations. If it is not supported,
     * calls to this method must throw an
     * {@link com.adenki.smpp.UnsupportedOperationException}. A timeout value
     * of <code>0</code> deactivates timeouts - reads will block forever.
     * @param timeout The new timeout value, specified in milliseconds.
     * @throws com.adenki.smpp.UnsupportedOperationException If read timeouts
     * are not supported.
     */
    void setTimeout(int timeout);
    
    /**
     * Determine if this SMSC link supports read timeouts.
     * @return <code>true</code> if the implementation supports read timeouts,
     * <code>false</code> if not.
     */
    boolean isTimeoutSupported();
}
