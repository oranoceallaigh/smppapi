package ie.omk.smpp.message.param;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;

import java.io.IOException;

public class StringParamDescriptor extends AbstractDescriptor {
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

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        if (obj != null) {
            String s = obj.toString();
            encoder.writeString(s, s.length());
        }
    }

    public Object readObject(PacketDecoder decoder, int length) {
        return decoder.readString(length);
    }
}
