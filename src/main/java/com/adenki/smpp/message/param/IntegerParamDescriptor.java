package com.adenki.smpp.message.param;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Mandatory parameter descriptor for an integer.
 * @version $Id$
 */
public class IntegerParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;

    /**
     * Number of bytes to read for the integer. Default is 1.
     */
    private int length;
    
    public IntegerParamDescriptor(int length) {
        this.length = length;
    }
    
    public int getLengthSpecifier() {
        return -1;
    }
    
    public int sizeOf(Object obj) {
        return length;
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        if (!(obj instanceof Number)) {
            throw new IllegalArgumentException("Invalid object type.");
        }
        long value = ((Number) obj).longValue();
        switch (length) {
        case 8:
            encoder.writeInt8(value);
            break;
        case 4:
            encoder.writeUInt4(value);
            break;
        case 2:
            encoder.writeUInt2((int) value);
            break;
        default:
            encoder.writeUInt1((int) value);
            break;
        }
    }

    public Object readObject(PacketDecoder decoder, int length) {
        Number value;
        switch (this.length) {
        case 8:
            value = new Long(decoder.readInt8());
            break;
        case 4:
            value = new Long(decoder.readUInt4());
            break;
        case 2:
            value = new Integer(decoder.readUInt2());
            break;
        default:
            value = new Integer(decoder.readUInt1());
            break;
        }
        return value;
    }
}
