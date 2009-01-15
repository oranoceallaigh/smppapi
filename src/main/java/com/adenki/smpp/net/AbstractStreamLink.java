package com.adenki.smpp.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.PacketEncoderImpl;
import com.adenki.smpp.util.PacketFactory;
import com.adenki.smpp.util.PropertyNotFoundException;
import com.adenki.smpp.util.SMPPIO;

/**
 * Abstract base implementation of the {@link com.adenki.smpp.net.SmscLink}
 * interface that operates on java.io streams.
 * <p>
 * This class implements basic read and write functionality that can be
 * re-used by other implementations.
 * </p>
 * <p>
 * Concrete implementations of this class <b>must</b> call both
 * {@link #setInputStream(InputStream)} and
 * {@link #setOutputStream(OutputStream)} during the
 * {@link SmscLink#connect()} operation in order to set up instances of
 * this class properly.
 * </p>
 * @version $Id$
 */
public abstract class AbstractStreamLink implements SmscLink {
    private static final String END_OF_STREAM_ERR = "EOS reached. No data available";
    private static final String LINK_NOT_UP_ERR = "Link not established.";
    private static final Logger LOG = LoggerFactory.getLogger(SmscLink.class);

    private byte[] buffer;
    private InputStream in;
    private OutputStream out;
    private OutputStream snoopIn;
    private OutputStream snoopOut;
    private PacketEncoder encoder;
    private PacketDecoder decoder;
    private PacketEncoder snoopOutEncoder;
    private PacketFactory packetFactory;

    /**
     * Set to automatically flush the output stream after every packet. Default
     * is <code>false</code>, but can be reconfigured in the API config.
     */
    private boolean autoFlush;

    /**
     * Create a new unconnected SmscLink.
     */
    public AbstractStreamLink() {
        try {
            APIConfig config = APIConfigFactory.getConfig();
            autoFlush = config.getBoolean(APIConfig.LINK_AUTO_FLUSH);
        } catch (PropertyNotFoundException x) {
            autoFlush = true;
        } finally {
            LOG.debug("autoFlush set to {}", autoFlush);
        }
    }

    /**
     * Close the connection to the SMSC. Calling this method will close the
     * network link to the remote SMSC system. Applications should be unbound
     * from the SMPP link (using {@link com.adenki.smpp.Session#unbind}) before
     * closing the underlying network link. The connection may be reestablished
     * using {@link #open}.
     * 
     * @throws java.io.IOException
     *             If an exception occurs while closing the connection.
     */
    public void disconnect() throws IOException {
        out = null;
        in = null;
        buffer = null;
        encoder = null;
        decoder = null;
        if (isAutoCloseSnoop()) {
            closeQuietly(snoopOut);
            closeQuietly(snoopIn);
        } else {
            flushQuietly(snoopOut);
            flushQuietly(snoopIn);
        }
    }

    /**
     * Send a packet to the SMSC.
     * 
     * @param pak
     *            the SMPP packet to send.
     * @param withOptional
     *            true to send the optional parameters over the link too, false
     *            to only send the mandatory parameters.
     * @throws java.io.IOException
     *             if an exception occurs during writing or if the connection is
     *             not open.
     */
    public void write(SMPPPacket pak, boolean withOptional) throws IOException {
        if (out == null) {
            throw new IOException(LINK_NOT_UP_ERR);
        }
        pak.writeTo(encoder, withOptional);
        try {
            if (snoopOutEncoder != null) {
                pak.writeTo(snoopOutEncoder, withOptional);
            }
        } catch (IOException x) {
            LOG.warn("IOException writing to snoop output stream.", x);
        }
        if (autoFlush) {
            out.flush();
        }
    }

    /**
     * Flush the output stream of the SMSC link.
     * 
     * @throws java.io.IOException
     *             If an exception occurs while flushing the output stream.
     */
    public void flush() throws IOException {
        if (out != null) {
            out.flush();
        }
    }

    /**
     * Get the auto flush behaviour of this link. The default behaviour is
     * defined in the smppapi properties file. If no properties are found at
     * runtime, the default behaviour is set to <code>true</code>.
     * 
     * @see #setAutoFlush
     * @see com.adenki.smpp.util.APIConfig
     */
    public boolean getAutoFlush() {
        return autoFlush;
    }

    /**
     * Set the auto flush behaviour of this link. If set to true, the link will
     * flush the output stream after every packet written. In high-load
     * environments this may be undesirable.
     * 
     * @see #getAutoFlush
     */
    public void setAutoFlush(boolean flush) {
        this.autoFlush = flush;
    }

    /**
     * Read the next SMPP packet from the SMSC. This method will block until a
     * full packet can be read from the SMSC. The caller should pass in a byte
     * array to read the packet into. If the passed in byte array is too small,
     * a new one will be allocated and returned to the caller.
     * 
     * @param array
     *            a byte array buffer to read the packet into.
     * @return the handle to the passed in buffer or the reallocated one.
     * @throws java.io.EOFException
     *             If the end of stream is reached before a full packet can be
     *             read.
     * @throws java.io.IOException
     *             If an exception occurs when reading the packet from the input
     *             stream.
     */
    public SMPPPacket read() throws IOException {
        if (in == null) {
            throw new IOException(LINK_NOT_UP_ERR);
        }
        int count = 0;
        try {
            count = readBytes(buffer, 0, 4, 16);
            int cmdLen = SMPPIO.readInt4(buffer, 0);
            if (cmdLen > buffer.length) {
                byte[] newbuf = new byte[cmdLen];
                System.arraycopy(buffer, 0, newbuf, 0, count);
                buffer = newbuf;
                decoder = new PacketDecoderImpl(buffer);
            }
            int remaining = cmdLen - count;
            readBytes(buffer, count, remaining, remaining);
            int commandId = SMPPIO.readInt4(buffer, 4);
            SMPPPacket packet = packetFactory.newInstance(commandId);
            decoder.setParsePosition(0);
            packet.readFrom(decoder);
            return packet;
        } catch (SocketTimeoutException x) {
            throw new ReadTimeoutException(x);
        } finally {
            dump(snoopIn, buffer, 0, count);
        }
    }

    /**
     * Get the number of bytes currently available on the input stream.
     */
    public final int available() {
        try {
            return (in != null) ? in.available() : 0;
        } catch (IOException x) {
            LOG.debug("IOException in available", x);
            return 0;
        }
    }

    /**
     * Set the snooper streams. The snooper streams will receive every byte that
     * is either received or sent using this class. This functionality is
     * intended as a debugging aid for SMPP developers. It will be up to the
     * application using the API to provide valid output streams for the data to
     * be written to. Either or both of the streams may be set to null, which in
     * effect turns off snooping.
     * 
     * @param snoopIn
     *            stream to receive incoming bytes from the SMSC (may be null).
     * @param snoopOut
     *            stream to receive outgoing bytes to the SMSC (may be null).
     */
    public void setSnoopStreams(OutputStream snoopIn, OutputStream snoopOut) {
        this.snoopIn = snoopIn;
        this.snoopOut = snoopOut;
        snoopOutEncoder = new PacketEncoderImpl(snoopOut);
    }

    protected void setInputStream(InputStream inputStream) {
        this.in = inputStream;
        buffer = new byte[512];
        this.decoder = new PacketDecoderImpl(buffer);
    }
    
    protected void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
        this.encoder = new PacketEncoderImpl(this.out);
    }
    
    /**
     * Attempt to read the bytes for an SMPP packet from the inbound stream.
     * @param buf The buffer to read bytes in to.
     * @param offset The offset into buffer to begin writing bytes from.
     * @param maxLen The maximum number of bytes to read in.
     * @param minimum The minimum number of bytes to read before returning. Once
     * this method has read at least this number of bytes, it will return.
     * @return The number of bytes read by this method.
     * @throws IOException
     */
    private int readBytes(byte[] buf, int offset, int minimum, int maxLen) throws IOException {
        int ptr = in.read(buf, offset, maxLen);
        if (ptr < minimum) {
            if (ptr == -1) {
                throw new EOFException(END_OF_STREAM_ERR);
            }
            while (ptr < minimum) {
                int count = in.read(buf, offset + ptr, maxLen - ptr);
                if (count < 0) {
                    throw new EOFException(END_OF_STREAM_ERR);
                }
                ptr += count;
           }
        }
        return ptr;
    }
    
    /**
     * Dump bytes to an output stream.
     * 
     * @param s
     *            the stream to write to (if null, do nothing).
     * @param b
     *            the byte array to dump bytes from.
     * @param offset
     *            the offset in <code>b</code> to begin from.
     * @param len
     *            the number of bytes to dump.
     */
    private void dump(OutputStream s, byte[] b, int offset, int len) {
        try {
            if (s != null) {
                s.write(b, offset, len);
            }
        } catch (IOException x) {
            LOG.warn("Couldn't write incoming bytes to input snooper.", x);
        }
    }

    /**
     * Check the configuration to see if the snoop streams should be
     * automatically closed when this link is closed.
     * @return <code>true</code> to close the snoop streams when
     * {@link #close} is called, <code>false</code> otherwise.
     */
    private boolean isAutoCloseSnoop() {
        boolean autoClose = true;
        try {
            APIConfig config = APIConfigFactory.getConfig();
            autoClose = config.getBoolean(APIConfig.LINK_AUTOCLOSE_SNOOP);
        } catch (PropertyNotFoundException x) {
            LOG.debug("{} property not found. Using the default of {}",
                    APIConfig.LINK_AUTOCLOSE_SNOOP, autoClose);
        }

        return autoClose;
    }
    
    private void closeQuietly(OutputStream stream) {
        try {
            stream.close();
        } catch (IOException x) {
            LOG.debug("Exception closing a stream quietly", x);
        }
    }
    
    private void flushQuietly(OutputStream stream) {
        try {
            stream.flush();
        } catch (IOException x) {
            LOG.debug("Exception flushing a stream quietly", x);
        }
    }
}
