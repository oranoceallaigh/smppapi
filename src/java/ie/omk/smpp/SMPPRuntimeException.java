package ie.omk.smpp;

/**
 * SMPPRuntimeException.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 * @version 1.0
 */
public class SMPPRuntimeException extends java.lang.RuntimeException {
    static final long serialVersionUID = 5392381000287167880L;

    public SMPPRuntimeException() {
        super();
    }

    public SMPPRuntimeException(String msg) {
        super(msg);
    }

    public SMPPRuntimeException(Throwable cause) {
        super(cause);
    }

    public SMPPRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

