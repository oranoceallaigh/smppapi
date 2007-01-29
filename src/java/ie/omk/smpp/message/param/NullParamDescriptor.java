package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;

public class NullParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    
    public int getLengthSpecifier() {
        return -1;
    }

    public int sizeOf(Object obj) {
        return 0;
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        return null;
    }
}
