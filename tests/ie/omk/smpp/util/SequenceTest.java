/*
 * Java SMPP API Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net Java SMPP API Homepage:
 * http://smppapi.sourceforge.net/ $Id: SequenceTest.java,v 1.1 2002/07/15
 * 19:50:08 orank Exp $
 */
package ie.omk.smpp.util;

import junit.framework.TestCase;

public class SequenceTest extends TestCase {

    public SequenceTest(String s) {
        super(s);
    }

    /**
     * Assert that the sequence number correctly starts from 1 and increases
     * numerically by 1 each time.
     */
    public void testSequence() {
        DefaultSequenceScheme dss = new DefaultSequenceScheme();

        for (int i = 1; i < 1000; i++)
            assertTrue(dss.nextNumber() == i);
    }

    /**
     * Assert that the sequence properly wraps from MAX_VALUE back to 1.
     */
    public void testSequenceWrap() {
        DefaultSequenceScheme dss = new DefaultSequenceScheme(
                Integer.MAX_VALUE - 1);

        assertTrue(dss.nextNumber() == (Integer.MAX_VALUE - 1));
        assertTrue(dss.nextNumber() == Integer.MAX_VALUE);
        assertTrue(dss.nextNumber() == 1);
    }
}