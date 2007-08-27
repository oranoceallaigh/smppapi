package ie.omk.smpp.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal API configuration. This class holds the configuration for
 * the smppapi. On initialisation, it searches for a file named
 * "smppapi.properties". This file needs to be locatable in the classpath in one
 * of the following locations: /, /ie, /ie/omk, /ie/omk/smpp or the default
 * classloader for this class must be able to find it.
 * <p>
 * Most applications can probably accept the default settings of the API. If,
 * however, you're trying to eke maximum performance out of your application,
 * tweaking these settings may help.
 * </p>
 * <p>
 * Supported API properties are: <table cols="3" border="1" width="100%">
 * <tr>
 * <th width="25%">Property name</th>
 * <th width="25%">Type</th>
 * <th width="50%">Description</th>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.lax_versions</code></td>
 * <td>Boolean</td>
 * <td>
 * Enable or disable interpreting interface_version values of
 * 0x00 thru 0x32 (inclusive) as SMPP version 3.3. The specification
 * is not entirely clear in its statement on whether this is allowed
 * or not.
 * </td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.buffersize_in</code></td>
 * <td>Integer</td>
 * <td>Sets the size of the buffer used on the incoming stream connection from
 * the SMSC. A plain value specified the number of bytes. A suffix of 'k' after
 * the number will be interpreted as kilobytes and a suffix of 'm' will be
 * interpreted as megabytes. For example, 4k will allocate a buffer size of 4096
 * bytes.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.buffersize_out</code></td>
 * <td>Integer</td>
 * <td>Sets the size of the buffer used on the outgoing stream connection to
 * the SMSC. A plain value specified the number of bytes. A suffix of 'k' after
 * the number will be interpreted as kilobytes and a suffix of 'm' will be
 * interpreted as megabytes. For example, 4k will allocate a buffer size of 4096
 * bytes.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.autoflush</code></td>
 * <td>Boolean</td>
 * <td>By default, the {@link ie.omk.smpp.net.SmscLink}class automatically
 * flushes the output stream after every packet written to the output stream. In
 * high-load environments, it may be better to turn this off and manually flush
 * the output stream only when required (after a short period of inactivity, for
 * example).</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.autoclose_snoop</code></td>
 * <td>Boolean</td>
 * <td>If snoop streams are set on the SMSC link object and this value is true
 * (the default), the snoop streams will be closed when the link is closed. If
 * false, the snoop streams will be flushed and left open when the link is
 * closed.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.link_timeout</code></td>
 * <td>Long</td>
 * <td>Sets the timeout in milliseconds for network links. This value affects
 * how long network reads should block for but its exact interpretation is
 * link-implementation specific. For <code>TcpLink</code>, this value represents
 * the <code>SO_TIMEOUT</code> setting on the TCP/IP socket.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.connection.bind_timeout</code></td>
 * <td>Long</td>
 * <td>The length of time, in milliseconds, to wait for a bind response packet
 * after sending a bind request. If a packet is not received within this time
 * period, the network connection is closed. A negative value or zero means wait
 * indefinitely.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.connection.rcv_daemon.ioex_count</code></td>
 * <td>Integer</td>
 * <td>The number of I/O exceptions the receiver daemon will accept occurring
 * before exiting.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.event.dispatcher</code></td>
 * <td>String</td>
 * <td>The name of a class which implements the
 * {@link ie.omk.smpp.event.EventDispatcher}which will be used as the default
 * event dispatcher for <code>Connection</code> objects.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.event.threaded_dispatcher.pool_size</code></td>
 * <td>Integer</td>
 * <td>The size of the thread pool used by the
 * {@link ie.omk.smpp.event.ThreadedEventDispatcher}class.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.event.threaded_dispatcher.queue_size</code></td>
 * <td>Integer</td>
 * <td>The size of the event FIFO queue used in the
 * <code>ie.omk.smpp.event.ThreadedEventDispatcher</code> class.</td>
 * </tr>
 * 
 * </table>
 *  
 */
public final class APIConfig extends Properties {
    private static final String BAD_PROPERTY_VALUE = "Bad property value";

    static final long serialVersionUID = 3668742926704484281L;
    
    /**
     * See class description for documentation on the properties.
     * 
     * @deprecated use LINK_TIMEOUT
     */
    public static final String TCP_SOCKET_TIMEOUT = "smppapi.net.tcp.so_timeout";

    /**
     * See class description for documentation on the properties.
     */
    public static final String LAX_VERSIONS = "smppapi.lax_versions";
    
    /**
     * See class description for documentation on the properties.
     */
    public static final String LINK_BUFFERSIZE_IN = "smppapi.net.buffersize_in";

    /**
     * See class description for documentation on the properties.
     */
    public static final String LINK_BUFFERSIZE_OUT = "smppapi.net.buffersize_out";

    /**
     * See class description for documentation on the properties.
     */
    public static final String LINK_AUTO_FLUSH = "smppapi.net.autoflush";

    /**
     * See class description for documentation on the properties.
     */
    public static final String LINK_AUTOCLOSE_SNOOP = "smppapi.net.autoclose_snoop";

    /**
     * See class description for documentation on the properties.
     */
    public static final String LINK_TIMEOUT = "smppapi.net.link_timeout";

    /**
     * See class description for documentation on the properties.
     */
    public static final String TOO_MANY_IO_EXCEPTIONS = "smppapi.connection.rcv_daemon.ioex_count";

    /**
     * See class description for documentation on the properties.
     */
    public static final String EVENT_DISPATCHER_CLASS = "smppapi.event.dispatcher";

    /**
     * See class description for documentation on the properties.
     */
    public static final String EVENT_THREAD_POOL_SIZE = "smppapi.event.threaded_dispatcher.pool_size";

    /**
     * See class description for documentation on the properties.
     */
    public static final String EVENT_THREAD_FIFO_QUEUE_SIZE = "smppapi.event.threaded_dispatcher.queue_size";

    /**
     * See class description for documentation on the properties.
     */
    public static final String BIND_TIMEOUT = "smppapi.connection.bind_timeout";

    private static final Log LOGGER = LogFactory.getLog(APIConfig.class);

    /**
     * Paths to search for the API properties file. These should always end in
     * the '/' character except for the last entry which should be a blank
     * string.
     */
    private static final String[] SEARCH_PATH = {"/", "/ie/", "/ie/omk/",
            "/ie/omk/smpp/", "", };

    /**
     * Name of the resource to load properties from.
     */
    private static final String PROPS_RESOURCE = "smppapi.properties";

    /**
     * The singleton instance of the API configuration.
     */
    private static APIConfig instance;

    /**
     * The URL that API properties are loaded from (including path info).
     */
    private URL propsURL;

    /**
     * Construct a new APIConfig object which reads properties from the
     * default properties resource.
     */
    public APIConfig() {
        this.propsURL = getDefaultPropertiesResource();
    }
    
    /**
     * Construct a new APIConfig object which reads properties from the
     * specified URL.
     * @param propertiesURL The URL to read properties from.
     */
    public APIConfig(URL propertiesURL) {
        this.propsURL = propertiesURL;
    }

    /**
     * Cause the API properties to be reloaded. The properties will be re-read
     * from the same location as they were initially loaded from. If the
     * resource has disappeared or is no longer accessible, the properties will
     * not be loaded and <code>false</code> will be returned to the caller.
     * 
     * @return true if the properties were successfully reloaded, false
     *         otherwise.
     */
    public boolean reloadAPIConfig() {
        LOGGER.debug("Reloading API config properties.");
        try {
            loadAPIProperties();
        } catch (IOException x) {
            LOGGER.warn("Could not reload API properties.", x);
            return false;
        }
        return true;
    }

    /**
     * Get the <code>APIConfig</code> instance. If the
     * <code>APIConfig</code> instance has not yet been initialised then
     * this method will cause the configuration to be read from the default
     * properties resource. The default resource will be searched for at
     * the following locations in the classpath:
     * /smppapi.properties
     * /ie/smppapi.properties
     * /ie/omk/smppapi.properties
     * /ie/omk/smpp/smppapi.properties
     * smppapi.properties
     * @return An initialised instance of <code>APIConfig</code>.
     */
    public static APIConfig getInstance() {
        if (instance == null) {
            try {
                instance = new APIConfig();
                instance.loadAPIProperties();
            } catch (IOException x) {
                LOGGER.error("Could not load API properties from default resource", x);
            }
        }
        return instance;
    }

    /**
     * Set the URL which <code>APIConfig</code> reads its properties from
     * and load them.
     * @param properties
     */
    public static void configure(URL properties) {
        try {
            if (instance == null) {
                instance = new APIConfig(properties);
            } else {
                instance.propsURL = properties;
            }
            instance.loadAPIProperties();
        } catch (IOException x) {
            LOGGER.error("Could not load API config from " + properties, x);
        }
    }
    
    /**
     * Get the value for a property.
     * 
     * @param property
     *            the name of the property to retrieve.
     * @return The value for <code>property</code>.
     * @throws ie.omk.smpp.util.PropertyNotFoundException
     *             if <code>property</code> is not found in the configuration.
     */
    public String getProperty(String property) throws PropertyNotFoundException {
        String val = super.getProperty(property);
        if (val == null) {
            throw new PropertyNotFoundException(property);
        } else {
            return val;
        }
    }

    /**
     * Get the value for a property or return a default value if it is not set.
     * @param property The name of the property to retrieve.
     * @param defaultValue The value to return if <code>property</code> is not
     * set.
     * @return The value for <code>property</code>.
     */
    public String getProperty(String property, String defaultValue) {
        String val = super.getProperty(property);
        if (val == null) {
            val = defaultValue;
        }
        return val;
    }

    /**
     * Get the value of a property, parsed as a <code>short</code>. If the
     * property is not set, the default value is returned.
     * @param property The name of the property to retrieve the value for.
     * @param defaultValue The default value to return if the property is
     * not set.
     * @return The value for <code>property</code>.
     * @throws InvalidConfigurationException If the value cannot be parsed as
     * a <code>short</code>.
     */
    public short getShort(String property, short defaultValue)
            throws InvalidConfigurationException {
        short s;
        try {
            s = getShort(property);
        } catch (PropertyNotFoundException x) {
            s = defaultValue;
        }
        return s;
    }
    
    /**
     * Get the value for a property, parsed as a Java <code>short</code>.
     * 
     * @param property
     *            the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException
     *             if <code>property</code> is not found in the configuration.
     * @throws ie.omk.smpp.util.InvalidConfigurationException
     *             if the value is not a valid short.
     */
    public short getShort(String property)
            throws InvalidConfigurationException, PropertyNotFoundException {
        
        int i = getInt(property);
        if (i < Short.MIN_VALUE || i > Short.MAX_VALUE) {
            throw new InvalidConfigurationException("Property value exceeds "
                    + "valid short range: " + i, property);
        }
        return (short) i;
    }
    
    /**
     * Get the value for a property, parsed as a Java <code>int</code>. If
     * the property is not found, the default value is returned.
     * 
     * @param property
     *            the name of the property to retrive.
     * @param defaultValue
     *            the value to return if the property does not exist.
     * @throws ie.omk.smpp.util.InvalidConfigurationException
     *             if the value is not a valid integer.
     */
    public int getInt(String property, int defaultValue)
            throws InvalidConfigurationException {
        try {
            return getInt(property);
        } catch (PropertyNotFoundException x) {
        }

        return defaultValue;
    }

    /**
     * Get the value for a property, parsed as a Java <code>int</code>.
     * 
     * @param property
     *            the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException
     *             if <code>property</code> is not found in the configuration.
     * @throws ie.omk.smpp.util.InvalidConfigurationException
     *             if the value is not a valid integer.
     */
    public int getInt(String property) throws InvalidConfigurationException,
            PropertyNotFoundException {

        long l;
        try {
            String n = getProperty(property);
            l = convertToNumber(n);
            if (l < (long) Integer.MIN_VALUE || l > (long) Integer.MAX_VALUE) {
                throw new InvalidConfigurationException("Property value exceeds"
                        + " valid int range: " + l, property);
            }
        } catch (NumberFormatException x) {
            throw new InvalidConfigurationException(BAD_PROPERTY_VALUE,
                    property);
        }
        return (int) l;
    }

    /**
     * Get the value for a property, parsed as a Java <code>long</code>. If
     * the property is not found, the default value is returned.
     * 
     * @param property
     *            the name of the property to retrive.
     * @param defaultValue
     *            the value to return if the property does not exist.
     * @throws ie.omk.smpp.util.InvalidConfigurationException
     *             if the value is not a valid long.
     */
    public long getLong(String property, long defaultValue)
            throws InvalidConfigurationException {
        long l;
        try {
            l = getLong(property);
        } catch (PropertyNotFoundException x) {
            l = defaultValue;
        }
        return l;
    }

    /**
     * Get the value for a property, parsed as a Java <code>long</code>.
     * 
     * @param property
     *            the name of the property to retrive.
     * @throws ie.omk.smpp.util.PropertyNotFoundException
     *             if <code>property</code> is not found in the configuration.
     * @throws ie.omk.smpp.util.InvalidConfigurationException
     *             if the value is not a valid long.
     */
    public long getLong(String property) throws InvalidConfigurationException,
            PropertyNotFoundException {

        long l;
        try {
            String n = getProperty(property);
            l = convertToNumber(n);
        } catch (NumberFormatException x) {
            throw new InvalidConfigurationException(BAD_PROPERTY_VALUE,
                    property);
        }
        return l;
    }

    /**
     * Get a property as a boolean value. Any of 'on', 'yes' or 'true'
     * (irrelevant of case) will evaluate to <code>true</code>. Any of 'off',
     * 'no' or 'false' will evaluate to <code>false</code>. Boolean
     * parameters may also be specified as a number, where zero will equate to
     * <code>false</code> while non-zero will equate to <code>true</code>.
     * All other words will result in an InvalidConfigurationException being
     * thrown.
     * 
     * @param property
     *            the name of the property to look up.
     * @param defaultValue
     *            the value to return if the property does not exist.
     * @throws InvalidConfigurationException
     *             if the property has a value that cannot be parsed or
     *             interpreted as boolean.
     */
    public boolean getBoolean(String property, boolean defaultValue)
            throws InvalidConfigurationException {
        try {
            return getBoolean(property);
        } catch (PropertyNotFoundException x) {
        }

        return defaultValue;
    }

    /**
     * Get a property as a boolean value. Any of 'on', 'yes' or 'true'
     * (irrelevant of case) will evaluate to <code>true</code>. Any of 'off',
     * 'no' or 'false' will evaluate to <code>false</code>. Boolean
     * parameters may also be specified as a number, where zero will equate to
     * <code>false</code> while non-zero will equate to <code>true</code>.
     * All other words will result in an exception being thrown.
     * 
     * @throws ie.omk.smpp.util.PropertyNotFoundException
     *             if <code>property</code> is not found in the configuration.
     * @throws InvalidConfigurationException
     *             if the property has a value that cannot be parsed or
     *             interpreted as boolean.
     */
    public boolean getBoolean(String property)
            throws InvalidConfigurationException, PropertyNotFoundException {
        boolean b = false;
        String s = getProperty(property).toLowerCase();

        try {
            int n = Integer.parseInt(s);
            if (n > 0) {
                b = true;
            } else {
                b = false;
            }
        } catch (NumberFormatException x) {
            // It's not a number..
            if ("yes".equals(s) || "on".equals(s) || "true".equals(s) || "1".equals(s)) {
                b = true;
            } else if ("no".equals(s) || "off".equals(s) || "false".equals(s) || "0".equals(s)) {
                b = false;
            } else {
                throw new InvalidConfigurationException(
                        BAD_PROPERTY_VALUE, property, s);
            }
        }

        return b;
    }

    /**
     * Try to locate the default smppapi properties resource.
     * @return A URL pointing to the default properties resource, or
     * <code>null</code> if it cannot be found.
     */
    private URL getDefaultPropertiesResource() {
        URL url = null;
        Class c = getClass();
        for (int i = 0; i < SEARCH_PATH.length && url == null; i++) {
            url = c.getResource(SEARCH_PATH[i] + PROPS_RESOURCE);
        }
        return url;
    }

    /**
     * Load the properties.
     */
    private void loadAPIProperties() throws IOException {
        if (propsURL != null) {
            load(propsURL.openStream());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Loaded API properties from " + propsURL);
                StringWriter w = new StringWriter();
                list(new PrintWriter(w));
                LOGGER.debug("\n" + w.toString());
            }
        }
    }

    /**
     * Convert a number string into a <code>long</code>, taking into account
     * base and multiplication specifiers.
     * @param num The String representing the number.
     * @return The parsed number.
     * @throws NumberFormatException If the String cannot be parsed as a number.
     */
    long convertToNumber(final String num) throws NumberFormatException {
        int base = 10;
        long multiplier = 1;
        String s;
        
        if (num.startsWith("0x") || num.startsWith("0X")) {
            base = 16;
            s = num.substring(2);
        } else if (num.endsWith("b")) {
            base = 2;
            s = num.substring(0, num.length() - 1);
        } else if (num.endsWith("k")) {
            multiplier = 1024L;
            s = num.substring(0, num.length() - 1);
        } else if (num.endsWith("m")) {
            multiplier = 1048576L;
            s = num.substring(0, num.length() - 1);
        } else if (num.startsWith("0") && num.length() > 1) {
            base = 8;
            s = num.substring(1);
        } else {
            s = num;
        }
        return Long.parseLong(s, base) * multiplier;
    }
}
