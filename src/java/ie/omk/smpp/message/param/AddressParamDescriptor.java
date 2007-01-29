package ie.omk.smpp.message.param;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;

public class AddressParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private static final Address NULL_ADDRESS = new Address();
    
    public int getLengthSpecifier() {
        return -1;
    }
    
    public int sizeOf(Object address) {
        if (address != null ){
            return ((Address) address).getLength();
        } else {
            return NULL_ADDRESS.getLength();
        }
    }

    public void writeObject(Object address, OutputStream out) throws IOException {
        if (address != null) {
            ((Address) address).writeTo(out);
        } else {
            NULL_ADDRESS.writeTo(out);
        }
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        Address address = new Address();
        address.readFrom(data, position);
        return address;
    }
}
