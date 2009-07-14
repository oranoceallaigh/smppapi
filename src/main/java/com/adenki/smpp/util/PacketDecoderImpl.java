package com.adenki.smpp.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * A {@link PacketDecoder} which uses NIO {@link ByteByffer ByteByffers}
 * and {@link ByteChannel ByteChannels}.
 * @version $Id$
 */
public class PacketDecoderImpl extends AbstractPacketDecoder {
    private ByteBuffer buffer;
    
    public PacketDecoderImpl(ByteBuffer buffer) {
        this.buffer = buffer;
    }
    
    public byte readByte() throws IOException {
        require(1);
        return buffer.get();
    }

    public byte[] readBytes(int length) throws IOException {
        require(length);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    public String readCString() throws IOException {
        try {
            int start = buffer.position();
            int end = start;
            for (; buffer.get(end) != (byte) 0; end++);
            byte[] bytes = new byte[end - start];
            buffer.get(bytes, 0, bytes.length);
            buffer.get();
            return new String(bytes, "US-ASCII");
        } catch (IndexOutOfBoundsException x) {
            throw new EOFException("Buffer does not contain the nul terminator");
        }
    }

    public long readInt8() throws IOException {
        require(8);
        return buffer.getLong();
    }

    public String readString(int length) throws IOException {
        require(length);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, "US-ASCII");
    }

    public int readUInt1() throws IOException {
        require(1);
        return ((int) buffer.get()) & 0xff;
    }

    public int readUInt2() throws IOException {
        require(2);
        return ((int) buffer.getShort()) & 0xffff;
    }

    public long readUInt4() throws IOException {
        require(4);
        return ((long) buffer.getInt()) & 0xffffffffL;
    }
    
    private void require(int numBytes) throws EOFException {
        if (buffer.remaining() < numBytes) {
            throw new EOFException(
                    "Not enough bytes in buffer, need " + numBytes);
        }
    }
}
