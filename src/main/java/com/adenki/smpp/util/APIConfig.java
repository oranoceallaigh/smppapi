package com.adenki.smpp.util;


/**
 * Interface for internal API configuration implementations.
 * Implementation of this class hold the configuration for
 * the smppapi. The API configuration is loaded by the
 * {@link APIConfigFactory} class.
 * <p>
 * Implementations <strong>must</strong> supply a no-argument constructor
 * so that <tt>APIConfigFactory</tt> can instantiate it.
 * </p>
 * <p>
 * Most applications can probably accept the default settings of the API. If,
 * however, you're trying to eke maximum performance out of your application,
 * tweaking these settings may help.
 * </p>
 * <p>
 * Supported API properties are:
 * </p>
 * <table cols="3" border="1" width="100%">
 * <tr>
 * <th width="25%">Property name</th>
 * <th width="25%">Type</th>
 * <th width="50%">Description</th>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.default_version</code></td>
 * <td>String</td>
 * <td>Set the default version that will be used for new Connections</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.default_alphabet</code></td>
 * <td>String</td>
 * <td>The class name of the default alphabet encoding to use. Must be
 * an implementation of <tt>com.adenki.smpp.util.AlphabetEncoding</tt></td>
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
 * <td>Sets the size of the buffer, in bytes, used on the incoming
 * stream connection from the SMSC.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.buffersize_out</code></td>
 * <td>Integer</td>
 * <td>Sets the size of the buffer, in bytes, used on the outgoing stream
 * connection to the SMSC.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.net.autoflush</code></td>
 * <td>Boolean</td>
 * <td>By default, the {@link com.adenki.smpp.net.SmscLink} class automatically
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
 * <td>The name of a class, which implements
 * {@link com.adenki.smpp.event.EventDispatcher}, which will be used as the default
 * event dispatcher for <code>Connection</code> objects.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.event.threaded_dispatcher.pool_size</code></td>
 * <td>Integer</td>
 * <td>The size of the thread pool used by the
 * {@link com.adenki.smpp.event.TaskExecutorEventDispatcher} class.</td>
 * </tr>
 * 
 * <tr>
 * <td><code>smppapi.message.segment_size</code></td>
 * <td>Integer</td>
 * <td>The default segment size to use for concatenated short messages
 * using optional parameters.</td>
 * </tr>
 * </table>
 * @version $Id$
 * @see APIConfigFactory
 * @see PropertiesAPIConfig
 */
public interface APIConfig {
    /**
     * @see APIConfig
     */
    String DEFAULT_VERSION = "smppapi.default_version";
    
    /**
     * @see APIConfig
     */
    String DEFAULT_ALPHABET = "smppapi.default_alphabet";
    
    /**
     * @see APIConfig
     */
    String LAX_VERSIONS = "smppapi.lax_versions";

    /**
     * @see APIConfig
     */
    String LINK_BUFFERSIZE_IN = "smppapi.net.buffersize_in";

    /**
     * @see APIConfig
     */
    String LINK_BUFFERSIZE_OUT = "smppapi.net.buffersize_out";

    /**
     * @see APIConfig
     */
    String LINK_AUTO_FLUSH = "smppapi.net.autoflush";

    /**
     * @see APIConfig
     */
    String LINK_AUTOCLOSE_SNOOP = "smppapi.net.autoclose_snoop";

    /**
     * @see APIConfig
     */
    String LINK_TIMEOUT = "smppapi.net.link_timeout";

    /**
     * @see APIConfig
     */
    String TOO_MANY_IO_EXCEPTIONS = "smppapi.connection.rcv_daemon.ioex_count";

    /**
     * @see APIConfig
     */
    String EVENT_DISPATCHER_CLASS = "smppapi.event.dispatcher";

    /**
     * @see APIConfig
     */
    String EVENT_THREAD_POOL_SIZE =
        "smppapi.event.threaded_dispatcher.pool_size";

    /**
     * @see APIConfig
     */
    String BIND_TIMEOUT ="smppapi.connection.bind_timeout";

    /**
     * @see APIConfig
     */
    String SEGMENT_SIZE = "smppapi.message.segment_size";
    
    /**
     * Initialise this properties instance. The {@link APIConfigFactory}
     * will call this method once after it has instantiated the
     * configuration implementation so that any implementation-specific
     * actions can be carried out.
     */
    void initialise();
    
    /**
     * Cause the API properties to be reloaded. The properties will be re-read
     * from the same location as they were initially loaded from. If the
     * resource has disappeared or is no longer accessible, the properties will
     * not be loaded and <code>false</code> will be returned to the caller.
     * @return <tt>true</tt> if the properties were successfully reloaded,
     * <tt>false</tt> otherwise.
     */
    boolean reloadAPIConfig();

    /**
     * Get the value for a property.
     * @param property The name of the property to retrieve.
     * @return The value for <code>property</code>.
     * @throws PropertyNotFoundException if <code>property</code> is
     * not found in the configuration.
     */
    String getProperty(String property) throws PropertyNotFoundException;

    /**
     * Get the value for a property or return a default value if it is not set.
     * @param property The name of the property to retrieve.
     * @param defaultValue The value to return if <code>property</code> is not
     * set.
     * @return The value for <code>property</code>.
     */
    String getProperty(String property, String defaultValue);

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
    short getShort(String property, short defaultValue) throws InvalidConfigurationException;
    
    /**
     * Get the value for a property, parsed as a Java <code>short</code>.
     * @param property the name of the property to retrive.
     * @return The value of <tt>property</tt>.
     * @throws PropertyNotFoundException if <code>property</code> is not
     * found in the configuration.
     * @throws InvalidConfigurationException if the value is not a valid short.
     */
    short getShort(String property) throws InvalidConfigurationException, PropertyNotFoundException;
    
    /**
     * Get the value for a property, parsed as a Java <code>int</code>. If
     * the property is not found, the default value is returned.
     * @param property the name of the property to retrive.
     * @param defaultValue the value to return if the property does not exist.
     * @return The value of <tt>property</tt>.
     * @throws InvalidConfigurationException if the value is not a valid
     * integer.
     */
    int getInt(String property, int defaultValue) throws InvalidConfigurationException;

    /**
     * Get the value for a property, parsed as a Java <code>int</code>.
     * @param property the name of the property to retrive.
     * @return The value of <tt>property</tt>.
     * @throws PropertyNotFoundException if
     * <code>property</code> is not found in the configuration.
     * @throws InvalidConfigurationException if the value is not a valid
     * integer.
     */
    int getInt(String property) throws InvalidConfigurationException, PropertyNotFoundException;

    /**
     * Get the value for a property, parsed as a Java <code>long</code>. If
     * the property is not found, the default value is returned.
     * @param property the name of the property to retrieve.
     * @param defaultValue the value to return if the property does not exist.
     * @return The value of <tt>property</tt>.
     * @throws InvalidConfigurationException if the value is not a valid long.
     */
    long getLong(String property, long defaultValue) throws InvalidConfigurationException;

    /**
     * Get the value for a property, parsed as a Java <code>long</code>.
     * @param property the name of the property to retrive.
     * @return The value of <tt>property</tt>.
     * @throws PropertyNotFoundException if <code>property</code> is not
     * found in the configuration.
     * @throws InvalidConfigurationException if the value is not a valid long.
     */
    long getLong(String property) throws InvalidConfigurationException, PropertyNotFoundException;

    /**
     * Get a property as a boolean value. Any of 'on', 'yes' or 'true'
     * (irrelevant of case) will evaluate to <code>true</code>. Any of 'off',
     * 'no' or 'false' will evaluate to <code>false</code>. Boolean
     * parameters may also be specified as a number, where zero will equate to
     * <code>false</code> while non-zero will equate to <code>true</code>.
     * All other words will result in an InvalidConfigurationException being
     * thrown.
     * @param property the name of the property to look up.
     * @param defaultValue the value to return if the property does not exist.
     * @return The value of <tt>property</tt>.
     * @throws InvalidConfigurationException if the property has a
     * value that cannot be parsed or interpreted as boolean.
     */
    boolean getBoolean(String property, boolean defaultValue) throws InvalidConfigurationException;

    /**
     * Get a property as a boolean value. See the description of
     * {@link #getBoolean(String, boolean)} for details of how a boolean
     * value can be specified.
     * @param property The name of the property to retrieve.
     * @return The value of <tt>property</tt>.
     * @throws PropertyNotFoundException if <code>property</code> is not
     * found in the configuration.
     * @throws InvalidConfigurationException if the property has a value
     * that cannot be parsed or interpreted as boolean.
     */
    boolean getBoolean(String property) throws InvalidConfigurationException, PropertyNotFoundException;

    /**
     * Instantiate a new instance of a class whose class name is specified
     * in <tt>property</tt>.
     * @param <T> The expected type of the instantiated class.
     * @param property The name of a property whose value is the fully
     * qualified name of a class to instantiate.
     * @param type The expected type of the instantiated class. This may
     * specify a super-class or interface of the actually instantiated
     * class.
     * @return The new object instance.
     * @throws PropertyNotFoundException If <tt>property</tt> is not
     * found in the configuration.
     */
    <T> T getClassInstance(String property, Class<T> type);
    
    /**
     * Instantiate a new instance of a class whose class name is specified
     * in <tt>property</tt>, returning a default value if the property
     * is not set.
     * @param <T> The expected type of the instantiated class.
     * @param property The name of a property whose value is the fully
     * qualified name of a class to instantiate.
     * @param type The expected type of the instantiated class. This may
     * specify a super-class or interface of the actually instantiated
     * class.
     * @return The new object instance, or <tt>defaultValue</tt> if
     * <tt>property</tt> is not set.
     */
    <T> T getClassInstance(String property, Class<T> type, T defaultValue);
    
    /**
     * Determine if a property is set in the configuration.
     * @param property The name of the property to test.
     * @return <tt>true</tt> if the property is set, <tt>false</tt> if not.
     */
    boolean isSet(String property);
}
