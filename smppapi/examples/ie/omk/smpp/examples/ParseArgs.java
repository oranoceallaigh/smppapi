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

/** This class implements the common command-line parsing logic
 * for all the example classes.
 * 
 * All can be run with the following syntax:<br />
 * <code>java ExampleClass SmscHost:Port system_id password system_type
 * ton:npi:bind_address</code><br />
 * <table border="1">
 * <tr><td>SmscHost:Port
 * <td>The hostname (or ip address) and port of the SMSC to bind to.
 * Example: "10.0.0.33:5757"
 * 
 * <tr><th>system_id
 * <td>The value of the "system_id" field in the bind packet.
 * To set as null, simply pass the empty string: ''
 * 
 * <tr><th>password
 * <td>The value of the "password" field in the bind packet.
 * To set as null, simply pass the empty string: ''
 * 
 * <tr><th>system_type
 * <td>The value of the "system_type" field in the bind packet.
 * To set as null, simply pass the empty string: ''
 * 
 * <tr><th>ton:npi:bind_address
 * <td>The bind address to send in the bind packet.
 * </table>
 * 
 * It is common that the last two arguments, system_type and bind_address,
 * are always null when binding to your SMSC as your account has
 * default values associated with them. If this is the case, it is
 * sufficient to omit these from the command line.
 * 
 * <br /><br />
 * <b>Examples</b><br />
 * <code>java ie.omk.smpp.examples.SyncTransmitter
 * mySmsc.example.com:1234 smppUser thePassword smpp 0:0:7876365</code>
 * 
 * <br /><br />
 * <code>java ie.omk.smpp.examples.AsyncReceiver2
 * mySmsc.example.com:1234 smppUser thePassword</code>
 * 
 * <br /><br />
 * <code>java ie.omk.smpp.examples.SyncTransceiver
 * mySmsc.example.com:1234 smppUser thePassword '' "1:1:4522[0-9]*"</code>
 */
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
	    String[] s = args[0].split(":");
	    a.put(HOSTNAME, s[0]);
	    if (args.length > 1) {
	        a.put(PORT, s[1]);
	    }

	    a.put(SYSTEM_ID, args[1]);
	    a.put(PASSWORD, args[2]);
	    a.put(SYSTEM_TYPE, args[3]);

	    s = args[4].split(":");

	    if (s.length == 1) {
		a.put(ADDRESS_RANGE, s[0]);
	    } else if (s.length == 3) {
		a.put(ADDRESS_TON, s[0]);
		a.put(ADDRESS_NPI, s[1]);
		a.put(ADDRESS_RANGE, s[2]);
	    }
	} catch (ArrayIndexOutOfBoundsException x) {
	}

	return (a);
    }
}
