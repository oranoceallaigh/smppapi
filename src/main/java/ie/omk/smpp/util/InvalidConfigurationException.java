package ie.omk.smpp.util;

import ie.omk.smpp.SMPPRuntimeException;

/**
 * @version $Id$
 */
public class InvalidConfigurationException extends SMPPRuntimeException {
    static final long serialVersionUID = 2L;
    
    private String invalidValue;


    public InvalidConfigurationException() {
        super();
    }

    public InvalidConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationException(String msg,
            String invalidValue) {
        super(msg);
        this.invalidValue = invalidValue;
    }

    public InvalidConfigurationException(String msg,
            Throwable cause,
            String invalidValue) {
        super(msg, cause);
        this.invalidValue = invalidValue;
    }
    
    /**
     * Get the invalid value which caused this exception.
     * @return The offending value.
     */
    public String getInvalidValue() {
        return invalidValue;
    }
}
