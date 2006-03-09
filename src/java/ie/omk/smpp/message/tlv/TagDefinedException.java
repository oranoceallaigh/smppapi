package ie.omk.smpp.message.tlv;

/**
 * Tag has already been defined. This exception is thrown by
 * {@link Tag#defineTag}if an attempt is made to redefine a tag which already
 * has a definition.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class TagDefinedException extends java.lang.RuntimeException {
    static final long serialVersionUID = -2723406909679964988L;
    
    /**
     * The value of the tag that was attempted to be redefined.
     */
    private int tagValue = -1;

    /**
     * Create a new TagDefinedException.
     * 
     * @param tagValue
     *            The tag for which an attempt as made to redefine.
     */
    public TagDefinedException(int tagValue) {
        this.tagValue = tagValue;
    }

    /**
     * Create a new TagDefinedException.
     * 
     * @param tagValue
     *            The tag for which an attempt as made to redefine.
     * @param msg
     *            The exception message.
     */
    public TagDefinedException(int tagValue, String msg) {
        super(msg);
        this.tagValue = tagValue;
    }

    /**
     * Get the tag for which an attempt as made to redefine.
     * 
     * @return The tag for which an attempt as made to redefine.
     */
    public int getTagValue() {
        return tagValue;
    }
}

