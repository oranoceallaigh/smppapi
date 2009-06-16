package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.testng.annotations.Test;

@Test
public class SMPPDateFormatTest {

    private SMPPDateFormat dateFormat = new SMPPDateFormat();
    
    public void testParse16CharactersAbsoluteFormat() throws Exception {
        SMPPDate date =
            (SMPPDate) dateFormat.parseObject("061217153023304-");
        bothAssertions(date, 2006, 11, 17, 15, 30, 23, 300, -3600000, '-');
        date = (SMPPDate) dateFormat.parseObject("010101010101000+");
        bothAssertions(date, 2001, 0, 1, 1, 1, 1, 0, 0, '+');
        date = (SMPPDate) dateFormat.parseObject("990731235959948+");
        bothAssertions(date, 2099, 6, 31, 23, 59, 59, 900, 43200000, '+');
    }

    public void testParse16CharacterRelativeFormat() throws Exception {
        SMPPDate date = (SMPPDate) dateFormat.parseObject("021219212121200R");
        dateAssertions(date, 2, 12, 19, 21, 21, 21, 0, 0, 'R');
        date = (SMPPDate) dateFormat.parseObject("000000000000000R");
        dateAssertions(date, 0, 0, 0, 0, 0, 0, 0, 0, 'R');
        date = (SMPPDate) dateFormat.parseObject("999999999999999R");
        dateAssertions(date, 99, 99, 99, 99, 99, 99, 0, 0, 'R');
    }
    
    public void testParse12CharacterAbsoluteFormat() throws Exception {
        SMPPDate date = (SMPPDate) dateFormat.parseObject("030912131313");
        bothAssertions(date, 2003, 8, 12, 13, 13, 13, 0, 0, (char) 0);
    }
    
    public void testParseFailsOnInvalidString() throws Exception {
        try {
            dateFormat.parseObject(null);
            fail("Should not have successfully parsed.");
        } catch (NullPointerException x) {
            // Pass
        }
        try {
            dateFormat.parseObject("");
            fail("Should not have successfully parsed.");
        } catch (ParseException x) {
            // Pass
        }
        try {
            dateFormat.parseObject("12345");
            fail("Should not have successfully parsed.");
        } catch (ParseException x) {
            // Pass
        }
        try {
            dateFormat.parseObject("abcdefghijklmnop");
            fail("Should not have successfully parsed.");
        } catch (ParseException x) {
            // Pass
        }
        try {
            dateFormat.parseObject("999999999999999-");
            fail("Should not have successfully parsed.");
        } catch (ParseException x) {
            // Pass
        }
    }

    public void testFormatAbsolute16() throws Exception {
        // Get a timezone that is 4 hours behind UTC
        TimeZone tz = new SimpleTimeZone(-14400000, "UTC-04:00");
        Calendar cal = new GregorianCalendar(2005, 3, 22, 14, 32, 12);
        cal.set(Calendar.MILLISECOND, 500);
        cal.setTimeZone(tz);
        
        SMPPDate date = SMPPDate.getAbsoluteInstance(cal, true);
        assertEquals(dateFormat.format(date), "050422143212516-");
        
        // Get a timezone 8 hours ahead of UTC
        tz = new SimpleTimeZone(28800000, "UTC+08:00");
        cal.setTimeZone(tz);
        cal.set(2005, 3, 22, 14, 32, 12);
        date = SMPPDate.getAbsoluteInstance(cal, true);
        assertEquals(dateFormat.format(date), "050422143212532+");
    }
    
    public void testFormatAbsolute12() throws Exception {
        Calendar cal = new GregorianCalendar(2005, 3, 22, 14, 32, 12);
        cal.set(Calendar.MILLISECOND, 500);
        SMPPDate date = SMPPDate.getAbsoluteInstance(cal, false);
        assertEquals(dateFormat.format(date), "050422143212");
    }
    
    public void testFormatRelative16() throws Exception {
        SMPPDate date = SMPPDate.getRelativeInstance(
                1, 2, 3, 4, 5, 6);
        assertEquals(dateFormat.format(date), "010203040506000R");
    }
    
    private void bothAssertions(SMPPDate date,
            int year,
            int month,
            int day,
            int hour,
            int min,
            int sec,
            int milli,
            int tzOffset,
            char sign) {
        int mult = 1;
        if (sign != (char) 0 && sign == '-') {
            mult = -1;
        }
        int tz = mult * ((tzOffset / 3600000) * 4);
        dateAssertions(date, year, month + 1, day, hour, min, sec, milli / 100, tz, sign);
        calAssertions(date, year, month, day, hour, min, sec, milli, tzOffset, sign);
    }
    private void dateAssertions(SMPPDate date,
            int year,
            int month,
            int day,
            int hour,
            int min,
            int sec,
            int tenths,
            int tzOffset,
            char sign) {
        assertEquals(date.getYear(), year);
        assertEquals(date.getMonth(), month);
        assertEquals(date.getDay(), day);
        assertEquals(date.getHour(), hour);
        assertEquals(date.getMinute(), min);
        assertEquals(date.getSecond(), sec);
        assertEquals(date.getTenth(), tenths);
        assertEquals(date.getUtcOffset(), tzOffset);
        assertEquals(date.getSign(), sign);
    }
    
    private void calAssertions(SMPPDate date,
            int year,
            int month,
            int day,
            int hour,
            int min,
            int sec,
            int milli,
            int tzOffset,
            char sign) {
        Calendar cal = date.getCalendar();
        assertEquals(cal.get(Calendar.YEAR), year);
        assertEquals(cal.get(Calendar.MONTH), month);
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), day);
        assertEquals(cal.get(Calendar.HOUR_OF_DAY), hour);
        assertEquals(cal.get(Calendar.MINUTE), min);
        assertEquals(cal.get(Calendar.SECOND), sec);
        assertEquals(cal.get(Calendar.MILLISECOND), milli);
        if (sign != (char) 0) {
            assertEquals(cal.getTimeZone().getRawOffset(), tzOffset);
        }
    }
}
