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

/** SMPP packet sequence numbering scheme interface. Implementations of this
 * interface provide a {@link ie.omk.smpp.Connection} with a unique
 * number for each call to <code>nextNumber</code>. This number is used as the
 * packet's sequence number in the SMPP header. The default implementation
 * ({@link DefaultSequenceScheme}) counts monotonically from 1 upwards for each
 * number requested. While this is the SMPP specification's recommended
 * behaviour, there is no requirement for 2 sequentially-requested numbers to be
 * numerically sequential.
 * @author Oran Kelly
 * @version 1.0
 */
public interface SequenceNumberScheme {

    public static final int PEEK_UNSUPPORTED = -1;

    /** Get the next number in this sequence's scheme. An implementation of this
     * interface <b>must</b> guard against multi-threaded access to this method
     * to prevent more than one thread getting the same sequence number.
     */
    public int nextNumber();

    /** Get the next number in this sequence's scheme without causing it to move
     * to the next-in-sequence. This method returns the number that will be
     * returned by the next call to <code>nextNumber</code> without actually
     * increasing the sequence. Multiple calls to <code>peek</code> will return
     * the same number until a call to <code>nextNumber</code> is made.
     */
    public int peek();

    /** Get the nth next number in this sequence's scheme without causing it to
     * move to the next-in-sequence. This method returns the <code>nth</code>
     * next number in the sequence. This is an optional operation. If a sequence
     * numbering scheme does not support this operation, it should always return
     * {@link #PEEK_UNSUPPORTED} to the caller.
     */
    public int peek(int nth);

    /** Reset the sequence scheme to the beginning of the sequence.
     */
    public void reset();
}
