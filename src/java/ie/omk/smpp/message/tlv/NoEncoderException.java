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
    
    public Class type = null;

    /**
     * Create a new NoEncoderException.
     * 
     * @param type
     *            The Java type that no encoder was found for.
     */
    public NoEncoderException(java.lang.Class type) {
    }

    /**
     * Create a new NoEncoderException.
     * 
     * @param type
     *            The Java type that no encoder was found for.
     * @param msg
     *            The exception message.
     */
    public NoEncoderException(java.lang.Class type, String msg) {
        super(msg);
    }
}
