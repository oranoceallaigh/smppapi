package ie.omk.smpp.util;

/**
 * Exception thrown when an attempt to parse a String as an SMPPDate fails due
 * to a bad format.
 * 
 * @author Oran Kelly
 * @since 1.0
 */
public class InvalidDateFormatException extends ie.omk.smpp.SMPPException {
    static final long serialVersionUID = -5254415730594384146L;
    
    private String dateString = "";

    public InvalidDateFormatException() {
    }

    public InvalidDateFormatException(String msg, String dateString) {
        super(msg);
        this.dateString = dateString;
    }

    /**
     * Get the date string that caused this exception.
     */
    public String getDateString() {
        return dateString;
    }
}

