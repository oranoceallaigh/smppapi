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

import java.util.BitSet;

/** Encoder for bit mask value types. This class encodes and
 * decodes {@link java.util.BitSet} objects to and from
 * byte arrays.
 * @see java.util.BitSet
 */
public class BitmaskEncoder implements Encoder {

    /** The BitmaskEncoder singleton.
     */    
    private static final BitmaskEncoder instance = new BitmaskEncoder();

    /** Create a new BitmaskEncoder. This is a
     * singleton pattern, only one will ever
     * be created.
     */    
    private BitmaskEncoder() {
    }

    /** Get the singleton BitmaskEncoder instance.
     * @return The singleton BitmaskEncoder instance.
     */    
    public static final BitmaskEncoder getInstance() {
	return (instance);
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) throws ArrayIndexOutOfBoundsException {
	try {
	    BitSet bs = (BitSet)value;
	    int l = tag.getLength();

	    for (int i = 0; i < l; i++) {
		b[offset + i] = 0;

		for (int j = 0; j < 8; j++) {
		    if (bs.get((i * 8) + j))
			b[offset + i] |= (byte)(1 << j);
		}
	    }
	} catch (ClassCastException x) {
	    throw new BadValueTypeException("Value must be of type "
		    + "java.util.BitSet");
	}
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) throws ArrayIndexOutOfBoundsException {
	BitSet bs = new BitSet();
	
	for (int i = 0; i < length; i++) {
	    for (int j = 0; j < 8; j++) {
		if ((b[offset + i] & (byte)(1 << j)) > 0)
		    bs.set((i * 8) + j);
	    }
	}

	return (bs);
    }

    public int getValueLength(Tag tag, Object value) {
	return (tag.getLength());
    }
}
