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

import java.util.Hashtable;

public abstract class MessageEncoding
{
    private static Hashtable dcMapping = new Hashtable();

    private int dataCoding = -1;


    protected MessageEncoding(int dataCoding) {
	this.dataCoding = dataCoding;
	dcMapping.put(new Integer(dataCoding), this);
    }

    /** Get the correct data_coding value for this message encoding type.
     */
    public final int getDataCoding() {
	return (dataCoding);
    }

    /** Get the MessageEncoding handler for data coding <i>dcs</i>.
      * @param dcs The data coding value to match.
      * @return The message encoding type registered, or null if none.
      */
    public static final MessageEncoding getEncoding(int dcs) {
	return ((MessageEncoding)dcMapping.get(new Integer(dcs)));
    }

    /** Get the number of bits each encoded message byte represents.
     * This method will almost always return 8. The only known situation
     * at this time where the value should be different is in
     * {@link ie.omk.smpp.util.DefaultAlphabetEncoding}. The reason is
     * that, although the <code>DefaultAlphabetEncoding</code> will
     * generate encoded message byte arrays with one character in each
     * byte location, in reality each byte actually represents only 7-bits.
     * When the message is re-encoded at the SMSC, it will be compressed.
     * This is how the GSM default alphabet can fit 160 characters into
     * a 140-byte payload. Most SMSCs, however, accept the message in
     * its uncompressed form.
     * <p>It is not correct to return a value of 16 from an AlphabetEncoding
     * which encodes characters in 2 or more bytes. The reason that, although
     * a character is encoding as 2 bytes, in the <i>encoded form each byte
     * location still represents 8 bits</i>. This is an important distinction.
     * </p>
     */
    public int getEncodingLength() {
	return (8);
    }
}
