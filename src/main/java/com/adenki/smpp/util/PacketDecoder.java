package com.adenki.smpp.util;

import java.io.EOFException;
import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

/**
 * Utility interface for decoding packet fields.
 * @version $Id$
 */
public interface PacketDecoder {
    /**
     * Read a C-String (a string terminated by a nul-byte).
     * The bytes will be interpreted as US-ASCII characters.
     * @return The decoded String.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes to read a C-String.
     */
    String readCString() throws IOException;
    
    /**
     * Read a fixed-length String. Bytes will be interpreted as
     * US-ASCII characters.
     * @param length The number of bytes to parse for the string.
     * @return The decoded String.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes to satisfy
     * <tt>length</tt>.
     */
    String readString(int length) throws IOException;
    
    /**
     * Read a single byte.
     * @return A single byte.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are no bytes available.
     */
    byte readByte() throws IOException;
    
    /**
     * Read a 1-byte unsigned integer.
     * @return The decoded integer.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are no bytes available.
     */
    int readUInt1() throws IOException;

    /**
     * Read a 2-byte unsigned integer. SMPP integers are
     * big-endian, also known as network byte order.
     * @return The decoded integer.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    int readUInt2() throws IOException;

    /**
     * Read a 4-byte unsigned integer. SMPP integers are
     * big-endian, also known as network byte order. Since the integers are
     * unsigned, a Java <code>long</code> primitive is required to hold
     * all the possible values.
     * @return The decoded (long) integer.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    long readUInt4() throws IOException;
    
    /**
     * Read an 8-byte unsigned integer. SMPP integers are
     * big-endian, also known as network byte order. Since the integers are
     * unsigned, a Java <code>long</code> primitive is required to hold
     * all the possible values.
     * @return The decoded (long) integer.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    long readInt8() throws IOException;
    
    /**
     * Read an SMPP address.
     * @return The parsed address.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    Address readAddress() throws IOException;

    /**
     * Read an SMPP address and error code pairing. This is used in the
     * submit multi response packet to create its table of unsuccessful
     * submissions.
     * @return An error address object.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    ErrorAddress readErrorAddress() throws IOException;
    
    /**
     * Read an SMPP date.
     * @return The parsed SMPP date, or <code>null</code> if the date is null.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    SMPPDate readDate() throws IOException;
    
    /**
     * Read a number of bytes.
     * @param length The number of bytes to read.
     * @return A byte array with <code>length</code> bytes in it.
     * @throws IOException If a problem occurs in reading.
     * @throws EOFException If there are not enough bytes available.
     */
    byte[] readBytes(int length) throws IOException;
}
