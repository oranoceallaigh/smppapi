package com.adenki.smpp.util;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

/**
 * Interface specification for a packet encoder.
 * @version $Id$
 */
public interface PacketEncoder {
    /**
     * Output a C-String (one that is terminated with a nul byte).
     * The characters will be encoded using US-ASCII.
     * @param value The string value to write. If <code>value</code> is
     * <code>null</code> a single nul-byte will still be written.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeCString(String value) throws IOException;

    /**
     * Output a string. The characters will be encoded using US-ASCII.
     * @param value The string value to write.
     * @param length The number of characters to write.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     * @throws IndexOutOfBoundsException If <code>length</code> is
     * longer than the number of characters in <code>value</code>.
     */
    PacketEncoder writeString(String value, int length) throws IOException;
    
    /**
     * Output a 1-byte unsigned integer.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeUInt1(int value) throws IOException;

    /**
     * Output a 2-byte unsigned integer in big-endian order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeUInt2(int value) throws IOException;

    /**
     * Output a 4-byte unsigned integer in big-endian order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeUInt4(long value) throws IOException;

    /**
     * Output a 4-byte integer in big-endian order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeInt4(int value) throws IOException;

    /**
     * Output an 8-byte integer in big-endian order.
     * @param value The integer value to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeInt8(long value) throws IOException;

    /**
     * Output an SMPP address.
     * @param address The address to output.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeAddress(Address address) throws IOException;

    /**
     * Ouptut an SMPP error address.
     * @param address The error address to output.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeErrorAddress(ErrorAddress errorAddress) throws IOException;
    
    /**
     * Output an SMPP date.
     * @param date The SMPP date to output.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeDate(SMPPDate date) throws IOException;
    
    /**
     * Output an array of bytes.
     * @param array The bytes to send.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeBytes(byte[] array) throws IOException;
    
    /**
     * Output a single byte.
     * @param theByte The byte to output.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     */
    PacketEncoder writeByte(int theByte) throws IOException;
    
    /**
     * Output a byte array.
     * @param array The byte array to output bytes from.
     * @param offset The offset to begin copying bytes from.
     * @param length The number of bytes to write.
     * @return This packet encoder.
     * @throws IOException If a problem occurs with output.
     * @throws IndexOutOfBoundsException if there are insufficient bytes
     * in the array to satisfy the <code>length</code> parameter.
     */
    PacketEncoder writeBytes(byte[] array, int offset, int length) throws IOException;
}
