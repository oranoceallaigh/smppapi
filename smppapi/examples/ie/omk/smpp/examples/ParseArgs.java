/*
 * Java implementation of the SMPP v3.3 API
 * Copyright (C) 1998 - 2000 by Oran Kelly
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
 * $Id$
 */
package ie.omk.smpp.examples;

import java.util.HashMap;


public class ParseArgs {

    public static final Object HOSTNAME = new String("hostname");

    public static final Object PORT = new String("port");

    public static final Object SYSTEM_ID = new String("sysid");

    public static final Object SYSTEM_TYPE = new String("systype");

    public static final Object PASSWORD = new String("password");

    public static final Object ADDRESS_TON = new String("ton");

    public static final Object ADDRESS_NPI = new String("npi");

    public static final Object ADDRESS_RANGE = new String ("ar");


    private ParseArgs() {
    }

    public static final HashMap parse(String[] args) {
	HashMap a = new HashMap();

	try {
	    String s;
	    int i = args[0].indexOf(':');
	    if (i >= 0) {
		s = args[0].substring(0, i);
		a.put(HOSTNAME, s);

		s = args[0].substring(i + 1);
		a.put(PORT, s);
	    } else {
		a.put(HOSTNAME, args[0]);
	    }

	    a.put(SYSTEM_ID, args[1]);
	    a.put(PASSWORD, args[2]);
	    a.put(SYSTEM_TYPE, args[3]);

	    int p1 = args[4].indexOf(':');
	    int p2 = args[4].indexOf(':', p1 + 1);

	    if (p1 > -1 && p2 > -1) {
		a.put(ADDRESS_TON, args[4].substring(0, p1));
		a.put(ADDRESS_NPI, args[4].substring(p1 + 1, p2));
		a.put(ADDRESS_RANGE, args[4].substring(p2 + 1));
	    }
	} catch (ArrayIndexOutOfBoundsException x) {
	}

	return (a);
    }
}
