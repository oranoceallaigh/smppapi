package ie.omk.smpp.message.tlv;

/**
 * "No value" encoder. This encoder type was necessary as there are some
 * optional parameters that have no value. Therefore, it was possible for the
 * tag/value map in <code>TLVTable</code> to have <code>null</code> values
 * in it. As <code>null</code> is also returned from a map when there is no
 * value for a particular key, some way was needed to distinguish between a
 * parameter not set and a parameter having a null value. Hence the encoder.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public class NullEncoder implements Encoder {

    /**
     * Create a new NullEncoder.
     */
    public NullEncoder() {
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) {
    }

    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
        return null;
    }

    public int getValueLength(Tag tag, Object value) {
        return 0;
    }
}
