package ie.omk.smpp.message.param;

import ie.omk.smpp.Address;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

public class AddressParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private static final Address NULL_ADDRESS = new Address();
    
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

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        Address address = new Address();
        address.readFrom(data, offset);
        body.add(address);
        return address.getLength();
    }
}
