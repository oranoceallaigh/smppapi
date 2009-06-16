package com.adenki.smpp.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Implementation of {@link com.adenki.smpp.util.SMPPDate} that represents an
 * absolute time specification.
 * 
 * @version $Id$
 */
class AbsoluteSMPPDate extends SMPPDate {
    private static final long serialVersionUID = 2L;
    
    private Calendar calendar;
    private boolean hasTimeZone;
    
    AbsoluteSMPPDate(Calendar calendar) {
        this(calendar, true);
    }
    
    AbsoluteSMPPDate(Calendar calendar, boolean withTimeZone) {
        this.calendar = calendar;
        this.hasTimeZone = withTimeZone;
    }
    
    public Calendar getCalendar() {
        return calendar;
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getSecond() {
        return calendar.get(Calendar.SECOND);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getTenth() {
        return calendar.get(Calendar.MILLISECOND) / 100;
    }
    
    public boolean hasTimezone() {
        return hasTimeZone;
    }

    public boolean isAbsolute() {
        return true;
    }

    public TimeZone getTimeZone() {
        if (hasTimeZone) {
            return calendar.getTimeZone();
        } else {
            return null;
        }
    }
    
    public int getUtcOffset() {
        if (hasTimeZone) {
            TimeZone tz = calendar.getTimeZone();
            int offset = Math.abs(tz.getOffset(System.currentTimeMillis()));
            // Divide by 900k to get the number of 15 minute intervals in the
            // offset.
            return offset / 900000;
        } else {
            return 0;
        }
    }
    
    public char getSign() {
        char sign;
        if (!hasTimeZone) {
            sign = (char) 0;
        } else {
            TimeZone tz = calendar.getTimeZone();
            int offset = tz.getOffset(System.currentTimeMillis());
            if (offset >= 0) {
                sign = '+';
            } else {
                sign = '-';
            }
        }
        return sign;
    }
    
    @Override
    public int getLength() {
        return hasTimeZone ? 17 : 13;
    }
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbsoluteSMPPDate)) {
            return false;
        }
        AbsoluteSMPPDate other = (AbsoluteSMPPDate) obj;
        return hasTimeZone == other.hasTimeZone
            && compareCalendarFields(this.calendar, other.calendar);
    }
    
    public int hashCode() {
        int hc1 = calendar.hashCode();
        return hc1 + (hasTimeZone ? 6203 : 7907);
    }
    
    public String toString() {
        return new StringBuffer(calendar.toString()).append(" hasTz=")
        .append(hasTimeZone).toString();
    }
    
    private boolean compareCalendarFields(Calendar calendar1, Calendar calendar2) {
        boolean equal = true;
        equal &= compareField(calendar1, calendar2, Calendar.YEAR);
        equal &= compareField(calendar1, calendar2, Calendar.MONTH);
        equal &= compareField(calendar1, calendar2, Calendar.DAY_OF_MONTH);
        equal &= compareField(calendar1, calendar2, Calendar.HOUR_OF_DAY);
        equal &= compareField(calendar1, calendar2, Calendar.MINUTE);
        equal &= compareField(calendar1, calendar2, Calendar.SECOND);
        int tenth1 = calendar1.get(Calendar.MILLISECOND) / 100;
        int tenth2 = calendar2.get(Calendar.MILLISECOND) / 100;
        equal &= tenth1 == tenth2;
        if (hasTimeZone) {
            int rawOffset1 = calendar1.getTimeZone().getRawOffset();
            int rawOffset2 = calendar2.getTimeZone().getRawOffset();
            equal &= rawOffset1 == rawOffset2;
        }
        return equal;
    }
    
    private boolean compareField(Calendar calendar1, Calendar calendar2, int field) {
        return calendar1.get(field) == calendar2.get(field);
    }
}
