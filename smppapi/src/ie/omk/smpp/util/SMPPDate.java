/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
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
 */

package ie.omk.smpp.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

/** Implement methods to convert a Date object into SMPP date format.
  * This class doesn't handle times that aren't UTC yet. The string
  * representation will always end in '00+' indicating a zero offset from UTC.
  */
public class SMPPDate
    implements java.io.Serializable
{
    private Date date = null;

    private static SimpleDateFormat inFormat =
	new SimpleDateFormat("yyMMddHHmmssS");

    private static SimpleDateFormat outFormat =
	new SimpleDateFormat("yyMMddHHmmss");

    private static SimpleDateFormat tenths = new SimpleDateFormat("S");

    /** Create a new SMPPDate. A new java.util.Date is created to represent the
      * current time.
      */
    public SMPPDate()
    {
	// The least 2 significant (decimal) digits of the time are no good tous
	// because SMPP can only represent a date down to tenths of a second.
	// This'll cause problems if we try to compare a created date versus a
	// date parsed from a String. So turf out the digits that are
	// meaningless to the SMPP protocol.
	long l = new Date().getTime();
	l -= (l % 100);
	date = new Date(l);
    }

    /** Create a new SMPPDate.
      * @param d The java.util.Date value to use.
      * @exception java.lang.NullPointerException if d is null.
      */
    public SMPPDate(Date d)
    {
	if (d == null)
	    throw new NullPointerException();

	long l = d.getTime();
	l -= (l % 100);
	date = new Date(l);
    }

    /** Make a java.util.Date object from an Smpp time string.
      * Time strings returned from the SMSC are assumed to be in the
      * format "YYMMDDhhmmss" where
      * <ul>
      * <li> YY = Year (00 - 99)
      * <li> MM = Month (01 - 12)
      * <li> DD = Day (01 - 31)
      * <li> hh = Hour (00 - 23)
      * <li> mm = minute (00 - 59)
      * <li> ss = second (00 - 59)
      * </ul>
      * @param An SMSC time string of the above format.
      * @exception java.lang.NullPointerException If s is null.
      */
    public SMPPDate(String s)
    {
	if (s == null)
	    throw new NullPointerException();

	ParsePosition pos = new ParsePosition(0);
	date = this.inFormat.parse(s, pos);
    }

    /** Get the java.util.Date object this SMPPDate object wraps.
      */
    public Date getDate()
    {
	return (this.date);
    }

    /** Check for equality. This method will compare either another SMPPDate or
     * a <code>java.util.Date</code> against this SMPPDate object and return
     * <code>true</code> iff they represent the same point in time.
     */
    public boolean equals(Object obj)
    {
	if (obj instanceof SMPPDate) {
	    return (this.date.equals(((SMPPDate)obj).date));
	} else if (obj instanceof Date) {
	    // get rid of the centi- and milli-seconds that too fine a precision
	    // for SMPP!
	    long l = ((Date)obj).getTime();
	    l -= (l % 100);
	    return (this.date.equals(new Date(l)));
	} else {
	    return (false);
	}
    }

    /** Get a hashCode for this object. The hashCode for an SMPPDate is the same
     * as that of the <code>java.util.Date</code> it encapsulates.
     */
    public int hashCode()
    {
	return (date.hashCode());
    }
    
    /** Make an SMPP protocol string representing this Date object.
      * Note that the SMPP Protocol defines a string that contains information
      * about the time difference between local time and UTC.  Since the
      * java.util.Date reflects UTC, the time difference will always be set
      * to 00+.
      * @return The string representation as defined by the SMPP protocol.
      */
    public String toString()
    {
	String s = this.outFormat.format(date);
	String t = this.tenths.format(date).substring(0, 1);
	return (new String(s + t + "00+"));
    }

    /** Test driver. Make sure the SMPPDate serializes and deserializes
     * correctly.
     */
    /*public static final void main(String[] args)
    {
	Date td = new Date(); // test date!

	SMPPDate d = new SMPPDate(td);
	String s1 = d.toString();

	if (s1.length() != 16)
	    System.out.println("Date is not in correct format: \""
		    + s1 + "\"");

	SMPPDate d1 = new SMPPDate(s1);

	if (d1.equals(d) && d1.equals(td)) {
	    System.out.println("All tests passed.");
	} else {
	    System.out.println("Parsed date does not equal the created date.");
	    System.out.println("Test date:    \"" + td.toString() + "\"");
	    System.out.println("Created date: \"" + d.date.toString() + "\"");
	    System.out.println("Parsed date:  \"" + d1.date.toString() + "\"");

	    System.out.println("\n");
	    System.out.println("Test date:    \"" + td.getTime() + "\"");
	    System.out.println("Created date: \"" + d.date.getTime() + "\"");
	    System.out.println("Parsed date:  \"" + d1.date.getTime() + "\"");
	}
    }*/
}
