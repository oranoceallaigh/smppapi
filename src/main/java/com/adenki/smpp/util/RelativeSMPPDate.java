package com.adenki.smpp.util;

/**
 * Implementation of {@link SMPPDate} representing a relative time
 * specification.
 * 
 * @version $Id$
 */
class RelativeSMPPDate extends SMPPDate {
    private static final long serialVersionUID = 2L;
    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    public RelativeSMPPDate(int years,
            int months,
            int days,
            int hours,
            int minutes,
            int seconds) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getDay() {
        return days;
    }

    public int getHour() {
        return hours;
    }

    public int getMinute() {
        return minutes;
    }

    public int getMonth() {
        return months;
    }

    public int getSecond() {
        return seconds;
    }

    public int getYear() {
        return years;
    }
    
    public char getSign() {
        return 'R';
    }
    
    public boolean isRelative() {
        return true;
    }
    
    @Override
    public int getLength() {
        return 17;
    }
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RelativeSMPPDate)) {
            return false;
        }
        RelativeSMPPDate other = (RelativeSMPPDate) obj;
        return years == other.years
            && months == other.months
            && days == other.days
            && hours == other.hours
            && minutes == other.minutes
            && seconds == other.seconds;
    }

    public int hashCode() {
        long val = (long) years * 10000000000L;
        val += (long) months * 100000000L;
        val += (long) days * 1000000L;
        val += (long) hours * 10000L;
        val += (long) minutes * 100;
        val += seconds;
        return new Long(val).hashCode();
    }
}
