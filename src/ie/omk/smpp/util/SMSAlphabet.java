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

/** SMS Alphabet to Java String mapping interface.
  * Implementations of this interface convert Java Unicode strings into a series
  * of bytes representing the String in a particular SMS alphabet.
  */
public interface SMSAlphabet
{
    /** Convert an SMS message into a Java String. */
    public String decodeString(byte[] b);

    /** Convert a Java String into an SMS message. */
    public byte[] encodeString(String s);

    /** Return the valid data_coding value for this alphabet.
      * This method should return an integer with only bits in the least
      * significant nibble set. See GSM 03.38.
      */
    public int getDataCoding();
}
