package com.adenki.smpp.message.param;

import java.io.IOException;
import java.util.BitSet;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Encode and decode bit masks. While this descriptor type supports bit masks
 * that are longer than a single octet, the SMPP specification does not define
 * how a multi-byte bit mask should be encoded on the wire. Since the rest
 * of SMPP is big-endian, this implementation assumes big-endian for bit masks
 * too.
 * @version $Id$
 */
public class BitmaskParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;
    
    public BitmaskParamDescriptor() {
    }
    
    public int getLengthSpecifier() {
        return -1;
    }

    public int sizeOf(Object obj) {
        return 1;
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        encoder.writeUInt1(bitsetToInt((BitSet) obj));
    }

    public Object readObject(PacketDecoder decoder, int length) {
        int bits = decoder.readUInt1();
        BitSet bitset = new BitSet();
        for (int i = 0; i < 8; i++) {
            if ((bits & (1 << i)) != 0) {
                bitset.set(i);
            }
        }
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
