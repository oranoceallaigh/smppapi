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

    /** Get the number of bits this encoding uses per character. This method
     * only makes proper sense for alphabet encoding classes. For example, the
     * default GSM alphabet encodes characters in 7 bits. Latin-1 encoding uses
     * 8 bits per character and UCS2 uses 16 bits per character. This method
     * always returns 8, unless overridden in the subclass.  
     */
    public int getEncodingLength() {
	return (8);
    }
}
