package ie.omk.smpp.util;

public class InvalidConfigurationException extends
        ie.omk.smpp.SMPPRuntimeException {
    static final long serialVersionUID = 5616081756943055309L;
    
    private String property = "";

    public InvalidConfigurationException() {
    }

    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    public InvalidConfigurationException(String msg, String property) {
        super(msg);
        this.property = property;
    }

    /**
     * Get the name of the offending property.
     */
    public String getProperty() {
        return property;
    }
}

