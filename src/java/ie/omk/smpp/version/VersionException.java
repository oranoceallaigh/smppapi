package ie.omk.smpp.version;

/**
 * Exception thrown when there is a problem with SMPP versions.
 * 
 * @since 1.0
 * @author Oran Kelly
 */
public class VersionException extends ie.omk.smpp.SMPPRuntimeException {

    public VersionException() {
    }

    public VersionException(String msg) {
        super(msg);
    }

    public VersionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

