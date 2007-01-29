package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Mandatory parameter descriptor.
 * @version $Id:$
 */
public interface ParamDescriptor extends Serializable {
    ParamDescriptor INTEGER1 = new IntegerParamDescriptor(1);
    ParamDescriptor INTEGER2 = new IntegerParamDescriptor(2);
    ParamDescriptor INTEGER4 = new IntegerParamDescriptor(4);
    ParamDescriptor INTEGER8 = new IntegerParamDescriptor(8);
    ParamDescriptor BYTES = new BytesParamDescriptor();
    ParamDescriptor CSTRING = new CStringParamDescriptor();
    ParamDescriptor BITMASK = new BitmaskParamDescriptor();
    ParamDescriptor ADDRESS = new AddressParamDescriptor();
    ParamDescriptor ERROR_ADDRESS = new ErrorAddressParamDescriptor();
    ParamDescriptor DATE = new DateParamDescriptor();
    ParamDescriptor NULL = new NullParamDescriptor();

    // TODO docs
    int getLengthSpecifier();
    
    int sizeOf(Object obj);
    
    void writeObject(Object obj, OutputStream out) throws IOException;

    // TODO this should throw something - a runtime exception
    Object readObject(byte[] data, ParsePosition position, int length);
}
