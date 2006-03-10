package ie.omk.smpp.util;

import junit.framework.TestCase;

public class DefaultSequenceSchemeTest extends TestCase {

    public void testNextNumber() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        assertEquals(1, seq.nextNumber());
        assertEquals(2, seq.nextNumber());
        assertEquals(3, seq.nextNumber());
        assertEquals(4, seq.nextNumber());
        assertEquals(5, seq.nextNumber());
        assertEquals(6, seq.nextNumber());
    }
    public void testPeek() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        assertEquals(1, seq.peek());
        assertEquals(1, seq.nextNumber());
        assertEquals(2, seq.peek());
        assertEquals(2, seq.peek());
        assertEquals(2, seq.peek());
        assertEquals(2, seq.peek());
        assertEquals(2, seq.nextNumber());
        assertEquals(3, seq.peek());
        assertEquals(13, seq.peek(10));
    }
    public void testReset() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        while (seq.nextNumber() < 1450);
        assertEquals(1451, seq.peek());
        seq.reset();
        assertEquals(1, seq.nextNumber());
    }
}
