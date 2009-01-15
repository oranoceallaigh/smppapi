package ie.omk.smpp.util;

import ie.omk.smpp.SMPPRuntimeException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of the {@link APIConfig} which reads its properties
 * from a Java properties file, loaded from a set of known URLs.
 * <p>
 * When initialised, this implementation searches for a file named
 * "smppapi.properties". This file needs to be locatable on the classpath in
 * one of the following locations:<br />
 * <tt>/, /ie, /ie/omk, /ie/omk/smpp</tt> or the default
 * classloader.
 * <p>
 */
public class PropertiesAPIConfig extends Properties implements APIConfig, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(PropertiesAPIConfig.class);

    private static final Map<String, Boolean> BOOLEANS =
        new HashMap<String, Boolean>();
    
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
     * The URL that API properties are loaded from (including path info).
     */
    private URL propsURL;

    static {
        BOOLEANS.put("1", Boolean.TRUE);
        BOOLEANS.put("true", Boolean.TRUE);
        BOOLEANS.put("on", Boolean.TRUE);
        BOOLEANS.put("yes", Boolean.TRUE);
        BOOLEANS.put("0", Boolean.FALSE);
        BOOLEANS.put("false", Boolean.FALSE);
        BOOLEANS.put("off", Boolean.FALSE);
        BOOLEANS.put("no", Boolean.FALSE);
    }
    
    /**
     * Construct a new APIConfig object which reads properties from the
     * default properties resource.
     */
    public PropertiesAPIConfig() {
        this.propsURL = getDefaultPropertiesResource();
    }
    
    /**
     * Construct a new APIConfig object which reads properties from the
     * specified URL.
     * @param propertiesURL The URL to read properties from.
     */
    public PropertiesAPIConfig(URL propertiesURL) {
        this.propsURL = propertiesURL;
    }

    public void initialise() {
        LOG.debug("Initialising API properties.");
        try {
            loadAPIProperties();
        } catch (IOException x) {
            throw new SMPPRuntimeException("Could not load API config", x);
        }
    }
    
    public boolean reloadAPIConfig() {
        LOG.debug("Reloading API config properties.");
        try {
            clear();
            loadAPIProperties();
        } catch (IOException x) {
            LOG.warn("Could not reload API properties.", x);
            return false;
        }
        return true;
    }

    /**
     * Reconfigure this <tt>PropertiesAPIConfig</tt> to load its properties
     * from a new URL, and reload the configuration.
     * @param newURL The new URL to load properties from.
     * @throws IOException If the properties cannot be loaded from the
     * <tt>newURL</tt>. If this exception is thrown, the API configuration
     * will be left in an indeterminate state.
     */
    public void reconfigure(URL newURL) throws IOException {
        propsURL = newURL;
        clear();
        loadAPIProperties();
    }
    
    public String getProperty(String property) throws PropertyNotFoundException {
        String val = super.getProperty(property);
        if (val == null) {
            throw new PropertyNotFoundException(property);
        } else {
            return val;
        }
    }

    public String getProperty(String property, String defaultValue) {
        String val = super.getProperty(property);
        if (val == null) {
            val = defaultValue;
        }
        return val;
    }

    public short getShort(String property, short defaultValue) {
        short s;
        try {
            s = getShort(property);
        } catch (PropertyNotFoundException x) {
            s = defaultValue;
        }
        return s;
    }
    
    public short getShort(String property) {
        int i = getInt(property);
        if (i < Short.MIN_VALUE || i > Short.MAX_VALUE) {
            throw new InvalidConfigurationException(
                    "Property value exceeds valid short range: " + i, property);
        }
        return (short) i;
    }
    
    public int getInt(String property, int defaultValue) {
        int value;
        try {
            value = getInt(property);
        } catch (PropertyNotFoundException x) {
            value = defaultValue;
        }
        return value;
    }

    public int getInt(String property) {
        long l;
        String n = getProperty(property);
        try {
            l = convertToNumber(n);
            if (l < (long) Integer.MIN_VALUE || l > (long) Integer.MAX_VALUE) {
                throw new InvalidConfigurationException(
                        "Property value exceeds valid integer range: " + l,
                        property);
            }
        } catch (NumberFormatException x) {
            throw new InvalidConfigurationException(property, n);
        }
        return (int) l;
    }

    public long getLong(String property, long defaultValue) {
        long l;
        try {
            l = getLong(property);
        } catch (PropertyNotFoundException x) {
            l = defaultValue;
        }
        return l;
    }

    public long getLong(String property) {
        long l;
        String n = getProperty(property);
        try {
            l = convertToNumber(n);
        } catch (NumberFormatException x) {
            throw new InvalidConfigurationException(property, n);
        }
        return l;
    }

    public boolean getBoolean(String property, boolean defaultValue) {
        boolean value;
        try {
            value = getBoolean(property);
        } catch (PropertyNotFoundException x) {
            value = defaultValue;
        }
        return value;
    }

    public boolean getBoolean(String property) {
        String s = getProperty(property).toLowerCase();
        Boolean bool = BOOLEANS.get(s);
        if (bool == null) {
            try {
                if (Integer.parseInt(s) != 0) {
                    bool = Boolean.TRUE;
                } else {
                    bool = Boolean.FALSE;
                }
            } catch (NumberFormatException x) {
                throw new InvalidConfigurationException(property, s);
            }
        }
        return bool.booleanValue();
    }

    /**
     * Try to locate the default smppapi properties resource.
     * @return A URL pointing to the default properties resource, or
     * <code>null</code> if it cannot be found.
     */
    private URL getDefaultPropertiesResource() {
        URL url = null;
        Class<? extends APIConfig> c = getClass();
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loaded API properties from {}", propsURL);
                StringWriter w = new StringWriter();
                list(new PrintWriter(w));
                LOG.debug("\n" + w.toString());
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
        
        char firstChar = num.charAt(0);
        char lastChar = num.charAt(num.length() - 1);
        if (num.startsWith("0x") || num.startsWith("0X")) {
            base = 16;
            s = num.substring(2);
        } else if (lastChar == 'b') {
            base = 2;
            s = num.substring(0, num.length() - 1);
        } else if (lastChar == 'k') {
            multiplier = 1024L;
            s = num.substring(0, num.length() - 1);
        } else if (lastChar == 'm') {
            multiplier = 1048576L;
            s = num.substring(0, num.length() - 1);
        } else if (firstChar == '0' && num.length() > 1) {
            base = 8;
            s = num.substring(1);
        } else {
            s = num;
        }
        return Long.parseLong(s, base) * multiplier;
    }
}
