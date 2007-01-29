package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;

public class BytesParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    
    private int linkIndex;
    
    public BytesParamDescriptor() {
        linkIndex = -1;
    }
    
    public BytesParamDescriptor(int linkIndex) {
        this.linkIndex = linkIndex;
    }
    
    public int getLengthSpecifier() {
        return linkIndex;
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((byte[]) obj).length;
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            out.write((byte[]) obj);
        }
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        byte[] array = new byte[length];
        System.arraycopy(data, position.getIndex(), array, 0, length);
        position.inc(length);
        return array;
    }
}
