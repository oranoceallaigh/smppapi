package com.adenki.smpp.message.param;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

public class BytesParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;
    
    public BytesParamDescriptor() {
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((byte[]) obj).length;
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        if (obj != null) {
            byte[] data = (byte[]) obj;
            encoder.writeBytes(data, 0, data.length);
        }
    }

    public Object readObject(PacketDecoder decoder, int length) throws IOException {
        return decoder.readBytes(length);
    }
}
