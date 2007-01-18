package ie.omk.smpp.message.param;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

/**
 * Mandatory parameter descriptor.
 * @version $Id:$
 */
public interface ParamDescriptor extends Serializable {
    ParamDescriptor INTEGER1 = new IntegerParamDescriptor(1);
    ParamDescriptor INTEGER2 = new IntegerParamDescriptor(2);
    ParamDescriptor INTEGER4 = new IntegerParamDescriptor(4);
    ParamDescriptor INTEGER8 = new IntegerParamDescriptor(8);
    ParamDescriptor CSTRING = new CStringParamDescriptor();
    ParamDescriptor ADDRESS = new AddressParamDescriptor();
    ParamDescriptor ERROR_ADDRESS = new ErrorAddressParamDescriptor();
    ParamDescriptor DATE = new DateParamDescriptor();

    int sizeOf(Object obj);
    
    void writeObject(Object obj, OutputStream out) throws IOException;
    
    int readObject(List body, byte[] data, int offset) throws ParseException;
}
