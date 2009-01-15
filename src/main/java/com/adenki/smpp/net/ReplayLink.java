package com.adenki.smpp.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketFactory;
import com.adenki.smpp.util.SMPPIO;

/**
 * An implementation of the SmscLink interface which can be used to replay
 * an SMPP session.
 * <p>
 * This implementation is intended to be used in conjunction with data
 * captured from a real SMPP session. All outbound bytes should be captured
 * in one location and all inbound bytes in another. This link will
 * reconstitute the SMPP packets for the session and provide response
 * packets only after it has seen the request packets being sent.
 * </p>
 * @version $Id$
 *
 */
public class ReplayLink implements SmscLink {

    private InputStream inPacketSource;
    private InputStream outPacketSource;
    private int timeout = 0;
    private boolean connected;
    private PacketFactory packetFactory = new PacketFactory();
    private Set<Long> outboundSeqNums = new HashSet<Long>();
    private List<SMPPPacket> packetLookahead = new ArrayList<SMPPPacket>();
    private byte[] header = new byte[16];
    private byte[] packet = new byte[512];
    private TestDecoder decoder = new TestDecoder();
    
    public ReplayLink(InputStream inPacketSource, InputStream outPacketSource) {
        this.inPacketSource = inPacketSource;
        this.outPacketSource = outPacketSource;
    }
    
    public void connect() throws IOException {
        connected = true;
        lookahead(10);
    }

    public void disconnect() throws IOException {
        connected = false;
        packetLookahead.clear();
    }

    public void flush() throws IOException {
    }

    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isTimeoutSupported() {
        return false;
    }

    public SMPPPacket getNextOutbound() throws IOException {
        synchronized (decoder) {
            return readOnePacket(outPacketSource);
        }
    }
    
    public SMPPPacket read() throws IOException {
        if (!connected) {
            throw new IllegalStateException("Not connected.");
        }
        if (packetLookahead.size() < 3) {
            lookahead(50);
        }
        if (packetLookahead.size() == 0) {
            throw new EOFException();
        }
        SMPPPacket entry = packetLookahead.get(0);
        Long sequence = new Long(entry.getSequenceNum());
        if (entry.isResponse() && !outboundSeqNums.contains(sequence)) {
            blockUntilRequestSent(sequence);
        }
        return packetLookahead.remove(0);
    }

    public void write(SMPPPacket packet, boolean withOptionalParams)
            throws IOException {
        if (!connected) {
            throw new IllegalStateException("Not connected.");
        }
        Long seq = new Long(packet.getSequenceNum());
        synchronized (this) {
            outboundSeqNums.add(seq);
            notifyAll();
        }
    }

    private void lookahead(int number) throws IOException {
        synchronized (decoder) {
            for (int i = 0; i < number; i++) {
                SMPPPacket packet = readOnePacket(inPacketSource);
                if (packet == null) {
                    break;
                }
                packetLookahead.add(packet);
            }
        }
    }

    private void blockUntilRequestSent(Long sequence) {
        try {
            synchronized (this) {
                if (!outboundSeqNums.contains(sequence)) {
                    wait(timeout);
                }
                if (!outboundSeqNums.contains(sequence)) {
                    throw new ReadTimeoutException();
                }
            }
        } catch (InterruptedException x) {
        }
    }
    
    private SMPPPacket readOnePacket(InputStream source) throws IOException {
        int count = 0;
        while (count < 16) {
            int n = source.read(header, 0, 16 - count);
            if (n < 0) {
                return null;
            }
            count += n;
        }
        int length = SMPPIO.readInt4(header, 0);
        if (length > packet.length) {
            packet = new byte[length];
        }
        while (count < length) {
            int n = source.read(packet, count - 16, length - count);
            if (n < 0) {
                return null;
            }
            count += n;
        }
        int id = SMPPIO.readInt4(header, 4);
        SMPPPacket packet = packetFactory.newInstance(id);
        decoder.reset(count);
        packet.readFrom(decoder);
        return packet;
    }
    
    private class TestDecoder extends PacketDecoderImpl {
        int offset;
        int available;
        
        public void reset(int byteCount) {
            setBytes(header);
            setParsePosition(0);
            offset = 0;
            available = byteCount;
        }
        
        @Override
        public int getAvailableBytes() {
            return available;
        }
        
        @Override
        public long readUInt4() {
            long val = super.readUInt4();
            if (getParsePosition() == 16) {
                setBytes(packet);
                setParsePosition(0);
                offset = 16;
            }
            return val;
        }
        
        @Override
        public int getParsePosition() {
            return offset + super.getParsePosition();
        }
    }
}
