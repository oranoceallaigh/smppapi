package ie.omk.smpp.message.param;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;

import java.io.IOException;

public class CStringParamDescriptor extends AbstractDescriptor {
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

    public void writeObject(Object str, PacketEncoder encoder) throws IOException {
        if (str != null) {
            encoder.writeCString(str.toString());
        } else {
            encoder.writeCString("");
        }
    }

    public Object readObject(PacketDecoder decoder, int length) {
        return decoder.readCString();
    }
}
