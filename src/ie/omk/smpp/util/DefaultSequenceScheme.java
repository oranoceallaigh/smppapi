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

/** The default sequence numbering scheme.
 * This implementation starts at sequence number 1 and increments by 1 for each
 * number requested, resulting in the sequence numbers
 * <code>1..2..3..4..5..6..7..8..n</code>. If the sequence number reaches as far
 * as <code>Integer.MAX_VALUE</code>, it will wrap back around to 1.
 * @author Oran Kelly
 * @version 1.0
 */
public class DefaultSequenceScheme implements SequenceNumberScheme {

    private int num = 1;

    public DefaultSequenceScheme() {
    }

    /** Construct a new DefaultSequenceScheme that starts the sequence from
     * <code>start</code>.
     */
    public DefaultSequenceScheme(int start) {
	num = start;
    }

    public synchronized int nextNumber() {
	if (num == Integer.MAX_VALUE) {
	    num = 1;
	    return (Integer.MAX_VALUE);
	} else {
	    return (num++);
	}
    }

    public synchronized int peek() {
	return (num);
    }

    public synchronized int peek(int nth) {
	return (num + nth);
    }

    public synchronized void reset() {
	num = 1;
    }
}
