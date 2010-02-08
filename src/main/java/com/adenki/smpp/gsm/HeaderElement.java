package com.adenki.smpp.gsm;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * An element that can be added to a user data header. Header elements are
 * stateful objects as they may be output to multiple SMS segments.
 * Depending on their implementation, they might be outputting the same
 * information for each segment or different information. Therefore,
 * before a <tt>HeaderElement</tt> can be used to generate message segments
 * via {@link UserData#toSegments()} it must be in its initial state. If
 * a header element has previously been used, returning to its initial
 * state may be accomplished by invoking its {@link #reset()} method.
 * @version $Id$
 * @see UserData
 * @see UserDataImpl
 */
public interface HeaderElement {
    /**
     * Get the total number of octets this header element encodes as
     * <strong>excluding</strong> the IEI and the IEI-Data-Length fields.
     * @return The number of octets in this header element's IE-Data, which
     * may exceed 140 octets.
     */
    int getLength();
    
    /**
     * Reset this <tt>HeaderElement</tt> to its initial state. A
     * <tt>HeaderElement</tt> must be in its initial state before it can
     * be used to generate SMS segments in a {@link UserData} implementation.
     */
    void reset();
    
    /**
     * Determine if this <tt>HeaderElement</tt> has written all its data.
     * This is an <strong>internal method</strong> that is used by
     * {@link UserData} implementations.
     * @return <tt>true</tt> if all of this header element's data has been
     * written to SMS segments. Elements which always recur in every
     * SMS segment (such as concatenation) will never return <tt>true</tt>
     * from this method. 
     */
    boolean isComplete();

    /**
     * Encode this header element to the given ByteBuffer.
     * @param out The byte buffer to write to.
     */
    boolean write(int segmentNum, ByteBuffer buffer);

    /**
     * This method allows <tt>HeaderElements</tt> to post-process SMS
     * segments after they have all been created by
     * {@link UserData#toSegments()}. This is primarily provided for
     * {@link ConcatenatedSms} so that it can fill in the "total message
     * segments" field in each segment once the total count is known.
     * @param segments The generated SMS segments.
     */
    void postProcess(List<ByteBuffer> segments);
    
    /**
     * Determine if this header element should be included in the header
     * of each message in a concatenated message. For example, concatenation
     * information must appear in every segment. Usually, other header
     * element implementations will simply return false from here.
     * @return <tt>true</tt> if this header element will be included in
     * each message of a concatenated message, <tt>false</tt> if it
     * only needs to occur in one segment.
     */
    boolean isRecurring();
}
