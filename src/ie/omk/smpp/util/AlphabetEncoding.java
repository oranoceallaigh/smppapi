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

import java.util.Hashtable;

/** SMS Alphabet to Java String mapping interface.
  * Implementations of this interface convert Java Unicode strings into a series
  * of bytes representing the String in a particular SMS alphabet.
  */
public abstract class AlphabetEncoding
    extends ie.omk.smpp.util.MessageEncoding
{
    private static Hashtable dcMapping = new Hashtable();


    /** Convert SMS message text into a Java String. */
    public abstract String decodeString(byte[] b);

    /** Convert a Java String into SMS message text. */
    public abstract byte[] encodeString(String s);


    /** Register an AlphabetEncoding handler for a particular data coding value.
      * @param dcs The data coding value this <code>enc</code> will be used to
      * decode.
      * @param enc An instance of the alphabet encoding class.
      */
    protected static final void registerEncoding(int dcs, AlphabetEncoding enc)
    {
	if (enc == null)
	    throw new NullPointerException("Encoding type cannot be null.");

	if (dcs < 0 || dcs > 255)
	    throw new IllegalArgumentException("DCS outside valid range.");

	dcMapping.put(new Integer(dcs), enc);
    }

    /** Get the registered MessageEncoding handler for data coding <i>dcs</i>.
      * @param dcs The data coding value to match.
      * @return The message encoding type registered, or null if none.
      */
    public static final AlphabetEncoding getEncoding(int dcs)
    {
	return ((AlphabetEncoding)dcMapping.get(new Integer(dcs)));
    }
}
