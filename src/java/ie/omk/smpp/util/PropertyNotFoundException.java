package ie.omk.smpp.util;

public class PropertyNotFoundException extends ie.omk.smpp.SMPPRuntimeException {
    static final long serialVersionUID = -3513175897407921550L;
    
    private String property = "";

    public PropertyNotFoundException() {
    }

    public PropertyNotFoundException(String property) {
        super();
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}

