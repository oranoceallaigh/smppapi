package ie.omk.smpp.message.param;

import ie.omk.smpp.util.PacketDecoder;
import ie.omk.smpp.util.PacketEncoder;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPDateFormat;

import java.io.IOException;

public class DateParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 1;
    private static final SMPPDateFormat DATE_FORMAT = new SMPPDateFormat();
    
    public int getLengthSpecifier() {
        return -1;
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            String str = DATE_FORMAT.format((SMPPDate) obj);
            return str.length() + 1;
        } else {
            return 1;
        }
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        encoder.writeDate((SMPPDate) obj);
    }

    public Object readObject(PacketDecoder decoder, int length) {
        return decoder.readDate();
    }
}
