package ie.omk.smpp.util;

/**
 * An object that implements {@link APIConfig} but does nothing.
 * @version $Id:$
 */
public class NullAPIConfig implements APIConfig {

    public boolean getBoolean(String property, boolean defaultValue)
            throws InvalidConfigurationException {
        return false;
    }

    public boolean getBoolean(String property)
            throws InvalidConfigurationException, PropertyNotFoundException {
        return false;
    }

    public int getInt(String property, int defaultValue)
            throws InvalidConfigurationException {
        return 0;
    }

    public int getInt(String property) throws InvalidConfigurationException,
            PropertyNotFoundException {
        return 0;
    }

    public long getLong(String property, long defaultValue)
            throws InvalidConfigurationException {
        return 0;
    }

    public long getLong(String property) throws InvalidConfigurationException,
            PropertyNotFoundException {
        return 0;
    }

    public String getProperty(String property) throws PropertyNotFoundException {
        return null;
    }

    public String getProperty(String property, String defaultValue) {
        return null;
    }

    public short getShort(String property, short defaultValue)
            throws InvalidConfigurationException {
        return 0;
    }

    public short getShort(String property)
            throws InvalidConfigurationException, PropertyNotFoundException {
        return 0;
    }

    public void initialise() {

    }

    public boolean reloadAPIConfig() {
        return false;
    }
}
