package ie.omk.smpp.event;

public class QueueFullException extends RuntimeException {
    static final long serialVersionUID = 1L;

    public QueueFullException() {
        super();
    }

    public QueueFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueFullException(String message) {
        super(message);
    }

    public QueueFullException(Throwable cause) {
        super(cause);
    }
}
