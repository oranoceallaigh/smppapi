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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SMPPExample {
    protected static Logger logger = Logger.getLogger("ie.omk.smpp.examples");


    public static final int DEFAULT_PORT = 5016;


    protected static String hostName = "localhost";
    
    protected static int port = DEFAULT_PORT;
    
    protected static String sysID = null;
    
    protected static String password = null;
    
    protected static String sysType = null;

    protected static int ton = 0;

    protected static int npi = 0;

    protected static String sourceRange = null;


    static {
	new SMPPExample();
    }

    protected SMPPExample() {
	PropertyConfigurator.configure(getClass().getResource("smppapi.properties"));
    }

    protected static void parseArgs(String[] args) {
	if (args.length > 1)
	    hostName = args[0];
	else
	    return;

	try {
	    int p = hostName.indexOf(':');
	    if (p > -1) {
		port = Integer.parseInt(hostName.substring(p + 1));
		hostName = hostName.substring(0, p);
	    }
	} catch (NumberFormatException x) {
	    System.err.println("Port is not a valid number, using default.");
	}

	try {
	    sysID = args[1];
	    password = args[2];
	    sysType = args[3];
	    sourceRange = args[4];

	    int p1 = sourceRange.indexOf(':');
	    int p2 = sourceRange.indexOf(':', p1 + 1);

	    if (p1 > -1 && p2 > -1) {
		ton = Integer.parseInt(sourceRange.substring(0, p1));
		npi = Integer.parseInt(sourceRange.substring(p1 + 1, p2));
		sourceRange = sourceRange.substring(p2 + 1);
	    }
	} catch (NumberFormatException x) {
	    logger.warn("TON or NPI is not a valid decimal number,"
		    + " using default.");
	} catch (ArrayIndexOutOfBoundsException x) {
	}
    }
}