package ie.omk.smpp;

import ie.omk.smpp.util.BinaryEncoding;

/**
 * @version $Id:$
 */
public class BinaryMessage extends Message {
    private static final long serialVersionUID = 1;
    
    private byte[] data = new byte[0];
    
    public BinaryMessage() {
        super(new BinaryEncoding());
    }
    
    @Override
    public byte[] getMessage() {
        return data;
    }

    public void setMessage(byte[] data) {
        this.data = data;
    }

}
