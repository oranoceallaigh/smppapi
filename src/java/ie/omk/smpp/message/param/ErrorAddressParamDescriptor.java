package ie.omk.smpp.message.param;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

import ie.omk.smpp.ErrorAddress;

public class ErrorAddressParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private static final ErrorAddress NULL_ADDRESS = new ErrorAddress();

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

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        ErrorAddress address = new ErrorAddress();
        address.readFrom(data, offset);
        body.add(address);
        return address.getLength();
    }

}
