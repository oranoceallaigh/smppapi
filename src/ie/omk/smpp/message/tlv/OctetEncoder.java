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
package ie.omk.smpp.message.tlv;

public class OctetEncoder implements Encoder {

    private static final OctetEncoder instance = new OctetEncoder();

    private OctetEncoder() {
    }

    public static final OctetEncoder getInstance() {
	return (instance);
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) {
	try {
	    byte[] valBytes = (byte[])value;
	    System.arraycopy(valBytes, 0, b, offset, valBytes.length);
	} catch (ClassCastException x) {
	    throw new BadValueTypeException("Value must be of type byte[]");
	}
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
	byte[] val = new byte[length];
	System.arraycopy(b, offset, val, 0, length);
	return (val);
    }

    public int getValueLength(Tag tag, Object value) {
	try {
	    byte[] b = (byte[])value;
	    return (b.length);
	} catch (ClassCastException x) {
	    throw new BadValueTypeException("Value must be of type byte[]");
	}
    }
}
