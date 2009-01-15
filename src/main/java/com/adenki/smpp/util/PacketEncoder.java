package com.adenki.smpp.util;

import java.io.IOException;
import java.io.OutputStream;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

/**
 * Interface specification for a packet encoder.
 * @version $Id$
 */
public interface PacketEncoder {
    
    /**
     * Set the output stream this encoder is writing to.
     * @param out The output stream to write to.
     * @return This packet encoder.
     */
    PacketEncoder setStream(OutputStream out);
    
    /**
     * Get the stream this encoder is writing to.
     * @return The stream this encoder is writing to.
     */
    OutputStream getStream();
    
    /**
     * Write a C-String (one that is terminated with a nul byte) to the
     * output stream. The characters will be encoded using US-ASCII.
     * @param value The string value to write. If <code>value</code> is
     * <code>null</code> a single nul-byte will still be written.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeCString(String value) throws IOException;

    /**
     * Write a string to the output stream. The characters will be encoded
     * using US-ASCII.
     * @param value The string value to write.
     * @param length The number of characters to write.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     * @throws IndexOutOfBoundsException If <code>length</code> is
     * longer than the number of characters in <code>value</code>.
     */
    PacketEncoder writeString(String value, int length) throws IOException;
    
    /**
     * Write a 1-byte unsigned integer to the output stream.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeUInt1(int value) throws IOException;

    /**
     * Write a 2-byte unsigned integer to the output stream in big-endian
     * order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeUInt2(int value) throws IOException;

    /**
     * Write a 4-byte unsigned integer to the output stream in big-endian
     * order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeUInt4(long value) throws IOException;

    /**
     * Write a 4-byte integer to the output stream in big-endian
     * order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeInt4(int value) throws IOException;

    /**
     * Write an 8-byte integer to the output stream in big-endian
     * order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeInt8(long value) throws IOException;

    /**
     * Write an SMPP address to the output stream.
     * @param address The address to write to the stream.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeAddress(Address address) throws IOException;

    /**
     * Write an SMPP error address to the output stream.
     * @param address The error address to write.
     * @return This packet encoder.
     * @throws IOException If a problem occurs while writing.
     */
    PacketEncoder writeErrorAddress(ErrorAddress errorAddress) throws IOException;
    
    /**
     * Write an SMPP date to the output stream.
     * @param date The SMPP date to write to the stream.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     */
    PacketEncoder writeDate(SMPPDate date) throws IOException;
    
    PacketEncoder writeBytes(byte[] array) throws IOException;
    
    /**
     * Write a byte array to the output stream.
     * @param array The byte array to write bytes from.
     * @param offset The offset to begin copying bytes from.
     * @param length The number of bytes to write.
     * @return This packet encoder.
     * @throws IOException If a problem occurs writing to the stream.
     * @throws IndexOutOfBoundsException if there are insufficient bytes
     * in the array to satisfy the <code>length</code> parameter.
     */
    PacketEncoder writeBytes(byte[] array, int offset, int length) throws IOException;
}
