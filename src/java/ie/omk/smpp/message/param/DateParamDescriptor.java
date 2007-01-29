package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPDateFormat;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

public class DateParamDescriptor implements ParamDescriptor {
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

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            String str = DATE_FORMAT.format((SMPPDate) obj);
            SMPPIO.writeCString(str, out);
        } else {
            out.write((byte) 0);
        }
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        SMPPDate date = null;
        String str = null;
        try {
            str = SMPPIO.readCString(data, position.getIndex());
            if (str.length() > 0) {
                date = (SMPPDate) DATE_FORMAT.parseObject(str);
                position.inc(str.length() + 1);
            } else {
                position.inc();
            }
        } catch (ParseException x) {
            // TODO
            throw new RuntimeException(
                    "Could not parse the date: \"" + str + "\"", x);
        }
        return date;
    }
}
