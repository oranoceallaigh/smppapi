package com.adenki.smpp.message.param;

import java.io.IOException;
import java.util.BitSet;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Encode and decode bit masks.
 * <p>
 * The SMPP specification only defines single-octet bit masks. However,
 * this descriptor supports <strong>reading</strong> bit masks that are
 * longer than a single octet. It assumes big-endian ordering of the octets.
 * </p>
 * <p>
 * Writing bit masks that contain more than one octet is not supported.
 * </p>
 * @version $Id$
 */
public class BitmaskParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;
    
    public BitmaskParamDescriptor() {
    }
    
    public int sizeOf(Object obj) {
        return 1;
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        encoder.writeUInt1(bitsetToInt((BitSet) obj));
    }

    public Object readObject(PacketDecoder decoder, int length) throws IOException {
        BitSet bitset = new BitSet();
        for (int i = 0; i < length; i++) {
            final int lowBit = ((length - 1) - i) * 8;
            final int currentBits = decoder.readUInt1();
            for (int j = 0; j < 8; j++) {
                if ((currentBits & (1 << j)) != 0) {
                    bitset.set(lowBit + j);
                }
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
