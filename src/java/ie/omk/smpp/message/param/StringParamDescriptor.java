package ie.omk.smpp.message.param;

import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

public class StringParamDescriptor extends AbstractParamDescriptor {
    private static final long serialVersionUID = 1;
    
    public StringParamDescriptor(int linkIndex) {
        super(linkIndex);
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((String) obj).length();
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            SMPPIO.writeString((String) obj, out);
        }
    }

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        int length = getCountFromBody(body);
        body.add(SMPPIO.readString(data, offset, length));
        return length;
    }
}
