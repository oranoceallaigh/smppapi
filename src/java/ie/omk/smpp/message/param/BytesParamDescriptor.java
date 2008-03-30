package ie.omk.smpp.message.param;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;

import java.io.IOException;

public class BytesParamDescriptor extends AbstractDescriptor {
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

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        if (obj != null) {
            byte[] data = (byte[]) obj;
            encoder.writeBytes(data, 0, data.length);
        }
    }

    public Object readObject(PacketDecoder decoder, int length) {
        return decoder.readBytes(length);
    }
}
