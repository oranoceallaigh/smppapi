package ie.omk.smpp.util;

import junit.framework.TestCase;

public class DefaultSequenceSchemeTest extends TestCase {

    public void testNextNumber() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        assertEquals(1L, seq.nextNumber());
        assertEquals(2L, seq.nextNumber());
        assertEquals(3L, seq.nextNumber());
        assertEquals(4L, seq.nextNumber());
        assertEquals(5L, seq.nextNumber());
        assertEquals(6L, seq.nextNumber());
    }
    public void testPeek() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        assertEquals(1L, seq.peek());
        assertEquals(1L, seq.nextNumber());
        assertEquals(2L, seq.peek());
        assertEquals(2L, seq.peek());
        assertEquals(2L, seq.peek());
        assertEquals(2L, seq.peek());
        assertEquals(2L, seq.nextNumber());
        assertEquals(3L, seq.peek());
        assertEquals(13L, seq.peek(10L));
    }
    public void testReset() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        while (seq.nextNumber() < 1450L);
        assertEquals(1451, seq.peek());
        seq.reset();
        assertEquals(1, seq.nextNumber());
    }

    public void testWrap() {
        DefaultSequenceScheme dss = new DefaultSequenceScheme(
                DefaultSequenceScheme.MAX_VALUE - 1);
        assertTrue(dss.nextNumber() == DefaultSequenceScheme.MAX_VALUE - 1L);
        assertTrue(dss.nextNumber() == DefaultSequenceScheme.MAX_VALUE);
        assertTrue(dss.nextNumber() == 1L);
    }
}
