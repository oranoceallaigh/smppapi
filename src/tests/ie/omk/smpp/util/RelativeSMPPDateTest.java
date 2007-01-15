package ie.omk.smpp.util;

import junit.framework.TestCase;

public class RelativeSMPPDateTest extends TestCase {

    public void testRelativeDate() {
        SMPPDate d = SMPPDate.getRelativeInstance(2, 5, 12, 9, 55, 3);
        assertTrue(d.isRelative());
        assertFalse(d.isAbsolute());
        assertEquals('R', d.getSign());
        assertEquals(2, d.getYear());
        assertEquals(5, d.getMonth());
        assertEquals(12, d.getDay());
        assertEquals(9, d.getHour());
        assertEquals(55, d.getMinute());
        assertEquals(3, d.getSecond());
        assertEquals(0, d.getTenth());
        assertEquals(0, d.getUtcOffset());
        assertNull(d.getTimeZone());
    }

    public void testEqualsAndHashCode() {
        SMPPDate date1 = SMPPDate.getRelativeInstance(2, 2, 2, 2, 2, 2);
        SMPPDate date2 = SMPPDate.getRelativeInstance(2, 2, 2, 2, 2, 2);
        SMPPDate date3 = SMPPDate.getRelativeInstance(3, 3, 3, 3, 3, 3);

        assertEquals(date1, date1);
        assertEquals(date2, date2);
        assertEquals(date3, date3);
        assertEquals(date1, date2);
        assertEquals(date2, date1);
        assertFalse(date1.equals(date3));
        assertFalse(date3.equals(date1));
        
        assertEquals(date1.hashCode(), date2.hashCode());
        assertFalse(date1.hashCode() == date3.hashCode());
    }
}
