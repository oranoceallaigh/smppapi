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

import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPRequest;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.util.DefaultSequenceScheme;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.util.SequenceNumberScheme;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Link implementation which returns packets which have previously been added to
 * it. This implementation is useful for testing applications by first setting
 * up the link by adding packets to it which is will later return when used by a
 * <code>Connection</code> object. For example:
 * 
 * <pre>
 * ObjectLink ol = new ObjectLink();
 * 
 * // Naturally, better test code will set up the packet fields before using
 * // them.
 * ol.add(new BindReceiverResp());
 * ol.add(new DeliverSM());
 * ol.add(new DeliverSM());
 * ol.add(new DeliverSM());
 * 
 * Connection conn = new Connection(ol);
 * conn.bind(id, pass, type);
 * </pre>
 * 
 * This class will always return the packets in the order they are added. If the
 * next packet in line is a response packet, it will wait until a request has
 * been sent before reporting a packet is available to the
 * <code>Connection</code>. If it is a request packet, it will be made
 * available immediately to the <code>Connection</code>.
 * 
 * @author orank
 */
public class ObjectLink extends SmscLink {

    private List packets = new ArrayList();

    private ByteArrayInputStream in = null;

    private OLByteArrayOutputStream out = null;

    private boolean connected = false;

    private int requestSent = 0;

    private long timeout = 0;

    private SequenceNumberScheme numScheme = new DefaultSequenceScheme();

    protected void implOpen() throws IOException {
        this.out = new OLByteArrayOutputStream();
    }

    protected void implClose() throws IOException {
    }

    protected OutputStream getOutputStream() throws IOException {
        return (out);
    }

    protected InputStream getInputStream() throws IOException {
        return (in);
    }

    public boolean isConnected() {
        return (connected);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setSequenceNumberScheme(SequenceNumberScheme seqNumScheme) {
        this.numScheme = seqNumScheme;
    }

    public void write(SMPPPacket pak, boolean withOptional) throws IOException {
        super.write(pak, withOptional);
        if (pak instanceof SMPPRequest) {
            synchronized (this) {
                requestSent++;

                // Possible a thread is sleeping waiting on a packet..
                this.notify();
            }
        }
    }

    public byte[] read(byte[] buf) throws EOFException, IOException {

        Object next = (Object) packets.remove(0);
        while (!(next instanceof SMPPPacket)) {
            if (next instanceof Long) {
                long delay = ((Number) next).longValue();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException x) {
                }
            }
            next = (Object) packets.remove(0);
        }

        SMPPPacket pak = (SMPPPacket) next;
        if (pak instanceof SMPPResponse) {
            synchronized (this) {
                try {
                    if (requestSent < 1) {
                        this.wait(this.timeout);
                    }
                } catch (InterruptedException x) {
                    throw new IOException("No packets available.");
                }
            }

            // Simulate a timeout..
            if (requestSent < 1) {
                throw new SocketTimeoutException("Timed out waiting on packet");
            }
        }

        int l = pak.getLength();
        out.setBuf(buf, l);
        pak.writeTo(out);

        return (out.getBuf());
    }

    public void add(SMPPPacket pak) {
        this.packets.add(pak);
    }

    /**
     * Add a millisecond delay to the stream. The delay only begins when the
     * <code>read</code> method is called.
     * 
     * @param milliseconds
     *            Number of milliseconds to delay. Values less than 1 will be
     *            ignored.
     */
    public void addDelay(long milliseconds) {
        if (milliseconds > 0L) {
            this.packets.add(new Long(milliseconds));
        }
    }

    private class OLByteArrayOutputStream extends OutputStream {
        private byte[] buf = null;

        int pos = -1;

        public void setBuf(byte[] buf, int minCapacity) {
            if (buf.length < minCapacity) {
                this.buf = new byte[minCapacity];
                this.pos = 0;
            } else {
                this.buf = buf;
                this.pos = 0;
            }
        }

        public byte[] getBuf() {
            return (this.buf);
        }

        public void close() throws IOException {
            super.close();
            this.buf = null;
        }

        public void flush() throws IOException {
            super.flush();
        }

        public void write(byte[] src, int start, int length) throws IOException {
            System.arraycopy(src, start, buf, pos, length);
            pos += length;
        }

        public void write(byte[] src) throws IOException {
            System.arraycopy(src, 0, buf, pos, src.length);
        }

        public void write(int num) throws IOException {
            SMPPIO.intToBytes(num, 4, buf, pos);
            pos += 4;
        }
    }
}