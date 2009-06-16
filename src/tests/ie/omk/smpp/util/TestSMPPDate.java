package ie.omk.smpp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import junit.framework.TestCase;

public class TestSMPPDate extends TestCase {

    private final TimeZone calcuttaTimeZone = TimeZone.getTimeZone("Asia/Calcutta");

    public void testDefaultConstructor() {
        SMPPDate d = new SMPPDate();
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getOffset(System.currentTimeMillis());
        offset /= 900000;

        assertFalse(d.isRelative());
        assertEquals(0, d.getYear());
        assertEquals(0, d.getMonth());
        assertEquals(0, d.getDay());
        assertEquals(0, d.getHour());
        assertEquals(0, d.getMinute());
        assertEquals(0, d.getSecond());
        assertEquals(0, d.getTenth());
        assertEquals(offset, d.getUtcOffset());
        assertEquals('+', d.getSign());
    }

    public void testDateConstructor() {
        Date now = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(now);
        TimeZone localZone = TimeZone.getDefault();
        
        SMPPDate d = new SMPPDate(now);
        assertEquals(cal.get(Calendar.YEAR) - 2000, d.getYear());
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

    public void testCalendarConstructor() {
        Calendar cal = new GregorianCalendar();
        SMPPDate d = new SMPPDate(cal);
        assertEquals(cal.get(Calendar.YEAR) - 2000, d.getYear());
        assertEquals(cal.get(Calendar.MONTH) + 1, d.getMonth());
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), d.getDay());
        assertEquals(cal.get(Calendar.HOUR_OF_DAY), d.getHour());
        assertEquals(cal.get(Calendar.MINUTE), d.getMinute());
        assertEquals(cal.get(Calendar.SECOND), d.getSecond());
        assertEquals(cal.get(Calendar.MILLISECOND) / 100, d.getTenth());
        assertEquals(cal.getTimeZone(), d.getTimeZone());
        if (cal.getTimeZone().getRawOffset() < 0) {
            assertEquals('-', d.getSign());
        } else {
            assertEquals('+', d.getSign());
        }
    }

    public void testRelativeConstructor() {
        SMPPDate d = new SMPPDate(2, 5, 12, 9, 55, 3);
        assertTrue(d.isRelative());
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

    public void testEqualsAndHashCodeWithDefaultTimeZone() {
        Calendar now = new GregorianCalendar();
        Calendar other = new GregorianCalendar();
        other.set(Calendar.YEAR, 1956);
        SMPPDate d1 = new SMPPDate(now);
        SMPPDate d2 = new SMPPDate(now);
        SMPPDate d3 = new SMPPDate(other);
        assertEquals(d1, d1);
        assertEquals(d2, d2);
        assertEquals(d1, d2);
        assertEquals(d2, d1);
        assertEquals(d1.hashCode(), d2.hashCode());
        assertFalse(d1.equals(d3));
        assertFalse(d2.equals(d3));
        assertFalse(d3.equals(d1));
        assertFalse(d3.equals(d2));
        assertFalse(d1.hashCode() == d3.hashCode());
        assertFalse(d2.hashCode() == d3.hashCode());

        // Use the java.util.Date constructor
        d2 = new SMPPDate(now.getTime());
        assertEquals(d2, d2);
        assertEquals(d1, d2);
        assertEquals(d2, d1);
        assertEquals(d1.hashCode(), d2.hashCode());
    }
    
    public void testEqualsAndHashCodeWithExplicitTimeZone() {
        Date now = new Date();
        Calendar nowLocal = Calendar.getInstance();
        nowLocal.setTime(now);
        
        Calendar nowElseWhere = Calendar.getInstance();
        nowElseWhere.setTimeZone(calcuttaTimeZone);
        nowElseWhere.setTime(now);
        
        SMPPDate d1 = new SMPPDate(nowLocal);
        SMPPDate d2 = new SMPPDate(nowElseWhere);
        assertFalse(d1.equals(d2));
        assertFalse(d2.equals(d1));
        assertFalse(d1.hashCode() == d2.hashCode());
    }

    public void testParseSMPPDate() {
        try {
            final String absolute1 = "050504123211808+";
            final String absolute2 = "050504123211812-";
            final String absolute3 = "061218111623";
            final String relative = "000000040559000R";
            
            final SimpleTimeZone twoHoursAhead =
                new SimpleTimeZone(7200000, "UTC+02:00");
            final SimpleTimeZone threeHoursBehind =
                new SimpleTimeZone(-10800000, "UTC-03:00");
            
            final Calendar absCalendar1 = Calendar.getInstance();
            absCalendar1.setTimeZone(twoHoursAhead);
            absCalendar1.set(Calendar.YEAR, 2005);
            absCalendar1.set(Calendar.MONTH, 4);
            absCalendar1.set(Calendar.DAY_OF_MONTH, 4);
            absCalendar1.set(Calendar.HOUR_OF_DAY, 12);
            absCalendar1.set(Calendar.MINUTE, 32);
            absCalendar1.set(Calendar.SECOND, 11);
            absCalendar1.set(Calendar.MILLISECOND, 800);

            final Calendar absCalendar2 = Calendar.getInstance();
            absCalendar2.setTimeZone(threeHoursBehind);
            absCalendar2.set(Calendar.YEAR, 2005);
            absCalendar2.set(Calendar.MONTH, 4);
            absCalendar2.set(Calendar.DAY_OF_MONTH, 4);
            absCalendar2.set(Calendar.HOUR_OF_DAY, 12);
            absCalendar2.set(Calendar.MINUTE, 32);
            absCalendar2.set(Calendar.SECOND, 11);
            absCalendar2.set(Calendar.MILLISECOND, 800);
            
            final Calendar absCalendar3 = Calendar.getInstance();
            absCalendar3.set(Calendar.YEAR, 2006);
            absCalendar3.set(Calendar.MONTH, 11);
            absCalendar3.set(Calendar.DAY_OF_MONTH, 18);
            absCalendar3.set(Calendar.HOUR_OF_DAY, 11);
            absCalendar3.set(Calendar.MINUTE, 16);
            absCalendar3.set(Calendar.SECOND, 23);
            
            SMPPDate absDate1 = SMPPDate.parseSMPPDate(absolute1);
            assertEquals(absCalendar1.getTime(), absDate1.getCalendar().getTime());
            assertNotNull(absDate1.getTimeZone());
            assertEquals(2 * 3600000, absDate1.getTimeZone().getRawOffset());
            assertFalse(absDate1.isRelative());
            assertTrue(absDate1.hasTimezone());
            assertEquals(absolute1, absDate1.toString());
            assertEquals('+', absDate1.getSign());

            SMPPDate absDate2 = SMPPDate.parseSMPPDate(absolute2);
            assertEquals(absCalendar2.getTime(), absDate2.getCalendar().getTime());
            assertNotNull(absDate2.getTimeZone());
            assertEquals(3 * -3600000, absDate2.getTimeZone().getRawOffset());
            assertFalse(absDate2.isRelative());
            assertTrue(absDate2.hasTimezone());
            assertEquals(absolute2, absDate2.toString());
            assertEquals('-', absDate2.getSign());

            SMPPDate absDate3 = SMPPDate.parseSMPPDate(absolute3);
            assertNull(absDate3.getTimeZone());
            assertFalse(absDate3.isRelative());
            assertFalse(absDate3.hasTimezone());
            assertEquals(absolute3, absDate3.toString());
            assertEquals((char) 0, absDate3.getSign());
            
            SMPPDate relDate = SMPPDate.parseSMPPDate(relative);
            assertEquals(0, relDate.getYear());
            assertEquals(0, relDate.getMonth());
            assertEquals(0, relDate.getDay());
            assertEquals(4, relDate.getHour());
            assertEquals(5, relDate.getMinute());
            assertEquals(59, relDate.getSecond());
            assertEquals(0, relDate.getTenth());
            assertEquals(0, relDate.getUtcOffset());
            assertNull(relDate.getTimeZone());
            assertTrue(relDate.isRelative());
            assertFalse(relDate.hasTimezone());
        } catch (InvalidDateFormatException x) {
            fail("Bad date format: " + x.toString());
        }
    }

    public void testToString() {
        final String absExpected = "060913165844422+";
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(calcuttaTimeZone);
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 58);
        cal.set(Calendar.SECOND, 44);
        cal.set(Calendar.MILLISECOND, 499);
        SMPPDate smppDate = new SMPPDate(cal);
        assertEquals(absExpected, smppDate.toString());
        
        final String relExpected = "010003041320000R";
        smppDate = new SMPPDate(1, 0, 3, 4, 13, 20);
        assertEquals(relExpected, smppDate.toString());
    }
}
