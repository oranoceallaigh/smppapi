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

/** Encoding class representing the ASCII (IA5) alphabet encoding.
 */
public class ASCIIEncoding
    extends ie.omk.smpp.util.AlphabetEncoding
{
    private static final int DCS = 1;

    private static final ASCIIEncoding instance = new ASCIIEncoding();


    /** Construct a new ASCIIEncoding.
     */
    public ASCIIEncoding()
    {
	super (DCS);
    }

    /** Get the singleton instance of ASCIIEncoding.
     */
    public static ASCIIEncoding getInstance() {
	return (instance);
    }

    /** Decode SMS message text to a Java String. The SMS message is expected to
     * be in ASCII format.
     */
    public String decodeString(byte[] b)
    {
	if (b == null)
	    return ("");

	try {
	    return (new String(b, "US-ASCII"));
	} catch (java.io.UnsupportedEncodingException x) {
	    return ("");
	}
    }

    /** Encode a Java String to bytes using the ASCII encoding.
     */
    public byte[] encodeString(String s)
    {
	if (s == null)
	    return (new byte[0]);

	try {
	    return (s.getBytes("US-ASCII"));
	} catch (java.io.UnsupportedEncodingException x) {
	    return (new byte[0]);
	}
    }

    public int getEncodingLength() {
	return (8);
    }
}
