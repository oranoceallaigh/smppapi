package com.adenki.smpp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.Session;
import com.adenki.smpp.SessionType;
import com.adenki.smpp.event.SMPPEvent;
import com.adenki.smpp.event.SessionObserver;
import com.adenki.smpp.message.Bind;
import com.adenki.smpp.message.BindReceiver;
import com.adenki.smpp.message.BindResp;
import com.adenki.smpp.message.BindTransceiver;
import com.adenki.smpp.message.BindTransmitter;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.Unbind;
import com.adenki.smpp.message.UnbindResp;
import com.adenki.smpp.net.ReadTimeoutException;

/**
 * Connection observer which mimics synchronous communications. This observer
 * provides methods which block the caller until the desired response packet
 * is available to be returned.
 * @version $Id$
 */
public class SyncWrapper implements SessionObserver {
    private static final Logger LOG = LoggerFactory.getLogger(SyncWrapper.class);
    
    private Session connection;
    private Map<Number, SMPPPacket> blockers = new HashMap<Number, SMPPPacket>();
    private List<SMPPPacket> packetQueue = new ArrayList<SMPPPacket>();
    private long packetTimeout;
    
    private ConnectionCaller bindCaller = new ConnectionCaller() {
        public void execute(Session connection, SMPPPacket packet) throws IOException {
            connection.bind((Bind) packet);
        }
    };
    private ConnectionCaller packetCaller = new ConnectionCaller() {
        public void execute(Session connection, SMPPPacket packet) throws IOException {
            connection.sendPacket(packet);
        }
    };

    public SyncWrapper(Session connection) {
        this.connection = connection;
    }

    /**
     * Bind to the SMSC.
     * @param type The type of connection to bind as (transmitter, receiver
     * or transceiver).
     * @param systemID The system ID of this connection.
     * @param password The password of this connection.
     * @param systemType The system Type for this connection.
     * @return The bind response packet received from the SMSC.
     * @throws IOException If an error occurs when trying to send the
     * bind request packet to the SMSC.
     * @throws ReadTimeoutException If the bind timeout
     * expires before the response is received from the SMSC.
     */
    public BindResp bind(SessionType type,
            String systemID,
            String password,
            String systemType) throws IOException {
        return bind(type, systemID, password, systemType, 0, 0, null);
    }

    /**
     * Bind to the SMSC.
     * @param type The type of connection to bind as (transmitter, receiver
     * or transceiver).
     * @param systemID The system ID of this connection.
     * @param password The password of this connection.
     * @param systemType The system Type for this connection.
     * @param typeOfNumber TON to bind as.
     * @param numberPlanIndicator NPI to bind as.
     * @param addressRange Address range to bind as.
     * @return The bind response packet received from the SMSC.
     * @throws IOException If an error occurs when trying to send the
     * bind request packet to the SMSC.
     * @throws ReadTimeoutException If the bind timeout
     * expires before the response is received from the SMSC.
     */
    public BindResp bind(SessionType type,
            String systemID,
            String password,
            String systemType,
            int typeOfNumber,
            int numberPlanIndicator,
            String addressRange) throws IOException {
        Bind bindRequest;
        if (type == SessionType.TRANSMITTER) {
            bindRequest = new BindTransmitter();
        } else if (type == SessionType.RECEIVER) {
            bindRequest = new BindReceiver();
        } else {
            bindRequest = new BindTransceiver();
        }
        bindRequest.setVersion(connection.getVersion());
        bindRequest.setSystemId(systemID);
        bindRequest.setPassword(password);
        bindRequest.setSystemType(systemType);
        bindRequest.setAddressTon(typeOfNumber);
        bindRequest.setAddressNpi(numberPlanIndicator);
        bindRequest.setAddressRange(addressRange);
        return bind(bindRequest);
    }

    /**
     * Bind to the SMSC.
     * @param bindRequest The bind request packet to send.
     * @return The bind response packet received from the SMSC.
     * @throws IOException If an error occurs when trying to send the bind
     * packet to the SMSC.
     * @throws ReadTimeoutException If the bind timeout
     * expires before the response is received from the SMSC.
     */
    public BindResp bind(Bind bindRequest) throws IOException {
        long timeout = getBindTimeout();
        BindResp bindResp = (BindResp) sendAndWait(
                bindRequest, bindCaller, timeout);
        if (bindResp == null) {
            throw new ReadTimeoutException();
        } else {
            return bindResp;
        }
    }

    /**
     * Unbind from the SMSC and wait for the unbind response packet.
     * @return The unbind response packet received from the SMSC.
     * @throws IOException If an error occurs when trying to send the unbind
     * packet to the SMSC.
     * @throws ReadTimeoutException If the <code>packetTimeout</code>
     * expires before the response is received from the SMSC.
     */
    public UnbindResp unbind() throws IOException {
        UnbindResp unbindResp = (UnbindResp) sendAndWait(
                new Unbind(), packetCaller, packetTimeout);
        if (unbindResp == null) {
            throw new ReadTimeoutException();
        } else {
            return unbindResp;
        }
    }
    
    /**
     * Send a packet to the SMSC and wait for its response.
     * @param packet The request packet to send to the SMSC.
     * @return The response packet received from the SMSC. This may be
     * <code>null</code> if the call timed out waiting on the packet, or if
     * the thread was instructed to give up the wait.
     * @throws IOException If there was a problem writing to, or reading
     * from, the connection.
     * @throws ReadTimeoutException If the <code>packetTimeout</code>
     * expires before the response is received from the SMSC.
     */
    public SMPPPacket sendPacket(SMPPPacket packet) throws IOException {
        SMPPPacket response = sendAndWait(packet, packetCaller, packetTimeout);
        if (response == null) {
            throw new ReadTimeoutException();
        } else {
            return response;
        }
    }
    
    public void packetReceived(Session source, SMPPPacket packet) {
        if (packet.isResponse()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response received: there are {} threads blocked.",
                        blockers.size());
            }
            Number seq = null;
            synchronized (blockers) {
                seq = new Long(packet.getSequenceNum());
                seq = getKeyObject(seq);
                if (seq != null) {
                    blockers.put(seq, packet);
                    synchronized (seq) {
                        seq.notify();
                    }
                } else {
                    LOG.debug("No blocker thread waiting on packet {}", seq);
                    addToQueue(packet);
                }
            }
        } else {
            addToQueue(packet);
        }
    }

    public void update(Session source, SMPPEvent event) {
        // TODO: need to handle SMPPEvent
        LOG.warn("SyncWrapper ignoring an SMPP event.");
    }

    /**
     * Check if there are any packets available to be read.
     * @return <code>true</code> if {@link #readNextPacket} will return a
     * packet without blocking, <code>false</code> if it would block waiting
     * on a packet.
     */
    public boolean isPacketAvailable() {
        return packetQueue.size() > 0;
    }

    /**
     * Read the next packet from the connection.
     * @param block <code>true</code> to block waiting on the next packet
     * to arrive, <code>false</code> to return whether or not a packet was
     * available. Note that calling this method will not return a response
     * packet to the calling thread if there is already another thread blocked
     * waiting on that response. That is, if thread 1 called <code>sendPacket
     * </code> and is waiting on a response and then thread 2 calls this method,
     * when the response packet arrives, thread 1 will be given the response
     * and thread 2 will continue to block in this method until the <b>next</b>
     * packet arrives. However, if thread 1 stops waiting for some reason
     * (for example, it times out waiting for its response), then thread 2
     * will receive the response packet.
     * @return The next packet from the SMSC, or <code>null</code> if no
     * packet was available and the caller requested non-blocking operation.
     * @throws IOException If there was a problem communicating with the 
     * connection.
     */
    public SMPPPacket readNextPacket(boolean block) throws IOException {
        SMPPPacket packet = null;
        try {
            synchronized (packetQueue) {
                if (packetQueue.size() < 1 && block) {
                    packetQueue.wait();
                } else {
                    packet = packetQueue.remove(0);
                }
            }
        } catch (InterruptedException x) {
            LOG.info("Thread interrupted while blocked waiting on a packet.");
        }
        return packet;
    }
    
    /**
     * Get the current packet timeout setting.
     * @return The packet timeout setting.
     * @see #setPacketTimeout(long)
     */
    public long getPacketTimeout() {
        return packetTimeout;
    }

    /**
     * Set the timeout, in milliseconds, to block waiting for a packet. The
     * default is <code>0</code>, meaning wait forever for the packet.
     * @param packetTimeout
     */
    public void setPacketTimeout(long packetTimeout) {
        this.packetTimeout = packetTimeout;
    }

    /**
     * Notify any threads that are currently blocked waiting on a response
     * packet to give up waiting on the response and return. 
     */
    public void interruptAllBlocked() {
        synchronized (blockers) {
            for (Iterator<Number> iter = blockers.keySet().iterator(); iter.hasNext();) {
                Number seq = iter.next();
                synchronized (seq) {
                    seq.notify();
                }
            }
        }
    }
    
    private Number getKeyObject(Number seq) {
        for (Iterator<Number> iter = blockers.keySet().iterator(); iter.hasNext();) {
            Number value = iter.next();
            if (value.equals(seq)) {
                return value;
            }
        }
        return null;
    }
    
    private void addToQueue(SMPPPacket packet) {
        synchronized (packetQueue) {
            packetQueue.add(packet);
            packetQueue.notify();
        }
    }
    
    private long getBindTimeout() {
        APIConfig config = APIConfigFactory.getConfig();
        return config.getLong(APIConfig.BIND_TIMEOUT, 0L);
    }
    
    private SMPPPacket sendAndWait(SMPPPacket packet,
            ConnectionCaller caller,
            long timeout) throws IOException {
        Long seq;
        if (packet.getSequenceNum() < 0L) {
            long nextSeq = connection.getSequenceNumberScheme().nextNumber();
            seq = new Long(nextSeq);
        } else {
            seq = new Long(packet.getSequenceNum());
        }
        synchronized (blockers) {
            if (blockers.containsKey(seq)) {
                throw new com.adenki.smpp.IllegalStateException(
                        "Got a duplicate sequence number!");
            }
            blockers.put(seq, null);
        }
        packet.setSequenceNum(seq.longValue());
        SMPPPacket response = null;
        try {
            synchronized (seq) {
                caller.execute(connection, packet);
                seq.wait(timeout);
            }
        } catch (InterruptedException x) {
            LOG.debug("Thread interrupted while waiting on response packet {}.",
                    seq);
        } finally {
            // Done in finally because the sequence number should be removed
            // from the map whether or not a response was received.
            synchronized (blockers) {
                response = blockers.remove(seq);
            }
        }
        return response;
    }
    
    private interface ConnectionCaller {
        void execute(Session connection, SMPPPacket packet) throws IOException;
    }
}
