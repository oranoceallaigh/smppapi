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
import java.util.TimeZone;

import junit.framework.*;

public class TestSMPPDate extends TestCase {

    protected Calendar a_testCalendar = null;

    protected SMPPDate a_testDate = null;
    protected SMPPDate r_testDate = null;

    protected int a_year = 2002;
    protected int a_month = Calendar.MAY;
    protected int a_day = 7;
    protected int a_hour = 14;
    protected int a_minute = 34;
    protected int a_second = 33;
    protected int a_tenth = 400;
    protected int a_offset = -1;
    protected TimeZone tz = TimeZone.getTimeZone("Asia/Calcutta");

    protected int r_years = 1;
    protected int r_months = 0;
    protected int r_days = 5;
    protected int r_hours = 2;
    protected int r_minutes = 45;
    protected int r_seconds = 12;

    // This string will be completed in setUp with the UTC offset info
    protected String a_expectedDateString = "0205071434334";

    protected String r_expectedDateString = "010005024512000R";

    public TestSMPPDate() {
	super ("ie.omk.smpp.util.SMPPDate test suite");
    }

    public TestSMPPDate(String name) {
	super (name);
    }

    protected void setUp() {
	a_testCalendar = Calendar.getInstance();

	char sign = '+';
	int offset = tz.getRawOffset();
	if (offset < 0)
	    sign = '-';

	offset = Math.abs(offset) / 900000;

	a_testCalendar.setTimeZone(tz);
	a_testCalendar.set(Calendar.YEAR, a_year);
	a_testCalendar.set(Calendar.MONTH, a_month);
	a_testCalendar.set(Calendar.DAY_OF_MONTH, a_day);
	a_testCalendar.set(Calendar.HOUR_OF_DAY, a_hour);
	a_testCalendar.set(Calendar.MINUTE, a_minute);
	a_testCalendar.set(Calendar.SECOND, a_second);
	a_testCalendar.set(Calendar.MILLISECOND, a_tenth);
	
	StringBuffer b = new StringBuffer(a_expectedDateString);
	if (offset < 10)
	    b.append('0');
	
	b.append(Integer.toString(offset)).append(sign);
	a_expectedDateString = b.toString();


	a_testDate = new SMPPDate(a_testCalendar);
	r_testDate = new SMPPDate(r_years, r_months, r_days, r_hours, r_minutes, r_seconds);
    }

    protected void tearDown() throws Exception {
    }

    public void testDefaultConstructor() {
	SMPPDate d = new SMPPDate();

	assertTrue(!d.isRelative());
	assertEquals(0, d.getYear());
	assertEquals(0, d.getMonth());
	assertEquals(0, d.getDay());
	assertEquals(0, d.getHour());
	assertEquals(0, d.getMinute());
	assertEquals(0, d.getSecond());
	assertEquals(0, d.getTenth());
	assertEquals(0, d.getUtcOffset());
	assertEquals('+', d.getSign());
    }

    public void testDateConstructor() {
	Date date = a_testCalendar.getTime();
	SMPPDate d = new SMPPDate(date);

	// Need to do a comparison that is independant of time zone
	// dependancies.
	long l1 = date.getTime();
	long l2 = d.getCalendar().getTimeInMillis();
	assertEquals(l1, l2);
    }

    public void testCalendarConstructor() {
	SMPPDate d = new SMPPDate(a_testCalendar);
	assertEquals(a_expectedDateString, d.toString());
	assertEquals(a_expectedDateString.hashCode(), d.hashCode());
    }

    public void testRelativeConstructor() {
	SMPPDate d = new SMPPDate(r_years, r_months, r_days, r_hours, r_minutes, r_seconds);
	assertTrue(d.isRelative());
	assertEquals("String compare", r_expectedDateString, d.toString());
	assertEquals("HashCode compare", r_expectedDateString.hashCode(), d.hashCode());
    }

    public void testEquality() {
	SMPPDate d1 = new SMPPDate(a_testCalendar);
	SMPPDate d2 = new SMPPDate(a_testCalendar);
	assertEquals(d1, d2);

	d1 = new SMPPDate(a_testCalendar.getTime());
	d2 = new SMPPDate(a_testCalendar.getTime());
	assertEquals(d1, d2);

	SMPPDate d3 = new SMPPDate();
	assertTrue(!d3.equals(d1));
	assertTrue(!d3.equals(d2));
    }

    public void testParsing() {
	try {
	    SMPPDate d1 = SMPPDate.parseSMPPDate(a_expectedDateString);
	    SMPPDate d2 = SMPPDate.parseSMPPDate(r_expectedDateString);

	    assertEquals("Absolute doesn't match", a_testDate, d1);
	    assertEquals("Absolute hashCode doesn't match", a_testDate.hashCode(), d1.hashCode());

	    assertEquals("Relative doesn't match", r_testDate, d2);
	    assertEquals("Relative hashCode doesn't match", r_testDate.hashCode(), d2.hashCode());
	} catch (InvalidDateFormatException x) {
	    fail("Bad date format: " + x.toString());
	}
    }

    public void testSerializing() {
	assertEquals(a_expectedDateString, a_testDate.toString());
	assertEquals(r_expectedDateString, r_testDate.toString());
    }
}
