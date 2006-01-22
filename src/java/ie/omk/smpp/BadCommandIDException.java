package ie.omk.smpp;

/**
 * BadCommandIDException
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BadCommandIDException extends ie.omk.smpp.SMPPException {
    private int expected = -1;

    private int actual = -1;

    public BadCommandIDException() {
    }

    /**
     * Construct a new BadCommandIdException with specified message.
     */
    public BadCommandIDException(String s) {
        super(s);
    }

    public BadCommandIDException(String msg, int id) {
        super(msg);
        this.actual = id;
    }

    /**
     * Construct a new BadCommandIdException.
     * 
     * @param expected
     *            The expected Command Id value.
     * @param actual
     *            The actual Command Id value received.
     */
    public BadCommandIDException(int expected, int actual) {
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * @return the expected Command id value.
     */
    public int getExpected() {
        return this.expected;
    }

    /**
     * Get the actual Command id value received.
     */
    public int getActual() {
        return this.actual;
    }
}

