package ie.omk.smpp.message.param;

import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

public class CStringParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((String) obj).length() + 1;
        } else {
            return 1;
        }
    }

    public void writeObject(Object str, OutputStream out) throws IOException {
        SMPPIO.writeCString((String) str, out);
    }

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        String value = SMPPIO.readCString(data, offset);
        body.add(value);
        return sizeOf(value);
    }
}
