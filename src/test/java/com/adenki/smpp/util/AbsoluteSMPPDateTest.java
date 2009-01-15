package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.testng.annotations.Test;

@Test
public class AbsoluteSMPPDateTest {

    public void testAbsoluteDate() {
        Calendar cal = new GregorianCalendar();
        TimeZone localZone = TimeZone.getDefault();
        Date now = new Date();
        cal.setTime(now);
        cal.setTimeZone(localZone);
        
        SMPPDate d = SMPPDate.getAbsoluteInstance(cal, true);
        assertFalse(d.isRelative());
        assertTrue(d.isAbsolute());
        assertEquals(d.getYear(), cal.get(Calendar.YEAR));
        assertEquals(d.getMonth(), cal.get(Calendar.MONTH) + 1);
        assertEquals(d.getDay(), cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(d.getHour(), cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(d.getMinute(), cal.get(Calendar.MINUTE));
        assertEquals(d.getSecond(), cal.get(Calendar.SECOND));
        assertEquals(d.getTenth(), cal.get(Calendar.MILLISECOND) / 100);
        assertEquals(d.getTimeZone(), cal.getTimeZone());
        if (localZone.getRawOffset() < 0) {
            assertEquals(d.getSign(), '-');
        } else {
            assertEquals(d.getSign(), '+');
        }
    }
    
    public void testEqualsAndHashCode() {
        Calendar now = new GregorianCalendar();
        Calendar other = new GregorianCalendar();
        other.set(Calendar.YEAR, 1956);
        SMPPDate now1WithTz = SMPPDate.getAbsoluteInstance(now);
        SMPPDate now2WithTz = SMPPDate.getAbsoluteInstance(now);
        SMPPDate now3NoTz = SMPPDate.getAbsoluteInstance(now, false);
        SMPPDate otherWithTz = SMPPDate.getAbsoluteInstance(other);

        assertEquals(now1WithTz, now1WithTz);
        assertEquals(now2WithTz, now2WithTz);
        assertEquals(now2WithTz, now1WithTz);
        assertEquals(now1WithTz, now2WithTz);
        assertEquals(now2WithTz.hashCode(), now1WithTz.hashCode());

        assertEquals(now3NoTz, now3NoTz);
        assertFalse(now1WithTz.equals(now3NoTz));
        assertFalse(now3NoTz.equals(now2WithTz));
        assertFalse(now3NoTz.hashCode() == now1WithTz.hashCode());
        
        assertFalse(now1WithTz.equals(otherWithTz));
        assertFalse(now2WithTz.equals(otherWithTz));
        assertFalse(otherWithTz.equals(now1WithTz));
        assertFalse(otherWithTz.equals(now2WithTz));
        assertFalse(now1WithTz.hashCode() == otherWithTz.hashCode());
        assertFalse(now2WithTz.hashCode() == otherWithTz.hashCode());
    }
}
