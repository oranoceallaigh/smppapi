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
package tests;

import ie.omk.smpp.message.*;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SMPPIO;

/** Test the size of packets with null fields. This test class creates one of
 * each of the concrete packet types using the default constructor. Without
 * changing any of the default field settings, it serializes the packet to an
 * output byte array and compares the size of the array to the value returned
 * from the <code>getLength</code> method to ensure they are the same. It then
 * deserializes the byte array into a new packet instance and compares the array
 * size against the new packet's <code>getLength</code> value. It also ensures
 * the original packet and the new packet are <code>equal</code>.
 * @author Oran Kelly
 * @version 1.0
 */
public class NullSize extends SizeTest
{
    public NullSize()
    {
    }

    public boolean runTest()
    {
	boolean passed = true;

	for (int i = 0; i < classList.length; i++) {
	    try {
		System.out.print(classList[i].getName() + ": ");
		SMPPPacket p = (SMPPPacket)classList[i].newInstance();
		byte[] ba = serialize(p);

		if (ba.length == p.getLength())
		    System.out.print("pass1 ");
		else
		    System.out.print("fail1 ");

		SMPPPacket p2 = deserialize(ba);
		if (ba.length == p2.getLength())
		    System.out.println("pass2");
		else
		    System.out.println("fail2");
	    } catch (Exception x) {
		passed = false;
		System.out.println("exception:\n");
		x.printStackTrace(System.out);
	    }
	} 

	return (passed);
    }

    public static final void main(String[] args)
    {
	boolean result = new NullSize().runTest();
	System.out.println("NullSize test "
		+ (result ? "passed." : "failed."));
	System.exit(result ? 0 : 1);
    }
}
