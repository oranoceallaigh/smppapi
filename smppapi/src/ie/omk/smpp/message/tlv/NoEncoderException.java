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

/** No encoder found for a Java type. An attempt was made to define a new tag
 * with a value type that the API does not have a known encoder for. The
 * application should define a new encoder and define the tag passing that
 * encoder to the
 * {@link ie.omk.smpp.message.tlv.Tag#defineTag(int, java.lang.Class, ie.omk.smpp.message.tlv.Encoder, int)}
 * method.
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class NoEncoderException extends RuntimeException {
    
    public Class type = null;

    /** Create a new NoEncoderException.
     * @param type The Java type that no encoder was found for.
     */
    public NoEncoderException(java.lang.Class type) {
    }

    /** Create a new NoEncoderException.
     * @param type The Java type that no encoder was found for.
     * @param msg The exception message.
     */
    public NoEncoderException(java.lang.Class type, String msg) {
	super (msg);
    }
}
