package ie.omk.smpp;

public class UnsupportedOperationException extends SMPPRuntimeException {
    static final long serialVersionUID = 2200729955220317767L;
    
    public UnsupportedOperationException() {
    }

    public UnsupportedOperationException(String msg) {
        super(msg);
    }
}

