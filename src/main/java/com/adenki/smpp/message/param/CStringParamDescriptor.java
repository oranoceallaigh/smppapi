package com.adenki.smpp.message.param;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

public class CStringParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;

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
