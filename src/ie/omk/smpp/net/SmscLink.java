/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
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
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** Abstract super class of all classes that implement a network link
  * to the SMSC. This class uses buffered input and output internally for
  * reading and writing to whatever input/output streams the concrete
  * implementation provides it.
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


    /** Open the connection to the SMSC.
      * @exception java.io.IOException If a communication error occurs
      */
    public abstract void open()
	throws java.io.IOException;

    /** Close the connection to the SMSC.
      * @exception java.io.IOException If a communication error occurs
      */
    public abstract void close()
	throws java.io.IOException;


    /** Send a packet to the SMSC.
      * @param pak the SMPP packet to send.
      * @exception java.io.IOException if an I/O error occurs.
      */
    public final void write(SMPPPacket pak)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	synchronized (writeLock) {
	    if (out == null)
		out = new BufferedOutputStream(getOutputStream());

	    Debug.send(pak);
	    pak.writeTo(out);
	    out.flush(); // XXX does it make sense to flush every packet?
	}
    }

    /** Flush the output stream of the SMSC link.
      */
    public final void outFlush()
	throws java.io.IOException
    {
	if (out != null)
	    out.flush();
    }

    /** Read the next SMPP packet from the SMSC. This method will block until a
      * full packet can be read from the SMSC. The caller should pass in a byte
      * array to read the packet into and use the
      * {@link ie.omk.smpp.util.PacketFactory} class to create an instance of
      * the appropriate SMPP packet class. If the passed in byte array is too
      * small, a new one will be allocated and returned to the caller.
      * @param buf a byte array buffer to read the packet into.
      * @return the handle to the passed in buffer or the reallocated one.
      * @exception java.io.IOException if an I/O error occurs.
      * @exception ie.omk.smpp.SMPPException if an SMPP packet cannot be
      * extracted from the incoming data.
      */
    public final byte[] read(byte[] buf)
	throws java.io.IOException
    {
	int ptr = 0,
	    c = 0,
	    cmdLen = 0;

	synchronized (readLock) {
	    if (in == null)
		in = new BufferedInputStream(getInputStream());

	    if ((ptr = in.read(buf, 0, 16)) < 4) {
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
	}
	return (buf);
    }

    /** Get the output stream of the virtual circuit.
      * @exception java.io.IOException If a communication error occurs
      */
    protected abstract OutputStream getOutputStream()
	throws java.io.IOException;

    /** Get the input stream of the virtual circuit.
      * @exception java.io.IOException If a communication error occurs
      */
    protected abstract InputStream getInputStream()
	throws java.io.IOException;

    /** Check whether or not the connection to the SMSC is open.
      */
    public abstract boolean isConnected();
}
