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
package ie.omk.smpp.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.message.SMPPPacket;

import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.PropertyNotFoundException;
import ie.omk.smpp.util.SMPPIO;

import org.apache.log4j.Logger;

/** Abstract super class of all classes that implement a network link
  * to the SMSC. This class uses buffered input and output internally for
  * reading and writing to whatever input/output streams the concrete
  * implementation provides it. Sending and receiving are guarded against
  * multiple-thread access. That is, if more than one thread attempts to write
  * packets to the link, they will not get "mixed" in the output stream.
  * Likewise on read, only one thread will receive an incoming packet.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SmscLink
{
    /** The buffered input of the link. */
    private BufferedInputStream in = null;

    /** The buffered output of the link. */
    private BufferedOutputStream out = null;

    /** Object to use to lock reading. */
    private final Object readLock = new Object();

    /** Object to use to lock writing. */
    private final Object writeLock = new Object();


    /** Incoming bytes snoop stream. */
    private OutputStream snoopIn = null;

    /** Outgoing bytes snoop stream. */
    private OutputStream snoopOut = null;

    /** Log4J Logger object. Subclasses may use this logger too. */
    protected Logger logger = null;

    /** Set to automatically flush the output stream after every packet.
     * Default is true.
     */
    protected boolean autoFlush = true;


    /** Create a new unconnected SmscLink.
     */
    public SmscLink()
    {
	logger = Logger.getLogger("ie.omk.smpp.net.SmscLink");
	try {
	    autoFlush = APIConfig.getInstance().getBoolean(APIConfig.LINK_AUTO_FLUSH);
	} catch (PropertyNotFoundException x) {
	} finally {
	    if (logger.isDebugEnabled())
		logger.debug("autoFlush set to" + autoFlush);
	}
    }


    /** Open the connection to the SMSC. Calling this method will cause the
     * network link to the SMSC to be established. Once this method returns an
     * application may bind to the SMSC to begin it's SMPP session.
     * @throws java.io.IOException If an exception occurs while opening the
     * connection.
     */
    public final void open()
	throws java.io.IOException
    {
	implOpen();

	int inSize = -1, outSize = -1;
	try {
	    APIConfig cfg = APIConfig.getInstance();
	    inSize = cfg.getInt(cfg.LINK_BUFFERSIZE_IN);
	    outSize = cfg.getInt(cfg.LINK_BUFFERSIZE_OUT);
	} catch (PropertyNotFoundException x) {
	} finally {
	    if (logger.isDebugEnabled()) {
		logger.debug("IN buffer size: " + inSize);
		logger.debug("OUT buffer size: " + outSize);
	    }
	}

	if (inSize < 1)
	    this.in = new BufferedInputStream(getInputStream());
	else
	    this.in = new BufferedInputStream(getInputStream(), inSize);

	if (outSize < 1)
	    this.out = new BufferedOutputStream(getOutputStream());
	else
	    this.out = new BufferedOutputStream(getOutputStream(), outSize);
    }

    /** Implementation-specific link open. This method will be called by the
     * {@link #open} method. This method is responsible for establishing the
     * underlying network connection to the remote SMSC system. For example, The
     * TCP/IP implementation would create and connect a new
     * <code>java.io.Socket</code> to the SMSC host.
     * @throws java.io.IOException If an exception occurs while opening the
     * connection.
     */
    protected abstract void implOpen()
	throws java.io.IOException;


    /** Close the connection to the SMSC. Calling this method will close the
     * network link to the remote SMSC system. Applications should be unbound
     * from the SMPP link (using {@link ie.omk.smpp.Connection#unbind})
     * before closing the underlying network link. The connection may be
     * reestablished using {@link #open}.
     * @throws java.io.IOException If an exception occurs while closing the
     * connection.
     */
    public final void close()
	throws java.io.IOException
    {
	out = null;
	in = null;

	implClose();
    }

    /** Implementation-specific link close. This method is called by the
     * {@link #close} method after ensuring no further writes or reads can
     * occur. Note that any threads that are writing, reading or blocked on
     * either the readLock or writeLock at the moment this method is called will
     * still execute. Only further reads or writes will be disallowed. An
     * implementation should completely close the underlying network link to the
     * remote SMSC system but it should not free any resources that will
     * preclude the {@link #open} method from reconnecting.
     * @throws java.io.IOException if an exception occurs during close.
     * @see #getInputStream
     * @see #getOutputStream
     * @see #close
     */
    protected abstract void implClose()
	throws java.io.IOException;


    /** Send a packet to the SMSC.
      * @param pak the SMPP packet to send.
      * @throws java.io.IOException if an exception occurs during writing or if
      * the connection is not open.
      */
    public void write(SMPPPacket pak)
	throws java.io.IOException
    {
	if (out == null)
	    throw new IOException("Link not established.");

	synchronized (writeLock) {
	    if (snoopOut != null)
		pak.writeTo(snoopOut);

	    pak.writeTo(out);
	    if (autoFlush)
		out.flush();
	}
    }

    /** Flush the output stream of the SMSC link.
     * @throws java.io.IOException If an exception occurs while flushing the
     * output stream.
     */
    public void flush()
	throws java.io.IOException
    {
	if (out != null)
	    out.flush();
    }

    /** Get the auto flush behaviour of this link. The default behaviour is
     * defined in the smppapi properties file. If no properties are found at
     * runtime, the default behaviour is set to <code>true</code>.
     * @see #setAutoFlush
     * @see ie.omk.smpp.util.APIConfig
     */
    public boolean getAutoFlush() {
	return (autoFlush);
    }

    /** Set the auto flush behaviour of this link. If set to true, the link will
     * flush the output stream after every packet written. In high-load
     * environments this may be undesirable.
     * @see #getAutoFlush
     */
    public void setAutoFlush(boolean flush) {
	this.autoFlush = flush;
    }

    /** Read the next SMPP packet from the SMSC. This method will block until a
      * full packet can be read from the SMSC. The caller should pass in a byte
      * array to read the packet into.  If the passed in byte array is too
      * small, a new one will be allocated and returned to the caller.
      * @param buf a byte array buffer to read the packet into.
      * @return the handle to the passed in buffer or the reallocated one.
      * @throws java.io.EOFException If the end of stream is reached before a
      * full packet can be read.
      * @throws java.io.IOException If an exception occurs when reading the
      * packet from the input stream.
      */
    public byte[] read(byte[] buf)
	throws java.io.EOFException, java.io.IOException
    {
	int ptr = 0,
	    c = 0,
	    cmdLen = 0;

	if (in == null)
	    throw new IOException("Link not established.");

	synchronized (readLock) {
	    try {
		if ((ptr = in.read(buf, 0, 16)) < 4) {
		    if (ptr == -1)
			throw new EOFException("EOS reached. No data "
				+ "available");

		    while (ptr < 4) {
			if ((c = in.read(buf, ptr, 16 - ptr)) < 0)
			    throw new EOFException("EOS reached. No data "
				    + "available");
			ptr += c;
		    }
		}

		cmdLen = SMPPIO.bytesToInt(buf, 0, 4);
		if (cmdLen > buf.length) {
		    byte[] newbuf = new byte[cmdLen];
		    System.arraycopy(buf, 0, newbuf, 0, ptr);
		    buf = newbuf;
		}

		c = in.read(buf, ptr, cmdLen - ptr);
		if (c == -1)
		    throw new EOFException("EOS reached. No data available.");

		ptr += c;
		while (ptr < cmdLen) {
		    if ((c = in.read(buf, ptr, cmdLen - ptr)) < 0)
			throw new EOFException("EOS reached. No data available");

		    ptr += c;
		}
	    } catch (IOException x) {
		// After the finally clause, make sure the caller still gets the
		// IOException..
		throw x;
	    } finally {
		dump(snoopIn, buf, 0, ptr);
	    }
	}
	return (buf);
    }

    /** Get the number of bytes currently available on the input stream.
     */
    public final int available() {
	try {
	    synchronized (readLock) {
		return (in.available());
	    }
	} catch (IOException x) {
	    logger.debug("IOException in available", x);
	    return (0);
	}
    }

    /** Dump bytes to an output stream.
     * @param s the stream to write to (if null, do nothing).
     * @param b the byte array to dump bytes from.
     * @param offset the offset in <code>b</code> to begin from.
     * @param len the number of bytes to dump.
     */
    private void dump(OutputStream s, byte[] b, int offset, int len)
    {
	try {
	    if (s != null)
		s.write(b, offset, len);
	} catch (IOException x) {
	    logger.debug("Couldn't write incoming bytes to input snooper.", x);
	}
    }

    /** Get the output stream of the virtual circuit.
      * @throws java.io.IOException If the output stream cannot be retrieved or
      * the connection is not open.
      */
    protected abstract OutputStream getOutputStream()
	throws java.io.IOException;

    /** Get the input stream of the virtual circuit.
      * @throws java.io.IOException If the input stream cannot be retrieved or
      * the connection is not open.
      */
    protected abstract InputStream getInputStream()
	throws java.io.IOException;

    /** Check whether or not the connection to the SMSC is open.
      */
    public abstract boolean isConnected();


    /** Set the snooper streams. The snooper streams will receive every byte
     * that is either received or sent using this class. This functionality is
     * intended as a debugging aid for SMPP developers. It will be up to the
     * application using the API to provide valid output streams for the data to
     * be written to. Either or both of the streams may be set to null, which in
     * effect turns off snooping.
     * @param snoopIn stream to receive incoming bytes from the SMSC (may be
     * null).
     * @param snoopOut stream to receive outgoing bytes to the SMSC (may be
     * null).
     */
    public void setSnoopStreams(OutputStream snoopIn, OutputStream snoopOut)
    {
	this.snoopIn = snoopIn;
	this.snoopOut = snoopOut;
    }
}
