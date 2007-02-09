package ie.omk.smpp.version;

import ie.omk.smpp.SMPPRuntimeException;

/**
 * Exception thrown when there is a versioning problem. This can be thrown
 * when an SMPP version is not recognized, or when a packet is
 * invalidate in the context of a particular version.
 * @version $Id$
 */
public class VersionException extends SMPPRuntimeException {
    static final long serialVersionUID = -6347880117047656707L;

    public VersionException() {
        super();
    }

    public VersionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public VersionException(String msg) {
        super(msg);
    }

    public VersionException(Throwable cause) {
        super(cause);
    }
}
