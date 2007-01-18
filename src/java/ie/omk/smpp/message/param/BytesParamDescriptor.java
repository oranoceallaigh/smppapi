package ie.omk.smpp.message.param;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

public class BytesParamDescriptor extends AbstractParamDescriptor {
    private static final long serialVersionUID = 1;

    public BytesParamDescriptor(int linkIndex) {
        super(linkIndex);
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((byte[]) obj).length;
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            out.write((byte[]) obj);
        }
    }

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        int count = getCountFromBody(body);
        byte[] array = new byte[count];
        System.arraycopy(data, offset, array, 0, count);
        body.add(array);
        return count;
    }
}
