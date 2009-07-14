package com.adenki.smpp.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.message.SMPPPacket;

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
 * @version $Id$
 */
public class ObjectLink implements SmscLink {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectLink.class);
    
    private List<Object> packets = new ArrayList<Object>();
    private boolean connected;
    private AtomicInteger requestSent = new AtomicInteger(0);
    private int timeout;

    /**
     * Create a new empty ObjectLink.
     */
    public ObjectLink() {
    }
    
    public boolean isConnected() {
        return connected;
    }

    public boolean isTimeoutSupported() {
        return true;
    }

    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void write(SMPPPacket pak, boolean withOptional) throws IOException {
        if (pak.isResponse()) {
            synchronized (this) {
                requestSent.incrementAndGet();
                // Possible a thread is sleeping waiting on a packet..
                this.notify();
            }
        }
    }

    public SMPPPacket read() throws IOException {
        try {
            SMPPPacket packet = null;
            for (Iterator<Object> iter = packets.iterator(); iter.hasNext(); ) {
                Object next = iter.next();
                iter.remove();
                if (next instanceof SMPPPacket) {
                    packet = (SMPPPacket) next;
                    if (packet.isResponse()) {
                        synchronized (this) {
                            if (requestSent.get() < 1) {
                                wait((long) timeout);
                            }
                            if (requestSent.get() < 1) {
                                throw new ReadTimeoutException();
                            } else {
                                requestSent.decrementAndGet();
                            }
                        }
                    }
                    break;
                } else {
                    handleNonPacketAction(next);
                }
            }
            return packet;
        } catch (InterruptedException x) {
            throw new SMPPRuntimeException("Thread interrupted", x);
        }
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
    
    public void connect() throws IOException {
    }

    public void disconnect() throws IOException {
    }
    
    public void flush() throws IOException {
    }
    
    private void handleNonPacketAction(Object obj) throws InterruptedException {
        if (obj instanceof Long) {
            long delay = ((Long) obj).longValue();
            LOG.debug("Delaying for {}ms", delay);
            Thread.sleep(delay);
        }
    }
}
