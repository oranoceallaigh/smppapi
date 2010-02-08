package com.adenki.smpp.gsm;

import java.nio.ByteBuffer;
import java.util.List;


/**
 * Abstract base class for {@link HeaderElement} implementations.
 * @version $Id$
 */
public abstract class AbstractHeaderElement implements HeaderElement {
    /**
     * Complete will be true after the element has been successfully written,
     * and false if not yet written to a segment. This only makes sense
     * for non-recurring elements.
     */
    private boolean complete;
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass().equals(obj.getClass());
    }

    public void reset() {
    }
    
    public boolean isComplete() {
        return complete;
    }

    public boolean isRecurring() {
        return false;
    }
    
    public boolean write(int segmentNum, ByteBuffer buffer) {
        if (buffer.remaining() < getLength() + 2) {
            return false;
        } else {
            complete = doWrite(segmentNum, buffer);
            return complete;
        }
    }

    public void postProcess(List<ByteBuffer> segments) {
    }
    
    protected boolean doWrite(int segmentNum, ByteBuffer buffer) {
        return true;
    }
}
