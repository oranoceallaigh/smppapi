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

public class StringEncoder implements Encoder {

    private static final StringEncoder instance = new StringEncoder();

    private StringEncoder() {
    }

    public static final StringEncoder getInstance() {
	return (instance);
    }

    // XXX document arrayindex exception
    public void writeTo(Tag tag, Object value, byte[] b, int offset) {
	try {
	    String s = value.toString();
	    int len = s.length();

	    byte[] b1 = s.getBytes("US-ASCII");
	    System.arraycopy(b1, 0, b, offset, len);
	    b[offset + len] = (byte)0;
	} catch (java.io.UnsupportedEncodingException x) {
	    // Java spec _requires_ US-ASCII support
	}
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
	try {
	    String s = new String(b, offset, length - 1, "US-ASCII");
	    return (s);
	} catch (java.io.UnsupportedEncodingException x) {
	    // Java spec _requires_ US-ASCII support
	}
	return ("");
    }

    public int getValueLength(Tag tag, Object value) {
	return (value.toString().length() + 1); // 1 for the nul byte
    }
}
