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
 *
 * $Id$
 */
package ie.omk.smpp.message.tlv;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ie.omk.smpp.util.SMPPIO;

public class TLVTable implements java.io.Serializable {

    private HashMap map = new HashMap();

    private byte[] opts = null;


    public TLVTable() {
    }

    public void readFrom(byte[] b, int offset, int len) {
	synchronized (map) {
	    opts = new byte[len];
	    System.arraycopy(b, offset, opts, 0, len);
	}
    }

    public void writeTo(OutputStream out) throws IOException {
	synchronized (map) {
	    int len = 0, offset = 0;
	    byte[] buffer = new byte[1024];

	    Iterator i = map.keySet().iterator();
	    while (i.hasNext()) {
		Tag t = (Tag)i.next();
		Encoder enc = t.getEncoder();
		Object v = map.get(t);
		int l = enc.getValueLength(t, v);

		if (buffer.length < (l + 4))
		    buffer = new byte[l + 4];

		SMPPIO.intToBytes(t.getTag(), 2, buffer, 0);
		SMPPIO.intToBytes(l, 2, buffer, 2);
		enc.writeTo(t, v, buffer, 4);

		// write the buffer out.
		out.write(buffer, 0, l + 4);
	    }
	}
    }

    public Object get(Tag tag) {
	Object v = map.get(tag);

	if (v == null)
	    v = getValueFromBytes(tag);

	return (v);
    }

    public boolean isSet(Tag tag) {
	return (map.containsKey(tag));
    }

    // XXX document the invalid size & type exception
    public Object set(Tag tag, Object value) {
	synchronized (map) {
	    if (opts != null) {
		parseAllOpts();
	    }

	    if (tag.getType() == null && value != null) {
		    throw new BadValueTypeException("Tag "
			    + Integer.toHexString(tag.intValue())
			    + " does not accept a value.");
	    } else if (!tag.getType().isAssignableFrom(value.getClass())) {
		throw new BadValueTypeException("Tag "
			+ Integer.toHexString(tag.intValue())
			+ " expects a value of type "
			+ tag.getType());
	    }

	    // Enforce the length restrictions on the Value specified by the
	    // Tag.
	    int min = tag.getMinLength();
	    int max = tag.getMaxLength();
	    int actual = tag.getEncoder().getValueLength(tag, value);

	    if (actual < min || actual > max) {
		throw new InvalidSizeForValueException("Tag "
			+ Integer.toHexString(tag.intValue())
			+ " must have a length in the range "
			+ min + " <= len <= " + max);
	    }

	    return (map.put(tag, value));
	}
    }

    public void clear() {
	synchronized (map) {
	    map.clear();
	}
    }

    public void parseAllOpts() {
	synchronized (map) {
	    int p = 0;

	    while (p < opts.length) {
		Object val = null;
		Tag t = Tag.getTag(SMPPIO.bytesToInt(opts, p, 2));
		Encoder enc = t.getEncoder();
		int l = SMPPIO.bytesToInt(opts, p + 2, 2);

		val = enc.readFrom(t, opts, p + 4, l);
		map.put(t, val);

		p += (4 + l);
	    }

	    opts = null;
	}
    }

    private Object getValueFromBytes(Tag tag) {
	if (opts == null || opts.length < 4)
	    return (null);

	Encoder enc = tag.getEncoder();
	Object val = null;
	int p = 0;
	while (true) {
	    int t = SMPPIO.bytesToInt(opts, p, 2);
	    int l = SMPPIO.bytesToInt(opts, p + 2, 2);

	    if (tag.equals(t)) {
		val = enc.readFrom(tag, opts, p + 4, l);
		synchronized (map) {
		    map.put(tag, val);
		    break;
		}
	    }

	    p += (4 + l);
	    if (p >= opts.length)
		break;
	}

	return (val);
    }

    /** Get the length the parameters in the table would encode as.
     */
    public int getLength() {
	if (opts != null)
	    parseAllOpts();

	int length = 0;
	Tag tag;
	Encoder enc;
	Iterator i = map.keySet().iterator();
	while (i.hasNext()) {
	    tag = (Tag)i.next();
	    enc = tag.getEncoder();
	    length += enc.getValueLength(tag, enc);
	}

	return (length);
    }

    /** Get the set of tags in this TLVTable.
     */
    public java.util.Set tagSet() {
	if (opts != null)
	    parseAllOpts();

	return (map.keySet());
    }

    /** Get a Collection view of the set of values in this TLVTable.
     */
    public java.util.Collection values() {
	if (opts != null)
	    parseAllOpts();

	return (map.values());
    }
}
