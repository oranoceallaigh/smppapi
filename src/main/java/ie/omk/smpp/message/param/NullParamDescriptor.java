package ie.omk.smpp.message.param;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;

import java.io.IOException;

public class NullParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 1;
    
    public int getLengthSpecifier() {
        return -1;
    }

    public int sizeOf(Object obj) {
        return 0;
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
    }

    public Object readObject(PacketDecoder decoder, int length) {
        return null;
    }
}
