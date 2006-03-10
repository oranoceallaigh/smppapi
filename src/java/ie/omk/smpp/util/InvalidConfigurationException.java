package ie.omk.smpp.util;

public class InvalidConfigurationException extends
        ie.omk.smpp.SMPPRuntimeException {
    static final long serialVersionUID = 5616081756943055309L;
    
    private final String property;
    private final String value;

    public InvalidConfigurationException(String msg, String property) {
        super(msg);
        this.property = property;
        this.value = null;
    }
    public InvalidConfigurationException(String msg, String property, String value) {
        super(msg);
        this.property = property;
        this.value = value;
    }
    public InvalidConfigurationException(
            String msg, String property, Throwable rootCause) {
        super(msg, rootCause);
        this.property = property;
        this.value = null;
    }

    /**
     * Get the name of the offending property.
     */
    public String getProperty() {
        return property;
    }
    
    /**
     * Get the value which caused this exception.
     * @return The offending value.
     */
    public String getValue() {
        return value;
    }
}

