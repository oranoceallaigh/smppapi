package ie.omk.smpp.util;

import junit.framework.TestCase;

public class SequenceTest extends TestCase {

    public SequenceTest(String s) {
        super(s);
    }

    /**
     * Assert that the sequence number correctly starts from 1 and increases
     * numerically by 1 each time.
     */
    public void testSequence() {
        DefaultSequenceScheme dss = new DefaultSequenceScheme();

        for (int i = 1; i < 1000; i++) {
            assertTrue(dss.nextNumber() == i);
        }
    }

    /**
     * Assert that the sequence properly wraps from MAX_VALUE back to 1.
     */
    public void testSequenceWrap() {
        DefaultSequenceScheme dss = new DefaultSequenceScheme(
                Integer.MAX_VALUE - 1);

        assertTrue(dss.nextNumber() == (Integer.MAX_VALUE - 1));
        assertTrue(dss.nextNumber() == Integer.MAX_VALUE);
        assertTrue(dss.nextNumber() == 1);
    }
}

