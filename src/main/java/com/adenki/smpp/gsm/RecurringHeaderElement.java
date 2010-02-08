package com.adenki.smpp.gsm;

/**
 * Class representing a header element that may recur in multiple
 * segments. Concatenation information must always occur across each
 * segment so that the segments can be reassembled. Most other header
 * elements do not recur, however the specification does allow some
 * elements to optionally recur, such as port addressing.
 * @version $Id$
 */
public abstract class RecurringHeaderElement extends AbstractHeaderElement {

    private boolean recurring;
    
    /**
     * Create a new recurring header element.
     * @param recur <tt>true</tt> if this header element recurs in multiple
     * segments, <tt>false</tt> if it should only occur once.
     */
    public RecurringHeaderElement(boolean recur) {
        recurring = recur;
    }
    
    @Override
    public boolean isRecurring() {
        return recurring;
    }
}
