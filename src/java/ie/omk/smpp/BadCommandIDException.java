package ie.omk.smpp;

/**
 * BadCommandIDException
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BadCommandIDException extends ie.omk.smpp.SMPPException {
    static final long serialVersionUID = -3690049503805565620L;
    
    // TODO: expected versus actual doesn't really make sense. Change
    // this exception.
    private final int expected;
    private final int actual;

    public BadCommandIDException() {
        expected = -1;
        actual = -1;
    }

    /**
     * Construct a new BadCommandIdException with specified message.
     */
    public BadCommandIDException(String s) {
        super(s);
        expected = -1;
        actual = -1;
    }

    public BadCommandIDException(String msg, int id) {
        super(msg);
        actual = id;
        expected = -1;
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
        return expected;
    }

    /**
     * Get the actual Command id value received.
     */
    public int getActual() {
        return actual;
    }
}
