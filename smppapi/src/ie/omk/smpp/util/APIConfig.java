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
import java.io.PrintWriter;
import java.io.StringWriter;

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
 * 
 * <tr><td><code>smppapi.net.so_timeout</code></td><td>Integer</td>
 * <td>This property sets the SO_TIMEOUT property for the sockets used by the
 * {@link ie.omk.smpp.net.TcpLink} objects to connect to the SMSC. Setting this
 * value affects the behavour of any methods that read from the SMSC link. The
 * timeout is specified in milliseconds.</td>
 * </tr>
 * 
 * <tr><td><code>smppapi.net.buffersize_in</code></td><td>Integer</td>
 * <td>Sets the size of the buffer used on the incoming stream connection from
 * the SMSC. A plain value specified the number of bytes. A suffix of 'k' after
 * the number will be interpreted as kilobytes and a suffix of 'm' will be
 * interpreted as megabytes. For example, 4k will allocate a buffer size of 4096
 * bytes.</td>
 * </tr>
 * 
 * <tr><td><code>smppapi.net.buffersize_out</code></td><td>Integer</td>
 * <td>Sets the size of the buffer used on the outgoing stream connection to
 * the SMSC. A plain value specified the number of bytes. A suffix of 'k' after
 * the number will be interpreted as kilobytes and a suffix of 'm' will be
 * interpreted as megabytes. For example, 4k will allocate a buffer size of 4096
 * bytes.</td>
 * </tr>
 * 
 * <tr><td><code>smppapi.net.autoflush</code></td><td>Boolean</td>
 * <td>By default, the {@link ie.omk.smpp.net.SmscLink} class automatically
 * flushes the output stream after every packet written to the output stream. In
 * high-load environments, it may be better to turn this off and manually flush
 * the output stream only when required (after a short period of inactivity, for
 * example).</td>
 * </tr>
 * 
 * <tr><td><code>smppapi.connection.rcv_daemon.ioex_count</code></td><td>Integer</td>
 * <td>The number of I/O exceptions the receiver daemon will accept occurring
 * before exiting.</td>
 * </tr>
 *
 * <tr><td><code>smppapi.event.dispatcher</code></td><td>String</td>
 * <td>The name of a class which implements the
 * {@link ie.omk.smpp.event.EventDispatcher} which will be used as the default
 * event dispatcher for <code>Connection</code> objects.</td>
 * </tr>
 *
 * <tr><td><code>smppapi.event.threaded_dispatcher.pool_size</code></td><td>Integer</td>
 * <td>The size of the thread pool used by the {@link ie.omk.smpp.event.ThreadedEventDispatcher}
 * class.</td>
 * </tr>
 *
 * <tr><td><code>smppapi.event.threaded_dispatcher.queue_size</code></td><td>Integer</td>
 * <td>The size of the event FIFO queue used in the <code>ie.omk.smpp.event.ThreadedEventDispatcher</code>
 * class.</td>
 * </tr>
 *
 * </table>
 *
 */
public class APIConfig extends Properties {

    /** See class description for documentation on the properties.
     */
    public static final String TCP_SOCKET_TIMEOUT = "smppapi.net.tcp.so_timeout";

    /** See class description for documentation on the properties.
     */
    public static final String LINK_BUFFERSIZE_IN = "smppapi.net.buffersize_in";

    /** See class description for documentation on the properties.
     */
    public static final String LINK_BUFFERSIZE_OUT = "smppapi.net.buffersize_out";

    /** See class description for documentation on the properties.
     */
    public static final String LINK_AUTO_FLUSH = "smppapi.net.autoflush";

    /** See class description for documentation on the properties.
     */
    public static final String TOO_MANY_IO_EXCEPTIONS = "smppapi.connection.rcv_daemon.ioex_count";

    /** See class description for documentation on the properties.
     */
    public static final String EVENT_DISPATCHER_CLASS = "smppapi.event.dispatcher";

    /** See class description for documentation on the properties.
     */
    public static final String EVENT_THREAD_POOL_SIZE = "smppapi.event.threaded_dispatcher.pool_size";

    /** See class description for documentation on the properties.
     */
    public static final String EVENT_THREAD_FIFO_QUEUE_SIZE = "smppapi.event.threaded_dispatcher.queue_size";


    private static final Logger logger = Logger.getLogger("ie.omk.smpp.util");

    /** Paths to search for the API properties file. These should always end in
     * the '/' character except for the last entry which should be a blank
     * string.
     */
    private static final String[] SEARCH_PATH = {
	"/", "/ie/", "/ie/omk/", "/ie/omk/smpp/", ""
    };

    /** Name of the resource to load properties from.
     */
    private static final String PROPS_RESOURCE = "smppapi.properties";

    /** The singleton instance of the API configuration.
     */
    private static APIConfig instance = null;

    /** The file the properties got loaded from (including path info).
     */
    private String propsFile = PROPS_RESOURCE;


    /** Construct a new APIConfig object. APIConfig follows the singleton
     * pattern.
     */
    private APIConfig() {
    }

    /** Load the API properties. This method searches for the properties
     * resource in a number of places and uses the
     * <code>Class.getResourceAsStream</code> and the
     * <code>Properties.load</code> method to load them.
     */
    private void loadAPIProperties() {

	try {
	    InputStream is = null;
	    Class c = getClass();

	    for (int i = 0; i < SEARCH_PATH.length && is == null; i++) {
		propsFile = SEARCH_PATH[i] + PROPS_RESOURCE;
		is = c.getResourceAsStream(propsFile);
	    }

	    if (is != null)
		loadAPIPropertiesFromStream(is);
	    else
		logger.warn("Could not find API properties to load");
	} catch (IOException x) {
	    logger.warn("Could not load API properties", x);
	}
    }

    /** Load the properties from a stream. This method actually just calls
     * <code>Properties.load</code> but includes some useful debugging output
     * too.
     */
    private void loadAPIPropertiesFromStream(InputStream stream) throws IOException {
	load(stream);
	if (logger.isDebugEnabled()) {
	    logger.debug("Loaded API properties from " + propsFile);
	    StringWriter w = new StringWriter();
	    list(new PrintWriter(w));
	    logger.debug("\n" + w.toString());
	}
    }

    /** Cause the API properties to be reloaded. The properties will be read
     * from the same location as they were initially loaded from. If the
     * resource has disappeared or is no longer accessible, the properties will
     * not be loaded and <code>false</code> will be returned to the caller.
     * @return true if the properties were successfully reloaded, false
     * otherwise.
     */
    public boolean reloadAPIConfig() {
	logger.debug("Reloading API config properties.");

	try {
	    Class c = getClass();
	    InputStream is = c.getResourceAsStream(propsFile);
	    if (is != null)
		loadAPIPropertiesFromStream(is);
	    else
		logger.warn("Could not reload API properties. File not found: " + propsFile);
	} catch (IOException x) {
	    logger.warn("Could not reload API properties.", x);
	    return (false);
	}

	return (true);
    }

    /** Get the singleton <code>APIConfig</code> instance.
     */
    public static final APIConfig getInstance() {
	if (instance == null) {
	    instance = new APIConfig();
	    instance.loadAPIProperties();
	}

	return (instance);
    }

    /** Get the value for a property.
     * @param property the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException if
     * <code>property</code> is not found in the configuration.
     */
    public String getProperty(String property) throws PropertyNotFoundException {
	String val = super.getProperty(property);
	if (val == null)
	    throw new PropertyNotFoundException(property);
	else
	    return (val);
    }

    /** Get the value for a property, parsed as a Java <code>short</code>.
     * @param property the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException if
     * <code>property</code> is not found in the configuration.
     * @throws ie.omk.smpp.util.InvalidConfigurationException if the value is
     * not a valid short.
     */
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

    /** Get the value for a property, parsed as a Java <code>int</code>.
     * @param property the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException if
     * <code>property</code> is not found in the configuration.
     * @throws ie.omk.smpp.util.InvalidConfigurationException if the value is
     * not a valid integer.
     */
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

    /** Get the value for a property, parsed as a Java <code>long</code>.
     * @param property the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException if
     * <code>property</code> is not found in the configuration.
     * @throws ie.omk.smpp.util.InvalidConfigurationException if the value is
     * not a valid long.
     */
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
     * @throws ie.omk.smpp.util.PropertyNotFoundException if
     * <code>property</code> is not found in the configuration.
     * @throws InvalidConfigurationException if the property has a value that
     * cannot be parsed or interpreted as boolean.
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

    /** Get the base of a number. Numbers can be specified as hex by prefixing
     * a '0x' or '0X', as binary if they are trailed by a 'b' or as octal if
     * they are prefixed with a '0'.
     * @param n the string representing a number with some form of base
     * specifier.
     * @return the base of the integer.
     */
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

    /** Strip the base specifiers out of a string number.
     * @param n the number as a string (including base specifiers).
     * @param base the base of the number.
     * @return a string containing only the number, base specifiers are removed.
     * For example, '0x4a' will cause a return of '4a' if <code>base</code> is
     * specified as 16.
     * @see #getBase
     */
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
