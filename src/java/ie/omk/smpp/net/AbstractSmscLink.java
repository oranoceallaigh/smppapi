package ie.omk.smpp.net;

import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.PropertyNotFoundException;
import ie.omk.smpp.util.SMPPIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base implementation of the {@link ie.omk.smpp.net.SmscLink}
 * interface.
 * This class implements basic read and write functionality that can be
 * re-used by other implementations.
 * @version $Id: $
 */
public abstract class AbstractSmscLink implements SmscLink {
    private static final String END_OF_STREAM_ERR = "EOS reached. No data available";
    private static final String LINK_NOT_UP_ERR = "Link not established.";
    private static final Logger LOG = LoggerFactory.getLogger(SmscLink.class);

    private BufferedInputStream in;
    private BufferedOutputStream out;

    private final Object readLock = new Object();
    private final Object writeLock = new Object();

    private OutputStream snoopIn;
    private OutputStream snoopOut;

    /**
     * Set to automatically flush the output stream after every packet. Default
     * is <code>false</code>, but can be reconfigured in the API config.
     */
    private boolean autoFlush;

    /**
     * Create a new unconnected SmscLink.
     */
    public AbstractSmscLink() {
        try {
            autoFlush = APIConfig.getInstance().getBoolean(
                    APIConfig.LINK_AUTO_FLUSH);
        } catch (PropertyNotFoundException x) {
            autoFlush = true;
        } finally {
            LOG.debug("autoFlush set to {}", autoFlush);
        }
    }

    /**
     * Open the connection to the SMSC. Calling this method will cause the
     * network link to the SMSC to be established. Once this method returns an
     * application may bind to the SMSC to begin it's SMPP session.
     * 
     * @throws java.io.IOException
     *             If an exception occurs while opening the connection.
     */
    public final void open() throws IOException {
        implOpen();

        APIConfig cfg = APIConfig.getInstance();
        int inSize = cfg.getInt(APIConfig.LINK_BUFFERSIZE_IN, 512);
        int outSize = cfg.getInt(APIConfig.LINK_BUFFERSIZE_OUT, 512);

        LOG.debug("IN buffer size: {}", inSize);
        LOG.debug("OUT buffer size: {}", outSize);
        in = new BufferedInputStream(getInputStream(), inSize);
        out = new BufferedOutputStream(getOutputStream(), outSize);
    }

    /**
     * Close the connection to the SMSC. Calling this method will close the
     * network link to the remote SMSC system. Applications should be unbound
     * from the SMPP link (using {@link ie.omk.smpp.Connection#unbind}) before
     * closing the underlying network link. The connection may be reestablished
     * using {@link #open}.
     * 
     * @throws java.io.IOException
     *             If an exception occurs while closing the connection.
     */
    public final void close() throws IOException {
        out = null;
        in = null;

        implClose();

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
        synchronized (writeLock) {
            try {
                if (snoopOut != null) {
                    pak.writeTo(snoopOut);
                }
            } catch (IOException x) {
                LOG.warn("IOException writing to snoop output stream.", x);
            }
            pak.writeTo(out);
            if (autoFlush) {
                out.flush();
            }
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
     * @see ie.omk.smpp.util.APIConfig
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
    public byte[] read(final byte[] array) throws IOException {
        if (in == null) {
            throw new IOException(LINK_NOT_UP_ERR);
        }

        byte[] buf = array;
        int count = 0;
        synchronized (readLock) {
            try {
                count = readBytes(buf, 0, 4, 16);
                int cmdLen = SMPPIO.bytesToInt(buf, 0);
                if (cmdLen > buf.length) {
                    byte[] newbuf = new byte[cmdLen];
                    System.arraycopy(buf, 0, newbuf, 0, count);
                    buf = newbuf;
                }
                int remaining = cmdLen - count;
                readBytes(buf, count, remaining, remaining);
            } catch (SocketTimeoutException x) {
                throw new ReadTimeoutException(x);
            } finally {
                dump(snoopIn, array, 0, count);
            }
        }
        return buf;
    }

    /**
     * Get the number of bytes currently available on the input stream.
     */
    public final int available() {
        try {
            synchronized (readLock) {
                return in.available();
            }
        } catch (IOException x) {
            LOG.debug("IOException in available", x);
            return 0;
        }
    }

    /**
     * Implementation-specific link close. This method is called by the
     * {@link #close}method after ensuring no further writes or reads can
     * occur. Note that any threads that are writing, reading or blocked on
     * either the readLock or writeLock at the moment this method is called will
     * still execute. Only further reads or writes will be disallowed. An
     * implementation should completely close the underlying network link to the
     * remote SMSC system but it should not free any resources that will
     * preclude the {@link #open}method from reconnecting.
     * 
     * @throws java.io.IOException
     *             if an exception occurs during close.
     * @see #getInputStream
     * @see #getOutputStream
     * @see #close
     */
    protected abstract void implClose() throws IOException;

    /**
     * Implementation-specific link open. This method will be called by the
     * {@link #open} method. This method is responsible for establishing the
     * underlying network connection to the remote SMSC system. For example, The
     * TCP/IP implementation would create and connect a new
     * <code>java.io.Socket</code> to the SMSC host.
     * 
     * @throws java.io.IOException
     *             If an exception occurs while opening the connection.
     */
    protected abstract void implOpen() throws IOException;

    /**
     * Get the output stream of the virtual circuit.
     * 
     * @throws java.io.IOException
     *             If the output stream cannot be retrieved or the connection is
     *             not open.
     */
    protected abstract OutputStream getOutputStream() throws IOException;

    /**
     * Get the input stream of the virtual circuit.
     * 
     * @throws java.io.IOException
     *             If the input stream cannot be retrieved or the connection is
     *             not open.
     */
    protected abstract InputStream getInputStream() throws IOException;

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
        assert Thread.holdsLock(readLock);
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
            autoClose = APIConfig.getInstance().getBoolean(
                    APIConfig.LINK_AUTOCLOSE_SNOOP);
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
