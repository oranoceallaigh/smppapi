package ie.omk.smpp.message.param;

import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

/**
 * Mandatory parameter descriptor for an integer.
 * @version $Id:$
 */
public class IntegerParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;

    /**
     * Number of bytes to read for the integer. Default is 1.
     */
    private int length;
    
    public IntegerParamDescriptor(int length) {
        this.length = length;
    }
    
    public int sizeOf(Object obj) {
        return length;
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        long value;
        if (obj != null) {
            value = ((Number) obj).longValue();
        } else {
            value = 0L;
        }
        switch (length) {
        case 8:
            SMPPIO.writeLong(value, out);
            break;
        case 4:
            SMPPIO.writeLongInt(value, out);
            break;
        case 2:
            SMPPIO.writeShort((int) value, out);
            break;
        default:
            SMPPIO.writeByte((int) value, out);
            break;
        }
    }

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        Number value;
        switch (length) {
        case 8:
            value = new Long(SMPPIO.bytesToLong(data, offset));
            break;
        case 4:
            value = new Long(SMPPIO.bytesToLongInt(data, offset));
            break;
        case 2:
            value = new Integer(SMPPIO.bytesToShort(data, offset));
            break;
        default:
            value = new Integer((int) data[offset] & 0xff);
            break;
        }
        body.add(value);
        return length;
    }
}
