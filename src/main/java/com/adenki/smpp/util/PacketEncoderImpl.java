package com.adenki.smpp.util;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementation of {@link PacketEncoder} which uses NIO to write
 * to {@link ByteBuffer ByteBuffers}.
 * @version $Id$
 */
public class PacketEncoderImpl extends AbstractPacketEncoder {
    private ByteBuffer buffer;
    
    public PacketEncoderImpl(ByteBuffer buffer) {
        this.buffer = buffer;
    }
    
    public PacketEncoder writeUInt1(int value) throws IOException {
        buffer.put((byte) value);
        return this;
    }

    public PacketEncoder writeUInt2(int value) throws IOException {
        buffer.putShort((short) value);
        return this;
    }

    public PacketEncoder writeUInt4(long value) throws IOException {
        buffer.putInt((int) value);
        return this;
    }

    public PacketEncoder writeInt4(int value) throws IOException {
        buffer.putInt(value);
        return this;
    }

    public PacketEncoder writeInt8(long value) throws IOException {
        buffer.putLong(value);
        return this;
    }

    public PacketEncoder writeBytes(byte[] bytes) throws IOException {
        if (bytes != null) {
            writeBytes(bytes, 0, bytes.length);
        }
        return this;
    }

    public PacketEncoder writeBytes(byte[] bytes, int offset, int length) throws IOException {
        if (bytes != null) {
            buffer.put(bytes, offset, length);
        } else {
            if (length != 0) {
                throw new IndexOutOfBoundsException(Integer.toString(offset));
            }
        }
        return this;
    }
    
    public PacketEncoder writeByte(int theByte) throws IOException {
        buffer.put((byte) theByte);
        return this;
    }
}
