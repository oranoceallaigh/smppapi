package ie.omk.smpp.net;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an Smsc link using user supplied input and output streams.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class StreamLink extends ie.omk.smpp.net.SmscLink {
    private static final Logger LOG = LoggerFactory.getLogger(StreamLink.class);

    /** The input side of the link. */
    private InputStream inStream;

    /** The output side of the link. */
    private OutputStream outStream;

    /**
     * Says if this connection is open or not.
     */
    private boolean connected;

    /**
     * Create a new StreamLink object.
     * 
     * @param inStream
     *            the stream to read SMPP packets from.
     * @param outStream
     *            the stream to write SMPP packets to.
     */
    public StreamLink(InputStream inStream, OutputStream outStream) {
        if (inStream == null || outStream == null) {
            throw new NullPointerException("Neither stream can be null!");
        }

        this.inStream = inStream;
        this.outStream = outStream;
    }

    /**
     * Does nothing (the streams should already be open).
     */
    public void implOpen() {
        LOG.debug("Opening stream connection");
        connected = true;
    }

    /**
     * Does nothing. This object is not responsible for opening or closing the
     * streams.
     */
    public void implClose() {
        LOG.debug("Closing stream connection");
        connected = false;
    }

    /**
     * Get the output stream of the output socket of the virtual connection.
     */
    public OutputStream getOutputStream() {
        return this.outStream;
    }

    /**
     * Get the input stream of the input socket of the virtual connection.
     */
    public InputStream getInputStream() {
        return this.inStream;
    }

    /**
     * Check connection status.
     * 
     * @return true always.
     */
    public boolean isConnected() {
        return connected;
    }
}
