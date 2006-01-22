package ie.omk.smpp.util;

public class PropertyNotFoundException extends ie.omk.smpp.SMPPRuntimeException {

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

