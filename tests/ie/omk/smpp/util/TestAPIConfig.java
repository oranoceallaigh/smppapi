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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class TestAPIConfig extends TestCase {

    public TestAPIConfig(String name) {
	super (name);
    }

    public void testAPIConfig() {
	// The test values should match those in the example properties file in
	// the CVS resources directory.
	try {
	    APIConfig c = APIConfig.getInstance();

	    int propCount = 0;
	    Class apiClass = c.getClass();
	    Field[] f = apiClass.getFields();
	    for (int i = 0; i < f.length; i++) {
		int mod = f[i].getModifiers();
		if (Modifier.isPublic(mod)
			&& Modifier.isStatic(mod)
			&& Modifier.isFinal(mod)
			&& f[i].getType() == String.class) {

		    try {
			String fn = f[i].get(c).toString();
			c.getProperty(fn);
			propCount++;
		    } catch (PropertyNotFoundException x) {
		    }
		}
	    }

	    if (propCount < 1) {
		// Probably no properties file.
		//pass("No properties were loaded. Maybe no props file found?");
	    }
	} catch (Exception x) {
	    fail("Exception caught: " + x.toString());
	}
    }
}
