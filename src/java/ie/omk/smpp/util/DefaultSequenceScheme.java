package ie.omk.smpp.util;

/**
 * The default sequence numbering scheme. This implementation starts at sequence
 * number 1 and increments by 1 for each number requested, resulting in the
 * sequence numbers <code>1..2..3..4..5..6..7..8..n</code>. If the sequence
 * number reaches as far as <code>Integer.MAX_VALUE</code>, it will wrap back
 * around to 1.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class DefaultSequenceScheme implements SequenceNumberScheme {

    private int num = 1;

    public DefaultSequenceScheme() {
    }

    /**
     * Construct a new DefaultSequenceScheme that starts the sequence from
     * <code>start</code>.
     */
    public DefaultSequenceScheme(int start) {
        num = start;
    }

    public synchronized int nextNumber() {
        if (num == Integer.MAX_VALUE) {
            num = 1;
            return Integer.MAX_VALUE;
        } else {
            return num++;
        }
    }

    public synchronized int peek() {
        return num;
    }

    public synchronized int peek(int nth) {
        return num + nth;
    }

    public synchronized void reset() {
        num = 1;
    }
}

