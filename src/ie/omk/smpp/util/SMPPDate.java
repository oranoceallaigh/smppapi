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
 * Java SMPP API author: oran.kelly@ireland.com
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
{
    private Date date = null;

    private SimpleDateFormat inFormat = new SimpleDateFormat("yyMMddHHmmssS");

    private SimpleDateFormat outFormat = new SimpleDateFormat("yyMMddHHmmss");

    private SimpleDateFormat tenths = new SimpleDateFormat("S");

    /** Create a new SMPPDate. A new java.util.Date is created to represent the
      * current time.
      */
    public SMPPDate()
    {
	date = new Date();
    }

    /** Create a new SMPPDate.
      * @param d The java.util.Date value to use.
      * @exception java.lang.NullPointerException if d is null.
      */
    public SMPPDate(Date d)
    {
	if (d == null)
	    throw new NullPointerException();

	date = d;
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
      * @return A java.util.Date object representing the time and date given
      * @exception java.lang.NullPointerException If s is null.
      */
    public SMPPDate(String s)
    {
	if (s == null)
	    throw new NullPointerException();

	ParsePosition pos = new ParsePosition(0);
	date = this.inFormat.parse(s, pos);
    }

    public Date getDate()
    {
	return (this.date);
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
}
