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


/** "No value" encoder. This encoder type was necessary as there are some
 * optional parameters that have no value. Therefore, it was possible for the
 * tag/value map in <code>TLVTable</code> to have <code>null</code> values in
 * it. As <code>null</code> is also returned from a map when there is no value
 * for a particular key, some way was needed to distinguish between a parameter
 * not set and a parameter having a null value. Hence the encoder.
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class NullEncoder implements Encoder {

    /** NullEncoder singleton instance.
     */    
    private static final NullEncoder instance = new NullEncoder();

    /** Create a new NullEncoder.
     */    
    private NullEncoder() {
    }

    /** Get the singleton NullEncoder instance.
     * @return the singleton NullEncoder instance.
     */
    public static final NullEncoder getInstance() {
	return (instance);
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) {
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
	return (null);
    }

    public int getValueLength(Tag tag, Object value) {
	return (0);
    }
}
