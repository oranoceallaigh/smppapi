package com.adenki.smpp.util;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

/**
 * Utility interface for decoding packet fields.
 * @version $Id$
 */
public interface PacketDecoder {
    /**
     * Get the current parse position of this packet decoder.
     * @return The current parse position.
     */
    int getParsePosition();
    
    /**
     * Set the current parse position of this decoder.
     * @param parsePosition The new parse position.
     */
    void setParsePosition(int parsePosition);
    
    /**
     * Get the number of available bytes to be read by this decoder.
     * @return The number of bytes that can be decoded.
     */
    int getAvailableBytes();
    
    /**
     * Read a C-String (a string terminated by a nul-byte) from the byte
     * array. The bytes will be interpreted as US-ASCII characters.
     * @param bytes The bytes to decode the string from.
     * @param pos The position to start parsing the string from. The
     * parse position must be updated to point to the first byte after
     * the terminating nul byte.
     * @return The decoded String.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse the string.
     */
    String readCString();
    
    /**
     * Read a fixed-length String from the bytes. The bytes will be
     * interpreted as US-ASCII characters.
     * @param bytes The bytes to decode the string from.
     * @param pos The position to start parsing the string from. The parse
     * position will be updated to point to the first byte after the end
     * of the string.
     * @param length The number of bytes to parse for the string.
     * @return The decoded String.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse the string.
     */
    String readString(int length);
    
    /**
     * Read a byte from the byte array.
     * @param bytes The byte array to read from.
     * @param pos The position to return the byte from. The parse position
     * will be incremented by 1 upon return from this method.
     * @return The byte at the specified parse position in the array.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * available.
     */
    byte readByte();
    
    /**
     * Read a 1-byte unsigned integer from the array.
     * @param bytes The byte array to read from.
     * @param pos The position to obtain the integer from. The parse position
     * will be incremented by 1 upon return from this method.
     * @return The decoded integer.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse the integer.
     */
    int readUInt1();

    /**
     * Read a 2-byte unsigned integer from the array. SMPP integers are
     * big-endian, also known as network byte order.
     * @param bytes The byte array to read from.
     * @param pos The position to obtain the integer from. The parse position
     * will be incremented by 2 upon return from this method.
     * @return The decoded integer.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse the integer.
     */
    int readUInt2();

    /**
     * Read a 4-byte unsigned integer from the array. SMPP integers are
     * big-endian, also known as network byte order. Since the integers are
     * unsigned, a Java <code>long</code> primitive is required to hold
     * all the possible values.
     * @param bytes The byte array to read from.
     * @param pos The position to obtain the integer from. The parse position
     * will be incremented by 4 upon return from this method.
     * @return The decoded (long) integer.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse the integer.
     */
    long readUInt4();
    
    // TODO
    long readInt8();
    
    /**
     * Read an SMPP address from the byte array.
     * @param bytes The byte array to read the address from.
     * @param pos The position to begin parsing the address from. The parse
     * position will be updated to point to the first byte after the address
     * in the byte array.
     * @return The parsed address.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse an address.
     */
    Address readAddress();

    /**
     * Read an SMPP address and error code pairing. This is used in the
     * submit multi response packet to create its table of unsuccessful
     * submissions.
     * @return An error address object.
     */
    ErrorAddress readErrorAddress();
    
    /**
     * Read an SMPP date from the byte array.
     * @param bytes The byte array to read the date from.
     * @param pos The position to begin parsing the date from. The parse
     * position will be updated to point to the first byte after the date
     * in the array.
     * @return The parsed SMPP date, or <code>null</code> if the date is null.
     */
    SMPPDate readDate();
    
    /**
     * Get a byte array sub-set from the specified byte array.
     * @param bytes The array to extract bytes from.
     * @param pos The position to begin extracting bytes at. The parse position
     * will be updated to point to the next byte after the extracted byte
     * array.
     * @param length The number of bytes to copy out of the array.
     * @return A byte array with <code>length</code> bytes in it copied
     * from <code>bytes</code>.
     */
    byte[] readBytes(int length);
}
