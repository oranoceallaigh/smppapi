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

/** Encoding class representing the Latin-1 (ISO-8859-1) alphabet encoding.
 */
public class Latin1Encoding
    extends ie.omk.smpp.util.AlphabetEncoding
{
    private static final int DCS = 3;

    private static final Latin1Encoding instance = new Latin1Encoding();


    /** Construct a new Latin1Encoding.
     */
    private Latin1Encoding()
    {
	super (DCS);
    }

    /** Get the singleton instance of Latin1Encoding.
     */
    public static Latin1Encoding getInstance() {
	return (instance);
    }

    /** Decode SMS message text to a Java String. The SMS message is expected to
     * be in Latin-1 format.
     */
    public String decodeString(byte[] b)
    {
	if (b == null)
	    return ("");

	try {
	    return (new String(b, "ISO-8859-1"));
	} catch (java.io.UnsupportedEncodingException x) {
	    return (null);
	}
    }

    /** Encode a Java String to bytes using Latin1.
     */
    public byte[] encodeString(String s)
    {
	if (s == null)
	    return (new byte[0]);

	try {
	    return (s.getBytes("ISO-8859-1"));
	} catch (java.io.UnsupportedEncodingException x) {
	    return (null);
	}
    }
}
