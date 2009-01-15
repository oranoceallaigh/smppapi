package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test
public class RelativeSMPPDateTest {

    public void testRelativeDate() {
        SMPPDate d = SMPPDate.getRelativeInstance(2, 5, 12, 9, 55, 3);
        assertTrue(d.isRelative());
        assertFalse(d.isAbsolute());
        assertEquals(d.getSign(), 'R');
        assertEquals(d.getYear(), 2);
        assertEquals(d.getMonth(), 5);
        assertEquals(d.getDay(), 12);
        assertEquals(d.getHour(), 9);
        assertEquals(d.getMinute(), 55);
        assertEquals(d.getSecond(), 3);
        assertEquals(d.getTenth(), 0);
        assertEquals(d.getUtcOffset(), 0);
        assertNull(d.getTimeZone());
    }

    public void testEqualsAndHashCode() {
        SMPPDate date1 = SMPPDate.getRelativeInstance(2, 2, 2, 2, 2, 2);
        SMPPDate date2 = SMPPDate.getRelativeInstance(2, 2, 2, 2, 2, 2);
        SMPPDate date3 = SMPPDate.getRelativeInstance(3, 3, 3, 3, 3, 3);

        assertEquals(date1, date1);
        assertEquals(date2, date2);
        assertEquals(date3, date3);
        assertEquals(date2, date1);
        assertEquals(date1, date2);
        assertFalse(date1.equals(date3));
        assertFalse(date3.equals(date1));
        
        assertEquals(date2.hashCode(), date1.hashCode());
        assertFalse(date1.hashCode() == date3.hashCode());
    }
}
