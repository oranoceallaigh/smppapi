package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.testng.annotations.Test;

@Test
public class AbsoluteSMPPDateTest {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    
    public void testAbsoluteDateInUTC() {
        Calendar cal = new GregorianCalendar();
        Date now = new Date();
        cal.setTime(now);
        cal.setTimeZone(UTC);
        
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
        assertEquals(d.getSign(), '+');
    }
    
    public void testDaylightSavingsTimeZone() throws Exception {
        // Create two similar time zones, each one is 1 hour behind
        // UTC in standard time and one hour ahead of UTC in daylight
        // savings. One of them will be in daylight savings "now", the
        // other will not.
        Calendar dstStart = Calendar.getInstance();
        dstStart.add(Calendar.MONTH, -2);
        Calendar dstEnd = Calendar.getInstance();
        dstEnd.add(Calendar.MONTH, 4);
        SimpleTimeZone aheadInDst = new SimpleTimeZone(
                -3600000,
                "AheadInDst",
                dstStart.get(Calendar.MONTH),
                dstStart.get(Calendar.DAY_OF_WEEK),
                -1,
                3600000,
                dstEnd.get(Calendar.MONTH),
                dstEnd.get(Calendar.DAY_OF_WEEK),
                -1,
                3600000,
                7200000);
        SimpleTimeZone behindWhenNotInDst = new SimpleTimeZone(
                -3600000,
                "BehindNotInDst",
                dstEnd.get(Calendar.MONTH),
                dstEnd.get(Calendar.DAY_OF_WEEK),
                -1,
                3600000,
                dstStart.get(Calendar.MONTH),
                dstStart.get(Calendar.DAY_OF_WEEK),
                -1,
                3600000,
                7200000);

        assertTrue(aheadInDst.inDaylightTime(new Date()));
        assertFalse(behindWhenNotInDst.inDaylightTime(new Date()));
        assertTrue(aheadInDst.getRawOffset() < 0);
        assertTrue(behindWhenNotInDst.getRawOffset() < 0);
        assertTrue(aheadInDst.getOffset(System.currentTimeMillis()) > 0);
        assertTrue(behindWhenNotInDst.getOffset(System.currentTimeMillis()) < 0);
        
        Calendar now = Calendar.getInstance(aheadInDst);
        SMPPDate date = SMPPDate.getAbsoluteInstance(now, true);
        assertTrue(date.hasTimezone());
        assertEquals(date.getUtcOffset(), 4);
        assertEquals(date.getSign(), '+');
        
        now = Calendar.getInstance(behindWhenNotInDst);
        date = SMPPDate.getAbsoluteInstance(now, true);
        assertTrue(date.hasTimezone());
        assertEquals(date.getUtcOffset(), 4);
        assertEquals(date.getSign(), '-');
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
