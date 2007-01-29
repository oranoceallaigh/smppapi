package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class CStringParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;

    public int getLengthSpecifier() {
        return -1;
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((String) obj).length() + 1;
        } else {
            return 1;
        }
    }

    public void writeObject(Object str, OutputStream out) throws IOException {
        SMPPIO.writeCString((String) str, out);
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        String s;
        int index = position.getIndex();
        try {
            if (length > -1) {
                s = new String(data, index, length - 1, "US-ASCII");
                position.inc(length);
            } else {
                s = SMPPIO.readCString(data, index);
                position.inc(s.length() + 1);
            }
        } catch (UnsupportedEncodingException x) {
            throw new RuntimeException("ASCII not supported.", x);
        }
        return s;
    }
}
