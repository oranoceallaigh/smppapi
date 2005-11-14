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
package ie.omk.smpp;

/**
 * BadCommandIDException
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BadCommandIDException extends ie.omk.smpp.SMPPException {
    private int expected = -1;

    private int actual = -1;

    public BadCommandIDException() {
    }

    /**
     * Construct a new BadCommandIdException with specified message.
     */
    public BadCommandIDException(String s) {
        super(s);
    }

    public BadCommandIDException(String msg, int id) {
        super(msg);
        this.actual = id;
    }

    /**
     * Construct a new BadCommandIdException.
     * 
     * @param expected
     *            The expected Command Id value.
     * @param actual
     *            The actual Command Id value received.
     */
    public BadCommandIDException(int expected, int actual) {
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Get the expected Command id value.
     */
    public int getExpected() {
        return (this.expected);
    }

    /**
     * Get the actual Command id value received.
     */
    public int getActual() {
        return (this.actual);
    }
}