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

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import org.apache.log4j.Logger;


/** Internal API configuration. This singleton class holds the configuration for
 * the smppapi. On initialisation, it searches for a file named
 * "smppapi.properties". This file needs to be locatable in the classpath in one
 * of the following locations: /, /ie, /ie/omk, /ie/omk/smpp or the default
 * classloader for this class must be able to find it.
 * <p> Most applications can probably accept the default settings of the API.
 * If, however, you're trying to eke maximum performance out of your
 * application, tweaking these settings may help.</p>
 * <p>Supported API properties are:
 * <table cols="3" border="1" width="100%">
 * <tr><th width="25%">Property name</th><th width="25%">Type</th>
 * <th width="50%">Description</th></tr>
 * <tr><td><code>smppapi.net.so_timeout</code></td><td>Integer</td>
 * <td>This property sets the SO_TIMEOUT property for the sockets used by the
 * {@link ie.omk.smpp.net.TcpLink} objects to connect to the SMSC. Setting this
 * value affects the behavour of any methods that read from the SMSC link.</td>
 * </tr>
 * <tr><td><code>smppapi.net.buffersize_in</code></td><td>Integer</td>
 * <td>Sets the size of the buffer used on the incoming stream connection from
 * the SMSC.</td>
 * </tr>
 * <tr><td><code>smppapi.net.buffersize_out</code></td><td>Integer</td>
 * <td>Sets the size of the buffer used on the outgoing stream connection to
 * the SMSC.</td>
 * </tr>
 * <tr><td><code>smppapi.net.autoflush</code></td><td>Boolean</td>
 * <td>By default, the {@link ie.omk.smpp.net.SmscLink} class automatically
 * flushes the output stream after every packet written to the output stream. In
 * high-load environments, it may be better to turn this off and manually flush
 * the output stream only when required (after a short period of inactivity, for
 * example).</td>
 * </tr>
 * </table>
 */
public class APIConfig extends Properties {

    public static final String TCP_SOCKET_TIMEOUT = "smppapi.net.tcp.so_timeout";

    public static final String LINK_BUFFERSIZE_IN = "smppapi.net.buffersize_in";

    public static final String LINK_BUFFERSIZE_OUT = "smppapi.net.buffersize_out";

    public static final String LINK_AUTO_FLUSH = "smppapi.net.autoflush";

    public static final String TOO_MANY_IO_EXCEPTIONS = "smppapi.connection.rcv_daemon.ioex_count";

    private static final String[] SEARCH_PATH = {
	"/", "/ie/", "/ie/omk/", "/ie/omk/smpp/"
    };

    private static final String PROPS_RESOURCE = "smppapi.properties";

    private static APIConfig instance = null;

    private APIConfig() {
    }

    private void loadAPIProperties() {
	try {
	    InputStream is = null;
	    Class c = getClass();

	    for (int i = 0; i < SEARCH_PATH.length && is == null; i++)
		is = c.getResourceAsStream(SEARCH_PATH[i] + PROPS_RESOURCE);

	    if (is == null)
		is = c.getResourceAsStream(PROPS_RESOURCE);

	    if (is != null)
		load(is);
	    else
		Logger.getLogger("ie.omk.smpp.util").warn("Could not find API properties to load");
	} catch (IOException x) {
	    Logger.getLogger("ie.omk.smpp.util").warn("Could not load API properties", x);
	}
    }

    public static final APIConfig getInstance() {
	if (instance == null) {
	    instance = new APIConfig();
	    instance.loadAPIProperties();
	}

	return (instance);
    }

    public String getProperty(String property) throws PropertyNotFoundException {
	String val = super.getProperty(property);
	if (val == null)
	    throw new PropertyNotFoundException(property);
	else
	    return (val);
    }

    public short getShort(String property) throws InvalidConfigurationException, PropertyNotFoundException {

	short s = 0;
	try {
	    String n = getProperty(property);
	    if (n != null) {
		int base = getBase(n);
		n = stripBaseSpecifiers(n, base);
		s = Short.parseShort(n, base);
	    }
	} catch (NumberFormatException x) {
	    throw new InvalidConfigurationException("Bad property value", property);
	}

	return (s);
    }

    public int getInt(String property) throws InvalidConfigurationException, PropertyNotFoundException {

	int i = 0;
	try {
	    String n = getProperty(property);
	    if (n != null) {
		int base = getBase(n);
		n = stripBaseSpecifiers(n, base);
		i = Integer.parseInt(n, base);
	    }
	} catch (NumberFormatException x) {
	    throw new InvalidConfigurationException("Bad property value", property);
	}

	return (i);
    }

    public long getLong(String property) throws InvalidConfigurationException, PropertyNotFoundException {

	long l = 0;
	try {
	    String n = getProperty(property);
	    if (n != null) {
		int base = getBase(n);
		n = stripBaseSpecifiers(n, base);
		l = Long.parseLong(n, base);
	    }
	} catch (NumberFormatException x) {
	    throw new InvalidConfigurationException("Bad property value", property);
	}

	return (l);
    }

    /** Get a property as a boolean value. Any of 'on', 'yes' or 'true'
     * (irrelevant of case) will evaluate to <code>true</code>. Any of 'off',
     * 'no' or 'false' will evaluate to <code>false</code>. Boolean parameters
     * may also be specified as a number, where zero will equate to
     * <code>false</code> while non-zero will equate to <code>true</code>.
     * All other words will result in an exception being thrown.
     * @throws InvalidConfigurationException, PropertyNotFoundException if the property has a value that
     * cannot be parsed as a boolean specifier.
     */
    public boolean getBoolean(String property) throws InvalidConfigurationException, PropertyNotFoundException {
	boolean b = false;
	String s = getProperty(property).toLowerCase();

	try {
	    int n = Integer.parseInt(s);

	    if (n > 0)
		b = true;
	    else
		b = false;
	} catch (NumberFormatException x) {
	    // It's not a number..
	    if (s.equals("yes") || s.equals("on") || s.equals("true"))
		b = true;
	    else if (s.equals("no") || s.equals("off") || s.equals("false"))
		b = false;
	    else
		throw new InvalidConfigurationException("Bad property value", property);
	}

	return (b);
    }

    private int getBase(String n) {
	int base = 10;

	if (n.startsWith("0x") || n.startsWith("0X"))
	    base = 16;
	else if (n.startsWith("0"))
	    base = 8;
	else if (n.endsWith("b"))
	    base = 2;

	return (base);
    }

    private String stripBaseSpecifiers(String n, int base) {
	switch (base) {
	    case 2:
		n = n.substring(0, n.length() - 1);
		break;

	    case 8:
		n = n.substring(1);
		break;

	    case 16:
		n = n.substring(2);
		break;
	}

	return (n);
    }
}
