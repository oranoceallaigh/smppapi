package com.adenki.smpp.message.param;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

public class NullParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;
    
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
