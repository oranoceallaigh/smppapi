package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

/**
 * Encode and decode bit masks. While this descriptor type supports bit masks
 * that are longer than a single octet, the SMPP specification does not define
 * how a multi-byte bit mask should be encoded on the wire. Since the rest
 * of SMPP is big-endian, this implementation assumes big-endian for bit masks
 * too.
 * @version $Id:$
 */
public class BitmaskParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    
    public BitmaskParamDescriptor() {
    }
    
    public int getLengthSpecifier() {
        return -1;
    }

    public int sizeOf(Object obj) {
        return 1;
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        out.write(bitsetToInt((BitSet) obj));
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        BitSet bitset = new BitSet();
        for (int i = 0; i < 8; i++) {
            if ((data[position.getIndex()] & (byte) (1 << i)) != 0) {
                bitset.set(i);
            }
        }
        position.inc();
        return bitset;
    }
    
    private int bitsetToInt(BitSet bitSet) {
        int value = 0;
        for (int i = bitSet.nextSetBit(0); i >= 0 && i < 8; i = bitSet.nextSetBit(i + 1)) {
            int bit = i % 8;
            value |= 1 << bit;
        }
        return value;
    }
}
