package com.adenki.smpp.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Object to represent an SMPP time specification.
 * There are two types of SMPP time specs: an absolute time and a relative
 * time. Absolute times specify the exact year, month, day, hour, minute,
 * second, tenths of a second and timezone. Relative times specify an
 * offset of years, months, days, hours, minutes and seconds from the
 * current time. Both types of time formats take the same string form
 * "YYMMDDhhmmss[tnnp]", where
 * <ul>
 * <li>YY is the representation of year, 00-99. The specification does not
 * define how these numbers are converted into actual years for absolute times.
 * By default, this API simply adds 2000 to this number to get the year.
 * This can be altered via the {@link SMPPDateFormat} class.</li>
 * <li>MM is the month (01-12).</li>
 * <li>DD is the day of the month (01-31)</li>
 * <li>hh is the hour (00-23)</li>
 * <li>mm is the minute (00-59)</li>
 * <li>ss is the second (00-59)</li>
 * <li>t is tenths of second (0-9)</li>
 * <li>nn is the time difference in quarter hours from UTC</li>
 * <li>p is one of '+', '-' or 'R'. + indicates the time is ahead of UTC, -
 * indicates it is behind UTC and R indicates the time specification is relative
 * to current SMSC time.</li>
 * </ul>
 * <p>
 * See section 7.1 of the SMPP v3.4 specification for the official definition
 * of SMPP time formats.
 * </p>
 * @see SMPPDateFormat
 * @version $Id$
 */
public abstract class SMPPDate implements java.io.Serializable {
    private static final long serialVersionUID = 3L;

    protected SMPPDate() {
    }

    /**
     * Get a date object representing an absolute time, as represented by
     * the supplied <code>calendar</code>. This is the same as calling
     * <code>SMPPDate.getAbsoluteInstance(calendar, true);</code>.
     * @param calendar A <code>java.util.Calendar</code> instance representing
     * the desired date, time and timezone for the SMPP time.
     * @return An SMPPDate object representing the date, time and timezone
     * specified by <code>calendar</code>.
     */
    public static SMPPDate getAbsoluteInstance(Calendar calendar) {
        return new AbsoluteSMPPDate(calendar);
    }
    
    /**
     * Get a date object representing an absolute time, as represented by
     * the supplied <code>calendar</code>. The returned object will either
     * use or ignore the timezone information in the calendar object,
     * depending on whether <code>withTz</code> is <code>true</code> or
     * <code>false</code>.
     * @param calendar A <code>java.util.Calendar</code> instance representing
     * the desired date, time and timezone for the SMPP time.
     * @param withTz <code>true</code> to return an object that uses the
     * timezone information specified in the calendar object, <code>false</code>
     * to return an SMPPDate that does not contain any timezone information.
     * @return An SMPPDate object representing the date, time and, optionally,
     * timezone specified by <code>calendar</code>.
     */
    public static SMPPDate getAbsoluteInstance(Calendar calendar, boolean withTz) {
        return new AbsoluteSMPPDate(calendar, withTz);
    }
    
    /**
     * Get a date object representing a relative time.
     * @param years The number of years.
     * @param months The number of months.
     * @param days The number of days.
     * @param hours The number of hours.
     * @param minutes The number of minutes.
     * @param seconds The number of seconds.
     * @return An SMPPDate object representing the relative time specified
     * by the supplied parameters.
     */
    public static SMPPDate getRelativeInstance(int years,
            int months,
            int days,
            int hours,
            int minutes,
            int seconds) {
        return new RelativeSMPPDate(years, months, days, hours, minutes, seconds);
    }

    /**
     * Get a calendar object that represents the time specified by this
     * SMPPDate. The returned value will be <code>null</code> for relative
     * SMPP times. Also, for absolute SMPP times that do not contain timezone
     * information, the returned calendar&apos;s timezone cannot be trusted -
     * it will simply be initialised to whatever <code>java.util.Calendar</code>
     * considers its default (usually the timezone of the JVM). 
     * @return A calendar object, or <code>null</code> if this is a
     * relative time specification.
     */
    public Calendar getCalendar() {
        return null;
    }
    
    /**
     * Get the year part of this time format. The return value from this will
     * be in the range 0 - 99 for relative times, or will be the full year
     * (such as <code>2007</code>) for absolute times.
     * @return The year part of this time format.
     */
    public abstract int getYear();
    
    /**
     * Get the month part of this time format. This will always return a value
     * in the range 1 - 12.
     * @return The month part of this time format.
     */
    public abstract int getMonth();
    
    /**
     * Get the day part of this time format. This will always return a value
     * in the range 1 - 31.
     * @return The day part of this time format.
     */
    public abstract int getDay();
    
    /**
     * Get the hour part of this time format. This will always return a value
     * in the range 0 - 23.
     * @return The hour part of this time format.
     */
    public abstract int getHour();

    /**
     * Get the minute part of this time format. This will always return a value
     * in the range 0 - 59.
     * @return The minute part of this time format.
     */
    public abstract int getMinute();

    /**
     * Get the second part of this time format. This will always return a value
     * in the range 0 - 59.
     * @return The second part of this time format.
     */
    public abstract int getSecond();

    /**
     * Get the tenths of a second part of this time format. This will always
     * return a value in the range 0 - 9.
     * @return The tenths of a second part of this time format.
     */
    public int getTenth() {
        return 0;
    }
    
    /**
     * Get the UTC offset part of this time format. This will always return a
     * value in the range 0 - 48. The "direction" of the offset should
     * be determined using {@link #getSign()}.
     * @return The UTC offset part of this time format.
     * @see #getTimeZone()
     */
    public int getUtcOffset() {
        return 0;
    }
    
    /**
     * Get the timezone of this SMPPDate.
     * @return The timezone of this SMPPDate object, or <code>null</code> if
     * there is no timezone.
     */
    public TimeZone getTimeZone() {
        return null;
    }
    
    /**
     * Get the timezone offset modifier character. For absolute time formats,
     * this will return one of '+' if the timezone offset is ahead of UTC,
     * '-' if the timezone offset is behind UTC, or <code>(char) 0</code> if
     * there is no timezone information.
     * @return One of '+', '-' or <code>(char) 0</code>.
     */
    public char getSign() {
        return (char) 0;
    }
    
    /**
     * Determine if this date object represents an absolute time.
     * @return <code>true</code> if this object is an absolute time,
     * <code>false</code> otherwise.
     */
    public boolean isAbsolute() {
        return false;
    }
    
    /**
     * Determine if this date object represents a relative time.
     * @return <code>true</code> if this object is a relative time,
     * <code>false</code> otherwise.
     */
    public boolean isRelative() {
        return false;
    }
    
    /**
     * Determine if this date object has timezone information associated
     * with it.
     * @return <code>true</code> if this date object "knows" its timezone,
     * <code>false</code> if it does not.
     */
    public boolean hasTimezone() {
        return false;
    }

    /**
     * Return the length this SMPP date would encode as.
     * @return The number of bytes this SMPP date encodes to on the wire.
     */
    public int getLength() {
        return 0;
    }
}
