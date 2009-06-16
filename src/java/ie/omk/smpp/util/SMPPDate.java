package ie.omk.smpp.util;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Object to represent a Date in SMPP format. SMPP dates take on a string form
 * of 'YYMMDDhhmmsstnnp' where
 * <ul>
 * <li>YY is the representation of year, 00-99. No specification is made how
 * these are interpreted. <b>Important </b>: This API assumes year ranges in the
 * range 2000 - 2099. If you're using some other form of year specification,
 * you'll run into trouble.
 * <li>MM is the month (01-12).
 * <li>DD is the day of the month (01-31)
 * <li>hh is the hour (00-23)
 * <li>mm is the minute (00-59)
 * <li>ss is the second (00-59)
 * <li>t is tenths of second (0-9)
 * <li>nn is the time difference in quarter hours from UTC
 * <li>p is one of '+', '-' or 'R'. + indicates the time is ahead of UTC, -
 * </ul>
 * indicates it is behind UTC and R indicates the time specification is relative
 * to current SMSC time.
 */
public class SMPPDate implements java.io.Serializable {
    static final long serialVersionUID = -2404447252053261604L;
    
    private static final String FORMAT =
        "{0,number,00}{1,number,00}{2,number,00}{3,number,00}{4,number,00}"
        + "{5,number,00}{6,number,0}{7,number,00}{8}";

    private static final String SHORT_FORMAT =
        "{0,number,00}{1,number,00}{2,number,00}{3,number,00}{4,number,00}"
        + "{5,number,00}";

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int tenth;
    private char sign = '+';
    private int hashCode;

    private TimeZone savedTimeZone;

    /**
     * Create a new SMPPDate. All fields will be initialised to zero.
     */
    public SMPPDate() {
        hashCode = toString().hashCode();
        savedTimeZone = TimeZone.getDefault();
    }

    /**
     * Create a new SMPPDate representing an absolute time. The timezone offset
     * will be set according to the current default timezone (as determined by
     * the JVM).
     * 
     * @param d
     *            The Date value to intialise this object to.
     * @throws java.lang.NullPointerException
     *             if d is null.
     */
    public SMPPDate(Date d) {
        if (d == null) {
            throw new NullPointerException("Cannot use a null Date");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        setFields(cal);
    }

    /**
     * Create a new SMPPDate representing an absolute time.
     * 
     * @throws java.lang.NullPointerException
     *             if cal is null.
     */
    public SMPPDate(Calendar cal) {
        if (cal == null) {
            throw new NullPointerException("Cannot use a null Calendar");
        }

        setFields(cal);
    }

    /**
     * Create a new SMPPDate representing a relative time.
     */
    public SMPPDate(int years, int months, int days, int hours, int minutes,
            int seconds) {
        this.year = years;
        this.month = months;
        this.day = days;
        this.hour = hours;
        this.minute = minutes;
        this.second = seconds;
        this.tenth = 0;
        this.sign = 'R';
        this.savedTimeZone = null;

        this.hashCode = toString().hashCode();
    }

    private void setFields(Calendar calendar) {
        year = calendar.get(Calendar.YEAR) - 2000;
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        tenth = calendar.get(Calendar.MILLISECOND) / 100;
        savedTimeZone = calendar.getTimeZone();

        // Time zone calculation
        sign = '+';
        int off = savedTimeZone.getOffset(System.currentTimeMillis());
        if (off < 0) {
            sign = '-';
        }

        // Cache the hashCode
        hashCode = toString().hashCode();
    }

    private void initCalendar(Calendar calendar) {
        if (savedTimeZone != null) {
            calendar.setTimeZone(savedTimeZone);
        }
        calendar.set(Calendar.YEAR, year + 2000);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, tenth * 100);
    }

    /**
     * Get a calendar object representing the internal time of this SMPPDate.
     * This method should not be used to retrieve a calendar from relative
     * time specifications. That is, if <code>this.isRelative() == true</code>,
     * you should not attempt to use the object returned from this method.
     */
    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        initCalendar(cal);
        return cal;
    }

    /**
     * Get the year, or number of years in a relative time spec.
     */
    public int getYear() {
        return year;
    }

    /**
     * Get the month, or number of months in a relative time spec. January is
     * month 1.
     */
    public int getMonth() {
        return month;
    }

    /**
     * Get the day, or number of days in a relative time spec. Day is in the
     * range [1..31]
     */
    public int getDay() {
        return day;
    }

    /**
     * Get the hour, or number of hours in a relative time spec. Hour is in the
     * range [00..23]
     */
    public int getHour() {
        return hour;
    }

    /**
     * Get the minute, or number of minutes in a relative time spec. Minute is
     * in the range [00..59]
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Get the second, or number of seconds in a relative time spec. Second is
     * in the range [00..59]
     */
    public int getSecond() {
        return second;
    }

    /**
     * Get the tenths of a second. Always zero in a relative time spec. Tenths
     * is in the range [0..9]
     */
    public int getTenth() {
        return tenth;
    }

    /**
     * Get the number of quarter-hours from UTC the time spec is offset by. This
     * value is always positive. Use {@link #getSign} to determine if the time
     * is ahead of or behind UTC. utcOffset is in the range [0..48]
     */
    public int getUtcOffset() {
        int offset = 0;
        if (savedTimeZone != null) {
            offset = savedTimeZone.getOffset(System.currentTimeMillis());
        }
        // Calculate the difference in quarter hours.
        return Math.abs(offset) / 900000;

    }

    /**
     * Get the timezone that this date is in. If this object represents
     * a relative time definition, then this method will return <code>null
     * </code>.
     * @return The timezone of this <code>SMPPDate</code>.
     * @see #isRelative()
     */
    public TimeZone getTimeZone() {
        return savedTimeZone;
    }
    
    /**
     * Get the UTC offset qualifier. This flag is '+' to indicate that the time
     * spec is ahead of UTC or '-' to indicate it is behind UTC. If the time
     * spec is a relative time spec, this flag will be 'R'.
     * 
     * @see #getUtcOffset
     */
    public char getSign() {
        return sign;
    }

    /**
     * Test if this SMPPDate represents a relative time specification.
     * 
     * @return true is this object represents a relative time spec, false if it
     *         represents an absolute time spec.
     */
    public boolean isRelative() {
        return sign == 'R';
    }

    /**
     * Test if this SMPPDate has timezone information associated with it.
     * Relative time specs have no timezone information, neither does the
     * short (12-character) form of the absolute time spec. The short-form
     * absolute format should only be used by an SMSC - applications should
     * never create a short-form format to send to the SMSC.
     * @return
     */
    public boolean hasTimezone() {
        return sign == '+' || sign == '-';
    }
    
    /**
     * Check for equality against another SMPPDate object.
     */
    public boolean equals(Object obj) {
        if (obj instanceof SMPPDate) {
            SMPPDate d = (SMPPDate) obj;
            int diff = (year - d.year)
                + (month - d.month)
                + (day - d.day)
                + (hour - d.hour)
                + (minute - d.minute)
                + (second - d.second)
                + (tenth - d.tenth)
                + ((int) sign - (int) d.sign);
            boolean sameTz = savedTimeZone == null ?
                    d.savedTimeZone == null : savedTimeZone.equals(d.savedTimeZone);
            return diff == 0 && sameTz && sign == d.sign;
        } else {
            return false;
        }
    }

    /**
     * Get a hashCode for this object.
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     * Parse an SMPP date string. This method returns a handle to a newly
     * created SMPPDate object with its fields initialised using the values in
     * the SMPP-formatted date string <code>s</code>.
     * 
     * @return a handle to this object.
     */
    public static SMPPDate parseSMPPDate(String s)
            throws InvalidDateFormatException {
        SMPPDate d = new SMPPDate();

        if (s == null || s.length() == 0) {
            return d;
        }
        if (s.length() != 16 && s.length() != 12) {
            throw new InvalidDateFormatException(
                    "Date string is incorrect length", s);
        }
        boolean longForm = s.length() == 16;
        try {
            d.year = Integer.parseInt(s.substring(0, 2));
            d.month = Integer.parseInt(s.substring(2, 4));
            d.day = Integer.parseInt(s.substring(4, 6));
            d.hour = Integer.parseInt(s.substring(6, 8));
            d.minute = Integer.parseInt(s.substring(8, 10));
            d.second = Integer.parseInt(s.substring(10, 12));
            if (longForm) {
                d.sign = s.charAt(15);
                if (d.sign != 'R') {
                    d.tenth = Integer.parseInt(s.substring(12, 13));
                    int utcOffset = Integer.parseInt(s.substring(13, 15));
                    int rawOffset = utcOffset * 900000;
                    if (d.sign == '-') {
                        rawOffset = -rawOffset;
                    }
                    int hours = utcOffset / 4;
                    int minutes = (utcOffset - (hours * 4)) * 15;
                    Object[] args = new Object[] {
                            new Character(d.sign),
                            Integer.valueOf(hours),
                            Integer.valueOf(minutes),
                    };
                    String id = MessageFormat.format(
                            "UTC{0}{1,number,00}:{1,number,00}", args);
                    d.savedTimeZone = new SimpleTimeZone(rawOffset, id);
                } else {
                    d.savedTimeZone = null;
                }
            } else {
                d.sign = (char) 0;
                d.savedTimeZone = null;
            }
            d.hashCode = d.toString().hashCode();
        } catch (NumberFormatException x) {
            throw new InvalidDateFormatException("Invalid SMPP date string", s);
        }
        return d;
    }

    /**
     * Make an SMPP protocol string representing this Date object.
     * 
     * @return The string representation as defined by the SMPP protocol.
     */
    public String toString() {
        int utcOffset = getUtcOffset();
        Object[] args = {new Integer(year), new Integer(month),
                new Integer(day), new Integer(hour), new Integer(minute),
                new Integer(second), new Integer(tenth),
                new Integer(utcOffset), new Character(sign),
        };
        String format = FORMAT;
        if (sign == (char) 0) {
            format = SHORT_FORMAT;
        }
        return MessageFormat.format(format, args);
    }
}
