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
 * http://smppapi.sourceforge.net/ $Id: AddressTest.java,v 1.2 2004/07/25
 * 12:08:01 orank Exp $
 */
package ie.omk.smpp;

import ie.omk.smpp.util.GSMConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class AddressTest extends TestCase {
    public AddressTest(String n) {
        super(n);
    }

    private void testSize(Address addr) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            addr.writeTo(out);
        } catch (IOException x) {
            fail("Serializing address caused I/O Exception:\n" + x.toString());
            return;
        }

        byte[] array = out.toByteArray();

        Address deserialized = new Address();
        deserialized.readFrom(array, 0);

        assertEquals("serialized. ", addr.getLength(), array.length);
        assertEquals("deserialized.", array.length, deserialized.getLength());
    }

    public void testEmptyFieldSize() {
        testSize(new Address());
    }

    public void testFilledFieldSize() {
        Address addr = new Address();
        addr.setTON(GSMConstants.GSM_TON_INTERNATIONAL);
        addr.setNPI(GSMConstants.GSM_NPI_ISDN);
        addr.setAddress("353851234567");
        testSize(addr);
    }

    public void testEquals() {
        Address a1 = new Address(GSMConstants.GSM_TON_NETWORK,
                GSMConstants.GSM_NPI_NATIONAL, "353851234567");
        Address a2 = new Address(GSMConstants.GSM_TON_NETWORK,
                GSMConstants.GSM_NPI_NATIONAL, "353851234567");
        Address a3 = new Address(GSMConstants.GSM_TON_NATIONAL,
                GSMConstants.GSM_NPI_NATIONAL, "441237654321");

        assertEquals(a1, a2);
        assertTrue(!(a1.equals(a3)));
    }
}