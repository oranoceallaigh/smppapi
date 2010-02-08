package com.adenki.smpp.gsm.ems;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.adenki.smpp.gsm.AbstractHeaderElement;

/**
 * Header element that represents one or more compressed extended
 * objects.
 * @version $Id$
 */
public class CompressedData extends AbstractHeaderElement {

    private List<ExtendedObject> objects = new ArrayList<ExtendedObject>();
    private byte[] compressedData;
    private int ptr;

    public void add(ExtendedObject object) {
        objects.add(object);
    }

    public byte[] compress() {
        int length = 0;
        for (ExtendedObject obj : objects) {
            length += obj.getLength();
        }
        length += objects.size();
        // Create the uncompressed buffer.
        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (ExtendedObject obj : objects) {
            buffer.put((byte) 0x14);
            buffer.put(obj.getData());
        }
        byte[] uncompressed = buffer.array();
        ByteBuffer compressed = ByteBuffer.allocate(length);
        ByteBuffer literal = ByteBuffer.allocate(length);
        literal.put(uncompressed, 0, 2);
        int readPos = 2;
        while (readPos < length) {
            boolean sliceFound = false;
            // Can't find a slice size larger than the number of bytes we
            // have available behind or in front of the current read position.
            int sliceSize =
                Math.min(Math.min(63, readPos), uncompressed.length - readPos);
            int slicePos = -1;
            for (; sliceSize > 2; sliceSize--) {
                slicePos = findSlice(uncompressed, readPos, sliceSize);
                if (slicePos >= 0) {
                    sliceFound = true;
                    break;
                }
            }
            if (!sliceFound) {
                // Add the current byte to the literal buffer
                literal.put((byte) uncompressed[readPos]);
                readPos++;
            } else {
                // Output the current literal buffer.
                outputLiterals(compressed, literal);
                // Output a slice descriptor
                slicePos = readPos - slicePos;
                int descriptor = ((sliceSize & 0x2f) << 9) | (slicePos & 0x1ff);
                compressed.putShort((short) descriptor);
                readPos += sliceSize;
            }
        }
        compressed.flip();
        compressedData = new byte[compressed.remaining()];
        compressed.get(compressedData, 0, compressed.remaining());
        return compressedData;
    }
    
    @Override
    public void reset() {
        super.reset();
        objects.clear();
        compressedData = null;
        ptr = 0;
    }
    
    public int getLength() {
        if (compressedData == null) {
            throw new IllegalStateException("Must compress the data first.");
        }
        return compressedData.length + 3;
    }

    @Override
    public boolean isComplete() {
        if (compressedData == null) {
            throw new IllegalStateException("Must compress the data first.");
        }
        return ptr == compressedData.length;
    }
    
    @Override
    public boolean write(int segmentNum, ByteBuffer buffer) {
        if (compressedData == null) {
            throw new IllegalStateException("Must compress the data first.");
        }
        boolean writeHeader = ptr == 0;
        if (writeHeader && buffer.remaining() < 5) {
            return false;
        } else if (!writeHeader && buffer.remaining() < 3) {
            return false;
        }
        int headerSize;
        int dataSize;
        if (writeHeader) {
            headerSize = 3;
            dataSize = Math.min(buffer.remaining() - 5, compressedData.length - ptr);
        } else {
            headerSize = 0;
            dataSize = Math.min(buffer.remaining() - 2, compressedData.length - ptr);
        }
        buffer.put((byte) 0x16);
        buffer.put((byte) (headerSize + dataSize));
        if (writeHeader) {
            buffer.put((byte) 0);
            buffer.putShort((short) compressedData.length);
        }
        buffer.put(compressedData, ptr, dataSize);
        ptr += dataSize;
        return true;
    }
    
    private int findSlice(byte[] data, int readPos, int sliceSize) {
        int pos = readPos - sliceSize;
        int endPos = Math.max(readPos - 511, 0);
        for (; pos >= endPos; pos--) {
            boolean matched = true;
            for (int i = 0; i < sliceSize; i++) {
                if (data[readPos + i] != data[pos + i]) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return pos;
            }
        }
        return -1;
    }
    
    private void outputLiterals(ByteBuffer compressed, ByteBuffer literal) {
        literal.flip();
        while (literal.remaining() > 0) {
            int size = Math.min(127, literal.remaining());
            compressed.put((byte) (0x80 | size));
            for (int i = 0; i < size; i++) {
                compressed.put(literal.get());
            }
        }
        literal.clear();
    }
}
