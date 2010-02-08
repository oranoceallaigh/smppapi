package com.adenki.smpp.gsm;

import java.nio.ByteBuffer;

/**
 * 8-bit port addressing. 3GPP TS 23.040 9.2.3.24.3.
 * @version $Id$
 */
public class PortAddressing8 extends RecurringHeaderElement {

    private int sourcePort;
    private int destPort;
    
    public PortAddressing8() {
        super(true);
    }
    
    public PortAddressing8(int sourcePort, int destPort) {
        super(true);
        setSourcePort(sourcePort);
        setDestPort(destPort);
    }
    
    public int getLength() {
        return 2;
    }

    public boolean write(int segmentNum, ByteBuffer buffer) {
        buffer.put((byte) 4);
        buffer.put((byte) 2);
        buffer.put((byte) destPort);
        buffer.put((byte) sourcePort);
        return true;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        if (sourcePort < 0 || sourcePort > 0xff) {
            throw new IllegalArgumentException(
                    "Source port must be 0 <= port <= 0xff");
        }
        this.sourcePort = sourcePort;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        if (destPort < 0 || destPort > 0xff) {
            throw new IllegalArgumentException(
                    "Destination port must be 0 <= port <= 0xff");
        }
        this.destPort = destPort;
    }
}
