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

/**
 * Attempt to set a value on a tag that expects a Java type other than that
 * used. This exception gets thrown if an attempt is made, for instance, to set
 * a <code>java.lang.String</code> value on a Tag that is defined as an
 * integer.
 */
public class BadValueTypeException extends RuntimeException {
    /**
     * Create a new BadValueTypeException.
     */
    public BadValueTypeException() {
    }

    /**
     * Create a new BadValueTypeException.
     * 
     * @param msg
     *            Exception message.
     */
    public BadValueTypeException(String msg) {
        super(msg);
    }
}