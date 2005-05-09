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

import ie.omk.smpp.util.SMPPIO;

/**
 * Encode a <code>java.lang.Number</code> to a byte array. The number encoder
 * is for encoding any optional parameters that are defined as integers.
 * NumberEncoder operates on the {@link java.lang.Number}type and therefore
 * accepts values of Byte, Short, Integer and Long. Encoding and decoding of
 * values using this class will never fail due to a lenght mismatch..the value
 * will always be either zero-padded or truncated down to the appropriate size.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class NumberEncoder implements Encoder {

    /**
     * NumberEncoder singleton instance.
     */
    private static final NumberEncoder instance = new NumberEncoder();

    /**
     * Create a new NumberEncoder.
     */
    private NumberEncoder() {
    }

    /**
     * Get the singleton NumberEncoder instance.
     * 
     * @return The singleton NumberEncoder instance.
     */
    public static final NumberEncoder getInstance() {
        return (instance);
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset)
            throws ArrayIndexOutOfBoundsException {

        long longVal = 0, mask = 0;
        Number num;
        try {
            num = (Number) value;
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type "
                    + "java.lang.Number");
        }

        if (value instanceof Byte)
            mask = 0xff;
        else if (value instanceof Short)
            mask = 0xffff;
        else if (value instanceof Integer)
            mask = 0xffffffff;
        else
            mask = 0xffffffffffffffffL;

        longVal = num.longValue() & mask;
        SMPPIO.longToBytes(longVal, tag.getLength(), b, offset);
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length)
            throws ArrayIndexOutOfBoundsException {
        long val = SMPPIO.bytesToLong(b, offset, length);

        if (length <= 4)
            return (new Integer((int) val));
        else
            return (new Long(val));
    }

    public int getValueLength(Tag tag, Object value) {
        return (tag.getLength());
    }
}