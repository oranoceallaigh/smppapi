package ie.omk.smpp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;

public class AbsoluteSMPPDateTest extends TestCase {

    public void testAbsoluteDate() {
        Calendar cal = new GregorianCalendar();
        TimeZone localZone = TimeZone.getDefault();
        Date now = new Date();
        cal.setTime(now);
        cal.setTimeZone(localZone);
        
        SMPPDate d = SMPPDate.getAbsoluteInstance(cal, true);
        assertFalse(d.isRelative());
        assertTrue(d.isAbsolute());
        assertEquals(cal.get(Calendar.YEAR), d.getYear());
        assertEquals(cal.get(Calendar.MONTH) + 1, d.getMonth());
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), d.getDay());
        assertEquals(cal.get(Calendar.HOUR_OF_DAY), d.getHour());
        assertEquals(cal.get(Calendar.MINUTE), d.getMinute());
        assertEquals(cal.get(Calendar.SECOND), d.getSecond());
        assertEquals(cal.get(Calendar.MILLISECOND) / 100, d.getTenth());
        assertEquals(cal.getTimeZone(), d.getTimeZone());
        if (localZone.getRawOffset() < 0) {
            assertEquals('-', d.getSign());
        } else {
            assertEquals('+', d.getSign());
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
        assertEquals(now1WithTz, now2WithTz);
        assertEquals(now2WithTz, now1WithTz);
        assertEquals(now1WithTz.hashCode(), now2WithTz.hashCode());

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
