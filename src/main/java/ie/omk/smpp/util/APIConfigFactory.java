package ie.omk.smpp.util;

/**
 * Factory class for obtaining the API configuration. Alternative API
 * configurations can be specified by setting the
 * <tt>ie.omk.smpp.configClass</tt> system property to the fully qualified
 * class name of a class that implements the {@link APIConfig} interface.
 * <p>
 * For example, start your application with:<br />
 * <tt>java -Die.omk.smpp.configClass=app.MyAPIConfig app.MainClass</tt>
 * </p>
 * <p>
 * Normally, the {@link #getConfig()} method will cache the loaded
 * configuration object on first call and return the cached config
 * on subsequent calls. If you want to override this behaviour and
 * never cache the loaded configuration object, set the
 * <tt>ie.omk.smpp.cacheConfig</tt> system property to <tt>false</tt>.
 * </p>
 * @version $Id:$
 */
public final class APIConfigFactory {
    /**
     * The system property to set to control whether a loaded
     * configuration instance is cached as a singleton or not.
     */
    public static final String CONFIG_CLASS_PROP = "ie.omk.smpp.configClass";
    
    /**
     * The system property specifying the concrete implementation of
     * {@link APIConfig} to read configuration properties from.
     */
    public static final String CACHE_CONFIG_PROP = "ie.omk.smpp.cacheConfig";
    
    private static boolean cacheConfig = true;
    private static APIConfig cachedConfig;
    
    private APIConfigFactory() {
    }
    
    public static final APIConfig getConfig() {
        initCacheConfig();
        if (cachedConfig != null) {
            return cachedConfig;
        } else {
            APIConfig config = loadConfig();
            if (cacheConfig) {
                cachedConfig = config;
            }
            return config;
        }
    }
    
    /**
     * Load and initialise an {@link APIConfig} instance. The type of
     * implementation to instantiate is read from the system property as
     * described in the {@link APIConfigFactory class description}.
     * @return An initialised <tt>APIConfig</tt> instance.
     * @throws InvalidConfigurationException If the configuration class
     * cannot be found, it does not implement {@link APIConfig} or its
     * constructor is not visible or throws an exception.
     */
    public static final APIConfig loadConfig() {
        String className = null;
        try {
            ClassLoader loader = getClassLoader();
            className = getConfigClassName();
            Class<?> clazz = loader.loadClass(className);
            if (!APIConfig.class.isAssignableFrom(clazz)) {
                throw new InvalidConfigurationException(
                        "Class "
                        + className
                        + " does not implement "
                        + APIConfig.class.getName());
            }
            APIConfig config = (APIConfig) clazz.newInstance();
            config.initialise();
            return config;
        } catch (ClassNotFoundException x) {
            throw new InvalidConfigurationException(
                    "Cannot find class " + className);
        } catch (InstantiationException x) {
            throw new InvalidConfigurationException(
                    "Constructor in class "
                    + className
                    + " threw an exception", x);
        } catch (IllegalAccessException x) {
            throw new InvalidConfigurationException(
                    "Constructor in class "
                    + className
                    + " is not visible");
        }
    }
    
    /**
     * Set the cached {@link APIConfig} instance that will be returned
     * from calls to {@link #getConfig()}. This method allows
     * applications to set their own <tt>APIConfig</tt> implementation,
     * bypassing the {@link #loadConfig()} logic.
     * @param apiConfig The <tt>APIConfig</tt> instance to cache and 
     * return from subsequent calls to <tt>getConfig()</tt>.
     */
    public static final void setCachedConfig(APIConfig apiConfig) {
        cachedConfig = apiConfig;
    }

    /**
     * Reset this <tt>APIConfigFactory</tt>. This clears both the
     * cached configuration object and resets config-caching setting to
     * <tt>true</tt> so that the next call to {@link #getConfig()} will
     * cache its loaded configuration.
     */
    public static final void reset() {
        cacheConfig = true;
        cachedConfig = null;
    }
    
    private static void initCacheConfig() {
        String value = System.getProperty(CACHE_CONFIG_PROP);
        if (value != null) {
            value = value.trim().toLowerCase();
            if ("false".equals(value)
                    || "no".equals(value)
                    || "off".equals(value)) {
                cacheConfig = false;
            } else {
                cacheConfig = true;
            }
        }
    }
    
    private static ClassLoader getClassLoader() {
        Thread currentThread = Thread.currentThread();
        ClassLoader loader = currentThread.getContextClassLoader();
        if (loader == null) {
            loader = APIConfigFactory.class.getClassLoader();
        }
        return loader;
    }
    
    private static String getConfigClassName() {
        String value = System.getProperty(CONFIG_CLASS_PROP);
        if (value != null) {
            return value;
        } else {
            return PropertiesAPIConfig.class.getName();
        }
    }
}
