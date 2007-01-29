package ie.omk.smpp.message.param;

import ie.omk.smpp.ErrorAddress;
import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;

public class ErrorAddressParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private static final ErrorAddress NULL_ADDRESS = new ErrorAddress();

    public int getLengthSpecifier() {
        return -1;
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((ErrorAddress) obj).getLength();
        } else {
            return NULL_ADDRESS.getLength();
        }
    }

    public void writeObject(Object obj, OutputStream out)
            throws IOException {
        if (obj != null) {
            ((ErrorAddress) obj).writeTo(out);
        } else {
            NULL_ADDRESS.writeTo(out);
        }
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        ErrorAddress address = new ErrorAddress();
        address.readFrom(data, position);
        return address;
    }
}
