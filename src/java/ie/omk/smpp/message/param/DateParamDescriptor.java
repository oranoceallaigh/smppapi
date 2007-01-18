package ie.omk.smpp.message.param;

import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPDateFormat;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

public class DateParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private static final SMPPDateFormat DATE_FORMAT = new SMPPDateFormat();
    
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

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        String str = SMPPIO.readCString(data, offset);
        if (str.length() > 0) {
            body.add(DATE_FORMAT.parseObject(str));
        } else {
            body.add(null);
        }
        return str.length() + 1;
    }
}
