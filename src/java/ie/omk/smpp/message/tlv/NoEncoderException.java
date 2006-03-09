package ie.omk.smpp.message.tlv;

/**
 * No encoder found for a Java type. An attempt was made to define a new tag
 * with a value type that the API does not have a known encoder for. The
 * application should define a new encoder and define the tag passing that
 * encoder to the
 * {@link ie.omk.smpp.message.tlv.Tag#defineTag(int, java.lang.Class, ie.omk.smpp.message.tlv.Encoder, int)}
 * method.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class NoEncoderException extends RuntimeException {
    static final long serialVersionUID = 6441311177365899332L;
    
    private final Class type;

    /**
     * Create a new NoEncoderException.
     * 
     * @param type
     *            The Java type that no encoder was found for.
     */
    public NoEncoderException(Class type) {
        this.type = type;
    }

    /**
     * Create a new NoEncoderException.
     * 
     * @param type
     *            The Java type that no encoder was found for.
     * @param msg
     *            The exception message.
     */
    public NoEncoderException(Class type, String msg) {
        super(msg);
        this.type = type;
    }
    
    public Class getType() {
        return type;
    }
}
