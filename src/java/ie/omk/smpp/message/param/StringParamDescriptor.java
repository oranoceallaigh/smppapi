package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;

public class StringParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private int linkIndex;
    
    public StringParamDescriptor(int linkIndex) {
        this.linkIndex = linkIndex;
    }
    
    public int getLengthSpecifier() {
        return linkIndex;
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((String) obj).length();
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            SMPPIO.writeString((String) obj, out);
        }
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        String str = SMPPIO.readString(data, position.getIndex(), length);
        position.inc(length);
        return str;
    }
}
