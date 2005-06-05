/*
 * Java SMPP API Copyright (C) 1998 - 2005 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net Java SMPP API Homepage:
 * http://smppapi.sourceforge.net/ $Id: UTF16Encoding.java,v 1.6 2004/12/03
 * 17:49:47 orank Exp $
 */

package ie.omk.smpp.util;

public class UTF16Encoding extends ie.omk.smpp.util.AlphabetEncoding {
    private String encType = "UTF-16BE";

    private static final int DCS = 8;

    private static final UTF16Encoding beInstance = new UTF16Encoding(true);

    private static final UTF16Encoding leInstance = new UTF16Encoding(false);

    /**
     * Construct a new UTF16 encoding.
     * 
     * @param bigEndian
     *            true to use UTF-16BE, false to use UTF-16LE.
     */
    private UTF16Encoding(boolean bigEndian) {
        super(DCS);

        if (!bigEndian)
            encType = "UTF-16LE";
    }

    /**
     * Get the singleton instance of the big-endian UTF16Encoding.
     */
    public static UTF16Encoding getInstance() {
        return (beInstance);
    }

    /**
     * Get the singleton instance of either the big-endian or little-endian
     * instance of UTF16Encoding.
     * 
     * @param bigEndian
     *            true to get the big-endian instance, false to get the
     *            little-endian instance.
     */
    public static UTF16Encoding getInstance(boolean bigEndian) {
        if (bigEndian)
            return (beInstance);
        else
            return (leInstance);
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in UTF16 format.
     */
    public String decodeString(byte[] b) {
        if (b == null)
            return ("");

        try {
            return (new String(b, encType));
        } catch (java.io.UnsupportedEncodingException x) {
            return ("");
        }
    }

    /**
     * Encode a Java String to bytes using UTF16.
     */
    public byte[] encodeString(String s) {
        if (s == null)
            return (new byte[0]);

        try {
            return (s.getBytes(encType));
        } catch (java.io.UnsupportedEncodingException x) {
            return (new byte[0]);
        }
    }
}