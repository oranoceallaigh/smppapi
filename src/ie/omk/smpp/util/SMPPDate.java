/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 * $Id$
 */

package ie.omk.smpp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import java.text.MessageFormat;

/** Object to represent a Date in SMPP format. SMPP dates take on a string form
 * of 'YYMMDDhhmmsstnnp' where
 * <ul>
 * <li>YY is the representation of year, 00-99. No specification is made how
 * these are interpreted. <b>Important</b>: This API assumes year ranges in the
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
public class SMPPDate
    implements java.io.Serializable
{
    protected static final String format =
	"{0,number,00}{1,number,00}{2,number,00}{3,number,00}{4,number,00}"
	+ "{5,number,00}{6,number,0}{7,number,00}{8}";

    protected int year = 0;

    protected int month = 0;

    protected int day = 0;

    protected int hour = 0;

    protected int minute = 0;

    protected int second = 0;

    protected int tenth = 0;

    protected int utcOffset = 0;

    protected char sign = '+';

    protected int hashCode = 0;

    protected TimeZone savedTimeZone = null;


    /** Create a new SMPPDate. All fields will be initialised to zero.
      */
    public SMPPDate()
    {
	hashCode = toString().hashCode();
	savedTimeZone = TimeZone.getDefault();
    }

    /** Create a new SMPPDate representing an absolute time. The timezone offset
     * will be set according to the current default timezone (as determined by
     * the JVM).
     * @param d The Date value to intialise this object to.
     * @throws java.lang.NullPointerException if d is null.
     */
    public SMPPDate(Date d)
    {
	if (d == null)
	    throw new NullPointerException("Cannot use a null Date");

	Calendar cal = Calendar.getInstance();
	cal.setTime(d);
	setFields(cal);
    }

    /** Create a new SMPPDate representing an absolute time.
     * @throws java.lang.NullPointerException if cal is null.
     */
    public SMPPDate(Calendar cal) {
	if (cal == null)
	    throw new NullPointerException("Cannot use a null Calendar");

	setFields(cal);
    }

    /** Create a new SMPPDate representing a relative time.
     */
    public SMPPDate(int years, int months, int days, int hours, int minutes, int seconds) {
	this.year = years;
	this.month = months;
	this.day = days;
	this.hour = hours;
	this.minute = minutes;
	this.second = seconds;
	this.tenth = 0;
	this.utcOffset = 0;
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
	long off = savedTimeZone.getRawOffset();
	if (off < 0)
	    sign = '-';

	// Calculate the difference in quarter hours.
	utcOffset = ((int)Math.abs(off) / 900000);

	// Cache the hashCode
	hashCode = toString().hashCode();
    }

    private void initCalendar(Calendar calendar) {
	calendar.setTimeZone(savedTimeZone);
	calendar.set(Calendar.YEAR, year + 2000);
	calendar.set(Calendar.MONTH, month - 1);
	calendar.set(Calendar.DAY_OF_MONTH, day);
	calendar.set(Calendar.HOUR_OF_DAY, hour);
	calendar.set(Calendar.MINUTE, minute);
	calendar.set(Calendar.SECOND, second);
	calendar.set(Calendar.MILLISECOND, tenth * 100);
    }

    /** Get a calendar object representing the internal time of this SMPPDate.
     * If this object represents a relative time specification, the information
     * returned by this method may be nonsensical.
     */
    public Calendar getCalendar() {
	Calendar cal = Calendar.getInstance();
	initCalendar(cal);
	return (cal);
    }

    /** Get the year, or number of years in a relative time spec.
     */
    public int getYear() {
	return (year);
    }

    /** Get the month, or number of months in a relative time spec.
     * January is month 1.
     */
    public int getMonth() {
	return (month);
    }

    /** Get the day, or number of days in a relative time spec.
     * Day is in the range [1..31]
     */
    public int getDay() {
	return (day);
    }

    /** Get the hour, or number of hours in a relative time spec.
     * Hour is in the range [00..23]
     */
    public int getHour() {
	return (hour);
    }

    /** Get the minute, or number of minutes in a relative time spec.
     * Minute is in the range [00..59]
     */
    public int getMinute() {
	return (minute);
    }

    /** Get the second, or number of seconds in a relative time spec.
     * Second is in the range [00..59]
     */
    public int getSecond() {
	return (second);
    }

    /** Get the tenths of a second. Always zero in a relative time spec.
     * Tenths is in the range [0..9]
     */
    public int getTenth() {
	return (tenth);
    }

    /** Get the number of quarter-hours from UTC the time spec is offset by.
     * This value is always positive. Use {@link #getSign} to determine if the
     * time is ahead of or behind UTC. utcOffset is in the range [0..48]
     */
    public int getUtcOffset() {
	return (utcOffset);
    }

    /** Get the UTC offset qualifier. This flag is '+' to indicate that the time
     * spec is ahead of UTC or '-' to indicate it is behind UTC. If the time
     * spec is a relative time spec, this flag will be 'R'.
     * @see #getUtcOffset
     */
    public char getSign() {
	return (sign);
    }

    /** Test if this SMPPDate represents a relative time specification.
     * @return true is this object represents a relative time spec, false if it
     * represents an absolute time spec.
     */
    public boolean isRelative() {
	return (sign == 'R');
    }

    /** Check for equality against another SMPPDate object.
     */
    public boolean equals(Object obj)
    {
	if (obj instanceof SMPPDate) {
	    SMPPDate d = (SMPPDate)obj;
	    return (hashCode == d.hashCode);
	} else {
	    return (false);
	}
    }

    /** Get a hashCode for this object.
     */
    public int hashCode()
    {
	return (hashCode);
    }

    /** Parse an SMPP date string. This method returns a handle to a newly
     * created SMPPDate object with its fields initialised using the values in
     * the SMPP-formatted date string <code>s</code>.
     * @return a handle to this object.
     */
    public static SMPPDate parseSMPPDate(String s) throws InvalidDateFormatException {
	SMPPDate d = new SMPPDate();

	if (s == null || s.length() == 0) {
	    return (d);
	}

	if (s.length() != 16)
	    throw new InvalidDateFormatException("Date string is incorrect length", s);

	// get the sign of the UTC offset..
	d.sign = s.charAt(15);

	try {
	    d.year = Integer.parseInt(s.substring(0, 2));
	    d.month = Integer.parseInt(s.substring(2, 4));
	    d.day = Integer.parseInt(s.substring(4, 6));
	    d.hour = Integer.parseInt(s.substring(6, 8));
	    d.minute = Integer.parseInt(s.substring(8, 10));
	    d.second = Integer.parseInt(s.substring(10, 12));

	    if (d.sign == 'R') {
		// time is a relative specification.
		d.tenth = 0;
		d.utcOffset = 0;
	    } else {
		d.tenth = Integer.parseInt(s.substring(12, 13));
		d.utcOffset = Integer.parseInt(s.substring(13, 15));
	    }

	    d.hashCode = d.toString().hashCode();
	} catch (NumberFormatException x) {
	    throw new InvalidDateFormatException("Invalid SMPP date string", s);
	}

	return (d);
    }
    
    /** Make an SMPP protocol string representing this Date object.
      * @return The string representation as defined by the SMPP protocol.
      */
    public String toString()
    {
	Object[] args = {
	    new Integer(year),
	    new Integer(month),
	    new Integer(day),
	    new Integer(hour),
	    new Integer(minute),
	    new Integer(second),
	    new Integer(tenth),
	    new Integer(utcOffset),
	    new Character(sign)
	};

	return (MessageFormat.format(format, args));
    }
}
