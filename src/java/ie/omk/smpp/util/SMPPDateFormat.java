package ie.omk.smpp.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Parse a String to an SMPPDate object.
 * @version $Id: $
 */
public class SMPPDateFormat extends Format {
    private static final long serialVersionUID = 1;

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
        if (obj == null || pos == null) {
            throw new NullPointerException();
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
        String s = source.substring(pos.getIndex());
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
        int year = Integer.parseInt(s.substring(0, 2));
        int month = Integer.parseInt(s.substring(2, 4));
        int day = Integer.parseInt(s.substring(4, 6));
        int hour = Integer.parseInt(s.substring(6, 8));
        int minute = Integer.parseInt(s.substring(8, 10));
        int second = Integer.parseInt(s.substring(10, 12));
        SMPPDate date;
        if (absolute) {
            date = checkAndCreateAbsolute(
                    s, year, month, day, hour, minute, second, hasTz);
        } else {
            date = SMPPDate.getRelativeInstance(
                    year, month, day, hour, minute, second);
        }
        pos.setIndex(pos.getIndex() + updatePos);
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
     * Check the bounds of the fields for an absolute date and create an
     * absolute instance if all are within bounds.
     * @param s The date string being parsed.
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @param hasTz If a timezone should be parsed from the string or not.
     * @return An absolute instance of <code>SMPPDate</code>.
     */
    private SMPPDate checkAndCreateAbsolute(
            String s,
            int year,
            int month,
            int day,
            int hour,
            int minute,
            int second,
            boolean hasTz) {
        boundsCheck(year, 0, 99, "Year");
        boundsCheck(month, 1, 12, "Month");
        boundsCheck(day, 1, 31, "Day");
        boundsCheck(hour, 0, 23, "Hour");
        boundsCheck(minute, 0, 59, "Minute");
        Calendar cal;
        if (hasTz) {
            char sign = s.charAt(15);
            int tenth = Integer.parseInt(s.substring(12, 13));
            int utcOffset = Integer.parseInt(s.substring(13, 15));
            boundsCheck(second, 0, 59, "Second");
            boundsCheck(tenth, 0, 9, "Tenths of a second");
            boundsCheck(utcOffset, 0, 48, "UTC offset");
            cal = getCalendar(year, month, day, hour, minute, second,
                    tenth, utcOffset, sign);
        } else {
            cal = getCalendar(
                    year, month, day, hour, minute, second, 0, 0, (char) 0);
        }
        return SMPPDate.getAbsoluteInstance(cal, hasTz);
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
     * Get a valid timezone for the specified UTC offset. If multiple
     * timezones match, the first one will be chosen. If a timezone cannot
     * be found (using <code>java.util.TimeZone.getAvailableIDs(int)</code>),
     * the default timezone will be obtained and its UTC offset altered
     * using <code>TimeZone.setRawOffset(int)</code>.
     * @param utcOffset The offset, in quarter hours, from UTC.
     * @param sign Whether the offset is ahead ('+') or behind ('-') UTC.
     * @return A timezone object.
     */
    private TimeZone getTimeZoneForOffset(int utcOffset, char sign) {
        int rawOffset = utcOffset * 900000;
        if (sign == '-') {
            rawOffset = -rawOffset;
        }
        String[] tzs = TimeZone.getAvailableIDs(rawOffset);
        if (tzs.length > 0) {
            return TimeZone.getTimeZone(tzs[0]);
        } else {
            TimeZone tz = TimeZone.getDefault();
            tz.setRawOffset(utcOffset);
            return tz;
        }
    }
    
    /**
     * Check that a value lies within the max and min range and throw an
     * exception if it does not.
     * @param value The value to test for bounds.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @param field The name of the field being tested - this will be placed
     * into the exception message.
     */
    private void boundsCheck(int value, int min, int max, String field) {
        if (value < min || value > max) {
            StringBuffer msg = new StringBuffer(field)
            .append(" must be a value between ")
            .append(min).append(" and ").append(max).append(".");
            throw new IllegalArgumentException(msg.toString());
        }
    }
}
