package ie.omk.smpp.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Implementation of {@link ie.omk.smpp.util.SMPPDate} that represents an
 * absolute time specification.
 * 
 * @version $Id: $
 */
class AbsoluteSMPPDate extends SMPPDate {
    private static final long serialVersionUID = 1;
    
    private Calendar calendar;
    private boolean hasTimeZone = true;
    
    AbsoluteSMPPDate(Calendar calendar) {
        this.calendar = calendar;
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
        TimeZone tz = calendar.getTimeZone();
        return (Math.abs(tz.getRawOffset()) / 3600000) * 4;
    }
    
    public char getSign() {
        char sign;
        if (!hasTimeZone) {
            sign = (char) 0;
        } else {
            if (calendar.getTimeZone().getRawOffset() >= 0) {
                sign = '+';
            } else {
                sign = '-';
            }
        }
        return sign;
    }
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbsoluteSMPPDate)) {
            return false;
        }
        AbsoluteSMPPDate other = (AbsoluteSMPPDate) obj;
        return calendar.equals(other.calendar)
            && hasTimeZone == other.hasTimeZone;
    }
    
    public int hashCode() {
        int hc1 = calendar.hashCode();
        return hc1 + (hasTimeZone ? 6203 : 7907);
    }
}
