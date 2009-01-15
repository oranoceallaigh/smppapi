package com.adenki.smpp.message.param;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.SMPPDate;
import com.adenki.smpp.util.SMPPDateFormat;

public class DateParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;
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
