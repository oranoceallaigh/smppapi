package com.adenki.smpp.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The default sequence numbering scheme. This implementation starts at sequence
 * number 1 and increments by 1 for each number requested, resulting in the
 * sequence numbers <code>1..2..3..4..5..6..7..8..n</code>. If the sequence
 * number reaches as far as <code>Integer.MAX_VALUE</code>, it will wrap back
 * around to 1.
 * <p>
 * This implementation uses an {@link java.util.concurrent.atomic.AtomicInteger}
 * internally to track the next sequence number.
 * </p>
 * @version $Id$
 */
public class DefaultSequenceScheme implements SequenceNumberScheme {
    /**
     * Maximum this sequence can go to (a 32-bit unsigned integer).
     */
    public static final long MAX_VALUE = 4294967295L;
    
    private long start = 1L;
    private AtomicLong sequence = new AtomicLong(1);

    public DefaultSequenceScheme() {
    }

    /**
     * Construct a new DefaultSequenceScheme that starts the sequence from
     * <code>start</code>.
     */
    public DefaultSequenceScheme(long start) {
        this.start = start;
        sequence.set(start);
    }

    public long nextNumber() {
        long n = sequence.getAndIncrement();
        if (n == MAX_VALUE) {
            sequence.set(1);
        }
        return n;
    }

    public long peek() {
        return sequence.get();
    }

    public long peek(long nth) {
        return sequence.get() + nth;
    }

    public void reset() {
        sequence.set(start);
    }
}
