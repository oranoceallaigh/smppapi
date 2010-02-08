package com.adenki.smpp.gsm;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConcatenatedSms extends RecurringHeaderElement {

    private static final Random RANDOM = new SecureRandom();
    
    private int referenceNumber = 1;
    private boolean use16bitRef;
    /**
     * Store the location in each buffer where we need to go back
     * end update the correct number of segments being sent.
     */
    private Map<ByteBuffer, Integer> totalMsgs =
        new HashMap<ByteBuffer, Integer>();

    public ConcatenatedSms(boolean use16bitRef) {
        super(true);
        this.use16bitRef = use16bitRef;
        reset();
    }

    public boolean doWrite(int segmentNum, ByteBuffer buffer) {
        if (use16bitRef) {
            buffer.put((byte) 8);
            buffer.put((byte) 4);
            buffer.putShort((short) referenceNumber);
        } else {
            buffer.put((byte) 0);
            buffer.put((byte) 3);
            buffer.put((byte) referenceNumber);
        }
        totalMsgs.put(buffer, buffer.position());
        buffer.put((byte) 1);
        buffer.put((byte) segmentNum);
        return true;
    }

    public int getLength() {
        if (use16bitRef) {
            return 4;
        } else {
            return 3;
        }
    }

    @Override
    public void reset() {
        super.reset();
        int max = use16bitRef ? 65535 : 255;
        referenceNumber = RANDOM.nextInt(max);
    }
    
    @Override
    public void postProcess(List<ByteBuffer> segments) {
        int numSegments = segments.size();
        if (numSegments != totalMsgs.size()) {
            throw new IllegalStateException("Cannot update all segments");
        }
        for (Map.Entry<ByteBuffer, Integer> entry : totalMsgs.entrySet()) {
            entry.getKey().put(entry.getValue(), (byte) numSegments);
        }
    }
}
