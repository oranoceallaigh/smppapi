package com.adenki.smpp.util;

/**
 * SMPP packet sequence numbering scheme interface. Implementations of this
 * interface provide a {@link com.adenki.smpp.Session} with a unique number for
 * each call to <code>nextNumber</code>. This number is used as the packet's
 * sequence number in the SMPP header. The default implementation (
 * {@link DefaultSequenceScheme}) counts monotonically from 1 upwards for each
 * number requested. While this is the SMPP specification's recommended
 * behaviour, there is no requirement for 2 sequentially-requested numbers to be
 * numerically sequential.
 * 
 * @version $Id$
 */
public interface SequenceNumberScheme {

    /**
     * Constant that can be returned from the <code>peek</code> methods to
     * indicate that the peek operation is not supported.
     */
    long PEEK_UNSUPPORTED = -1;

    /**
     * Get the next number in this sequence's scheme. An implementation of this
     * interface <b>must </b> guard against multi-threaded access to this method
     * to prevent more than one thread getting the same sequence number.
     */
    long nextNumber();

    /**
     * Get the next number in this sequence's scheme without causing it to move
     * to the next-in-sequence. This method returns the number that will be
     * returned by the next call to <code>nextNumber</code> without actually
     * increasing the sequence. Multiple calls to <code>peek</code> will
     * return the same number until a call to <code>nextNumber</code> is made.
     */
    long peek();

    /**
     * Get the nth next number in this sequence's scheme without causing it to
     * move to the next-in-sequence. This method returns the <code>nth</code>
     * next number in the sequence. This is an optional operation. If a sequence
     * numbering scheme does not support this operation, it should always return
     * {@link #PEEK_UNSUPPORTED} to the caller.
     */
    long peek(long nth);

    /**
     * Reset the sequence scheme to the beginning of the sequence.
     */
    void reset();
}

