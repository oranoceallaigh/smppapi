package ie.omk.smpp.message.tlv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

/**
 * Encoder for bit mask value types. This class encodes and decodes
 * {@link java.util.BitSet}objects to and from byte arrays.
 * 
 * @version $Id$
 * @see java.util.BitSet
 */
public class BitmaskEncoder implements Encoder {
	// TODO: this needs a test!
    /**
     * Create a new BitmaskEncoder.
     */
    public BitmaskEncoder() {
    }

    public void writeTo(Tag tag, Object value, byte[] b, int offset) {
        try {
            int[] array = bitsetToArray((BitSet) value, tag.getLength());
            for (int i = 0; i < array.length; i++) {
                b[offset + i] = (byte) array[i];
            }
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type "
                    + "java.util.BitSet");
        }
    }

    public void writeTo(Tag tag, Object value, OutputStream out) throws IOException {
        try {
            int[] array = bitsetToArray((BitSet) value, tag.getLength());
            for (int i = 0; i < array.length; i++) {
                out.write(array[i]);
            }
        } catch (ClassCastException x) {
            throw new BadValueTypeException("Value must be of type "
                    + "java.util.BitSet");
        }
    }
    
    public Object readFrom(Tag tag, byte[] b, int offset, int length) {
        BitSet bs = new BitSet();

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < 8; j++) {
                if ((b[offset + i] & (byte) (1 << j)) != 0) {
                    bs.set((i * 8) + j);
                }
            }
        }

        return bs;
    }

    public int getValueLength(Tag tag, Object value) {
        return tag.getLength();
    }
    
    private int[] bitsetToArray(BitSet bitSet, int length) {
        int[] array = new int[length];
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            int arrayIndex = i / 8;
            int bit = i % 8;
            if (arrayIndex >= length) {
                break;
            }
            array[arrayIndex] |= 1 << bit;
        }
        return array;
    }
}
