package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test
public class DefaultSequenceSchemeTest {

    public void testNextNumber() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        assertEquals(seq.nextNumber(), 1L);
        assertEquals(seq.nextNumber(), 2L);
        assertEquals(seq.nextNumber(), 3L);
        assertEquals(seq.nextNumber(), 4L);
        assertEquals(seq.nextNumber(), 5L);
        assertEquals(seq.nextNumber(), 6L);
    }
    public void testPeek() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        assertEquals(seq.peek(), 1L);
        assertEquals(seq.nextNumber(), 1L);
        assertEquals(seq.peek(), 2L);
        assertEquals(seq.peek(), 2L);
        assertEquals(seq.peek(), 2L);
        assertEquals(seq.peek(), 2L);
        assertEquals(seq.nextNumber(), 2L);
        assertEquals(seq.peek(), 3L);
        assertEquals(seq.peek(10L), 13L);
    }
    public void testReset() {
        DefaultSequenceScheme seq = new DefaultSequenceScheme();
        while (seq.nextNumber() < 1450L);
        assertEquals(seq.peek(), 1451);
        seq.reset();
        assertEquals(seq.nextNumber(), 1);
    }

    public void testWrap() {
        DefaultSequenceScheme dss = new DefaultSequenceScheme(
                DefaultSequenceScheme.MAX_VALUE - 1);
        assertTrue(dss.nextNumber() == DefaultSequenceScheme.MAX_VALUE - 1L);
        assertTrue(dss.nextNumber() == DefaultSequenceScheme.MAX_VALUE);
        assertTrue(dss.nextNumber() == 1L);
    }
}
