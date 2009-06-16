package com.adenki.smpp.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * Parse a String to an SMPPDate object.
 * @version $Id$
 */
public class SMPPDateFormat extends Format {
    private static final long serialVersionUID = 2L;

    /**
     * "Local" time specification, no timezone info included.
     */
    private static final String ABS_FORMAT_12 =
        "{0,number,00}{1,number,00}{2,number,00}{3,number,00}{4,number,00}"
        + "{5,number,00}";
    /**
     * Absolute format, specifying timezone information.
     */
    private static final String ABS_FORMAT_16 =
        ABS_FORMAT_12 + "{6,number,0}{7,number,00}{8}";
    /**
     * Relative format.
     */
    private static final String REL_FORMAT_16 = ABS_FORMAT_12 + "000R";
    
    private static final Pattern ABS_PATTERN_16 = Pattern.compile("^\\d{15}[+-]");
    private static final Pattern REL_PATTERN_16 = Pattern.compile("^\\d{15}R");
    private static final Pattern ABS_PATTERN_12 = Pattern.compile("^\\d{12}");

    /**
     * The default year modifier. The year modifier is the value used
     * to convert 2-digit years into their full version. Year modifier is
     * simply added to (or subtracted from) one form to get the other.
     * <br />
     * <code>
     * int twoDigitYear = fullYear - getYearModifier();<br />
     * int fullYear = twoDigitYear + getYearModifier();<br />
     * </code>
     * The default value for this is <code>2000</code>, meaning the allowed
     * values for a 2-digit year (0 to 99) represent the years 2000 to 2099.
     */
    public static final int DEFAULT_YEAR_MODIFIER = 2000;
    
    private int yearModifier = DEFAULT_YEAR_MODIFIER;
    
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (toAppendTo == null || pos == null) {
            throw new NullPointerException();
        }
        if (obj == null) {
            return toAppendTo;
        }
        if (!(obj instanceof SMPPDate)) {
            throw new IllegalArgumentException("Cannot format an object of type "
                    + obj.getClass().getName());
        }
        SMPPDate date = (SMPPDate) obj;
        int year = date.getYear();
        String format = ABS_FORMAT_16;
        if (date.isAbsolute()) {
            year -= yearModifier;
            if (!date.hasTimezone()) {
                format = ABS_FORMAT_12;
            }
        } else if (date.isRelative()) {
            format = REL_FORMAT_16;
        }
        Object[] args = new Object[] {
                Integer.valueOf(year),
                Integer.valueOf(date.getMonth()),
                Integer.valueOf(date.getDay()),
                Integer.valueOf(date.getHour()),
                Integer.valueOf(date.getMinute()),
                Integer.valueOf(date.getSecond()),
                Integer.valueOf(date.getTenth()),
                Integer.valueOf(date.getUtcOffset()),
                Character.valueOf(date.getSign()),
        };
        toAppendTo.append(MessageFormat.format(format, args));
        return toAppendTo;
    }

    /**
     * Parse a string into an {@link SMPPDate}.
     * @param source The string to parse.
     * @param pos The position to begin parsing from.
     * @throws NullPointerException if <code>source</code> is <code>null</code>.
     * @throws IllegalArgumentException if any of the parsed values exceed
     * their allowed range.
     */
    public Object parseObject(String source, ParsePosition pos) {
        String s = source;
        if (pos.getIndex() > 0) {
            s = source.substring(pos.getIndex());
        }
        int updatePos = 0;
        boolean absolute = false;
        boolean hasTz = true;
        if (ABS_PATTERN_16.matcher(s).find()) {
            absolute = true;
            updatePos = 16;
        } else if (REL_PATTERN_16.matcher(s).find()) {
            absolute = false;
            updatePos = 16;
        } else if (ABS_PATTERN_12.matcher(s).find()) {
            absolute = true;
            hasTz = false;
            updatePos = 12;
        } else {
            return null;
        }
        SMPPDate date = null;
        try {
            if (absolute) {
                date = parseAbsoluteDate(s, pos, hasTz);
            } else {
                date = parseRelativeDate(s, pos);
            }
            pos.setIndex(pos.getIndex() + updatePos);
        } catch (SMPPRuntimeException x) {
            // Fall-through...ParsePosition will not get updated, and
            // error index should have been set.
        }
        return date;
    }

    /**
     * Get the value for the year modifier.
     * @return The current value of the year modifier.
     * @see #DEFAULT_YEAR_MODIFIER
     */
    public int getYearModifier() {
        return yearModifier;
    }

    /**
     * Set the value for the year modifier.
     * @param yearModifier The new value for year modifier.
     * @see #DEFAULT_YEAR_MODIFIER
     * @see #getYearModifier()
     */
    public void setYearModifier(int yearModifier) {
        this.yearModifier = yearModifier;
    }

    /**
     * Parse an absolute date from the string.
     * @param s The string to parse from.
     * @param pos The parse position.
     * @param hasTz Parse timezone information if <code>true</code>.
     * @return The parsed SMPP date.
     * @throws SMPPRuntimeException If the date cannot be parsed.
     */
    private SMPPDate parseAbsoluteDate(String s, ParsePosition pos, boolean hasTz) {
        int index = pos.getIndex();
        int year = parseAndCheck(s, pos, index, index + 2, 0, 99);
        int month = parseAndCheck(s, pos, index + 2, index + 4, 1, 12);
        int day = parseAndCheck(s, pos, index + 4, index + 6, 1, 31);
        int hour = parseAndCheck(s, pos, index + 6, index + 8, 0, 23);
        int minute = parseAndCheck(s, pos, index + 8, index + 10, 0, 59);
        int second = parseAndCheck(s, pos, index + 10, index + 12, 0, 59);
        Calendar cal;
        if (hasTz) {
            char sign = s.charAt(index + 15);
            int tenth = parseAndCheck(s, pos, index + 12, index + 13, 0, 9);
            int utcOffset = parseAndCheck(
                    s, pos, index + 13, index + 15, 0, 48);
            cal = getCalendar(year, month, day, hour, minute, second,
                    tenth, utcOffset, sign);
        } else {
            cal = getCalendar(
                    year, month, day, hour, minute, second, 0, 0, (char) 0);
        }
        return SMPPDate.getAbsoluteInstance(cal, hasTz);
    }

    /**
     * Parse a relative date from the string.
     * @param s The string to parse the date from.
     * @param pos The parse position.
     * @return The parsed SMPP date.
     * @throws SMPPRuntimeException If the date cannot be parsed.
     */
    private SMPPDate parseRelativeDate(String s, ParsePosition pos) {
        int index = pos.getIndex();
        int year = parseAndCheck(s, pos, index, index + 2, 0, 99);
        int month = parseAndCheck(s, pos, index + 2, index + 4, 0, 99);
        int day = parseAndCheck(s, pos, index + 4, index + 6, 0, 99);
        int hour = parseAndCheck(s, pos, index + 6, index + 8, 0, 99);
        int minute = parseAndCheck(s, pos, index + 8, index + 10, 0, 99);
        int second = parseAndCheck(s, pos, 10, 12, 0, 99);
        return SMPPDate.getRelativeInstance(
                year, month, day, hour, minute, second);
    }
    
    /**
     * Get a {@link java.util.Calendar} object, initialising its fields
     * to the supplied values. The values should be specified in their
     * SMPP form (that is, year is 0-99, month is 1-12) and this method will
     * convert them appropriately before placing them in the calendar.
     * @param year The year.
     * @param month The month.
     * @param day The day.
     * @param hour The hour.
     * @param minute The minute.
     * @param second The second.
     * @param tenths Tenths of a second.
     * @param utcOffset The offset from UTC.
     * @param sign The direction of the UTC offset ('+' or '-').
     * @return An initialised Calendar object.
     */
    private Calendar getCalendar(int year,
            int month,
            int day,
            int hour,
            int minute,
            int second,
            int tenths,
            int utcOffset,
            char sign) {
        Calendar calendar = Calendar.getInstance();
        if (sign != (char) 0) {
            calendar.setTimeZone(getTimeZoneForOffset(utcOffset, sign));
        }
        calendar.set(Calendar.YEAR, year + yearModifier);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, tenths * 100);
        return calendar;
    }

    /**
     * Get a timezone for the specified UTC offset.
     * A new {@link SimpleTimeZone} will be created and given the
     * specified offset from UTC.
     * @param utcOffset The offset, in quarter hours, from UTC.
     * @param sign Whether the offset is ahead ('+') or behind ('-') UTC.
     * @return A timezone object.
     */
    private TimeZone getTimeZoneForOffset(int utcOffset, char sign) {
        int rawOffset = utcOffset * 900000;
        if (sign == '-') {
            rawOffset = -rawOffset;
        }
        int hours = utcOffset / 4;
        int minutes = (utcOffset - (hours * 4)) * 15;
        String id = String.format("UTC%c%02d:%02d", sign, hours, minutes);
        TimeZone tz = new SimpleTimeZone(rawOffset, id);
        return tz;
    }
    
    /**
     * Parse an integer from a string and verify the value lies within
     * a maximum and minimum range. If it does
     * not, update the <code>ParsePosition&apos;s</code> error index to point
     * to <code>start</code> and throw an <code>SMPPRuntimeException</code>.
     * <code>SMPPRuntimeException</code> is also thrown in the case of a
     * <code>NumberFormatException</code>.
     * @param s The string to parse the integer from.
     * @param pos The parse position to update in case of error.
     * @param start The start of the substring containing the integer.
     * @param end The end of the substring containing the integer.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     */
    private int parseAndCheck(String s,
            ParsePosition pos,
            int start,
            int end,
            int min,
            int max) {
        try {
            int n = Integer.parseInt(s.substring(start, end));
            if (n < min || n > max) {
                throw new NumberFormatException();
            }
            return n;
        } catch (NumberFormatException x) {
            pos.setErrorIndex(start);
            throw new SMPPRuntimeException();
        }
    }
}
