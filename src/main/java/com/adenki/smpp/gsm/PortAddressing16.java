package com.adenki.smpp.gsm;

import java.nio.ByteBuffer;

/**
 * 16-bit Port addressing. 3GPP TS 23.040 9.2.3.24.4.
 * @version $Id$
 */
public class PortAddressing16 extends RecurringHeaderElement {

    private int sourcePort;
    private int destPort;

    public PortAddressing16() {
        super(true);
    }

    public PortAddressing16(int sourcePort, int destPort) {
        super(true);
        setSourcePort(sourcePort);
        setDestPort(destPort);
    }
    
    public int getLength() {
        return 4;
    }

    public boolean doWrite(int segmentNum, ByteBuffer buffer) {
        buffer.put((byte) 5);
        buffer.put((byte) 4);
        buffer.putShort((short) destPort);
        buffer.putShort((short) sourcePort);
        return true;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        if (sourcePort < 0 || sourcePort > 0xffff) {
            throw new IllegalArgumentException(
                    "Source port must be 0 <= port <= 0xffff");
        }
        this.sourcePort = sourcePort;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        if (destPort < 0 || destPort > 0xffff) {
            throw new IllegalArgumentException(
                    "Destination port must be 0 <= port <= 0xffff");
        }
        this.destPort = destPort;
    }
}
