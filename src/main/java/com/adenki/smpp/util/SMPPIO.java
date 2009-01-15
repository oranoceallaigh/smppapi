package com.adenki.smpp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * Class that provides input and output methods for writing Java types encoded
 * as SMPP types. This class cannot be instantiated...all it's methods are
 * static.
 */
public final class SMPPIO {
    
    private static final String US_ASCII = "US-ASCII";

    private SMPPIO() {
    }
    
    /**
     * Read a C-String (a string terminated by a nul-byte) from the byte
     * array. The bytes will be interpreted as US-ASCII characters.
     * @param bytes The bytes to decode the string from.
     * @param offset The offset in <code>bytes</code> to begin reading the
     * string from.
     * @return The decoded string.
     * @throws ArrayIndexOutOfBoundsException if no <code>nul</code> byte
     * is detected in the array.
     */
    public static String readCString(byte[] bytes, int offset) {
        try {
            int endIndex = offset;
            for (; bytes[endIndex] != (byte) 0; endIndex++);
            return new String(bytes, offset, (endIndex - offset), "US-ASCII");
        } catch (UnsupportedEncodingException x) {
            throw new SMPPRuntimeException("JVM does not support ASCII!", x);
        }
    }

    /**
     * Read a fixed-length String from <code>bytes</code>. The bytes will be
     * interpreted as US-ASCII characters.
     * @param bytes The bytes to decode the string from.
     * @param offset The offset in the array to begin parsing from.
     * @param length The number of bytes to parse for the string.
     * @return The decoded String.
     * @throws ArrayIndexOutOfBoundsException if there are insufficient bytes
     * to parse the string.
     */
    public static String readString(byte[] bytes, int offset, int length) {
        try {
            if (offset + length > bytes.length) {
                throw new ArrayIndexOutOfBoundsException(offset + length);
            }
            return new String(bytes, offset, length, "US-ASCII");
        } catch (UnsupportedEncodingException x) {
            throw new SMPPRuntimeException("JVM does not support ASCII??", x);
        }
    }

    /**
     * Read an unsigned 1-byte integer from a byte array.
     * @param b The byte array to read the integer from.
     * @param offset The offset in the array to read the integer from.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static int readUInt1(byte[] b, int offset) {
        return (int) b[offset] & 0xff;
    }
    
    /**
     * Read a 2-byte unsigned integer from a byte array.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the integer resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static int readUInt2(byte[] b, int offset) {
        int value = (int) b[offset + 1] & 0xff;
        value |= ((int) b[offset] & 0xff) << 8;
        return value;
    }
    
    /**
     * Read a 4-byte integer from a byte array. <b>Warning:</b> SMPP integers
     * are unsigned, whereas Java integers are signed. This method will take
     * the 4 bytes as they appear in the array and assemble them into a
     * Java int. If the <u>unsigned</u> value exceeds <code>Integer.MAX_VALUE
     * </code>, then the Java value will actually be a negative number.
     * <p>If you are more interested in the actual value, then use
     * the {@link #readUInt4} method, which will return the real value in a
     * Java <code>long</code> primitive.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the integer resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static int readInt4(byte[] b, int offset) {
        int value = (int) b[offset + 3] & 0xff;
        value |= ((int) b[offset] & 0xff) << 24;
        value |= ((int) b[offset + 1] & 0xff) << 16;
        value |= ((int) b[offset + 2] & 0xff) << 8;
        return value;
    }

    /**
     * Read a 4-byte unsigned integer from a byte array, returning a long. This
     * method is provided as SMPP integers are unsigned, so a <code>long</code>
     * primitive is needed in Java to support the full range of unsigned
     * values.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the integer resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static long readUInt4(byte[] b, int offset) {
        long value = (long) b[offset + 3] & 0xffL;
        value |= ((long) b[offset] & 0xffL) << 24;
        value |= ((long) b[offset + 1] & 0xffL) << 16;
        value |= ((long) b[offset + 2] & 0xffL) << 8;
        return value;
    }

    /**
     * Read an 8-byte integer from a byte array. Care should be taken as
     * values read from the wire that exceed <code>Long.MAX_VALUE</code>
     * will be interpreted by Java as negative numbers.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the int resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static long readInt8(byte[] b, int offset) {
        long value = (long) b[offset + 7] & 0xffL;
        value |= ((long) b[offset] & 0xffL) << 56;
        value |= ((long) b[offset + 1] & 0xffL) << 48;
        value |= ((long) b[offset + 2] & 0xffL) << 40;
        value |= ((long) b[offset + 3] & 0xffL) << 32;
        value |= ((long) b[offset + 4] & 0xffL) << 24;
        value |= ((long) b[offset + 5] & 0xffL) << 16;
        value |= ((long) b[offset + 6] & 0xffL) << 8;
        return value;
    }

    /**
     * Write a byte value to the output stream;
     * @param b The value to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs writing to the stream.
     */
    public static void writeByte(int b, OutputStream out) throws IOException {
        out.write(b);
    }
    
    /**
     * Write a 2-byte integer value to the output stream.
     * @param value The value to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs writing to the stream.
     */
    public static void writeShort(int value, OutputStream out) throws IOException {
        out.write(value >>> 8);
        out.write(value);
    }

    /**
     * Write a 4-byte integer value to the output stream.
     * @param value The value to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs writing to the stream.
     */
    public static void writeInt(int value, OutputStream out) throws IOException {
        out.write(value >>> 24);
        out.write(value >>> 16);
        out.write(value >>> 8);
        out.write(value);
    }

    /**
     * Write a 4-byte integer value to the output stream. This method is
     * provided as SMPP integers are unsigned, so we need a <code>long</code>
     * to contain one in Java.
     * @param value The value to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs writing to the stream.
     */
    public static void writeLongInt(long value, OutputStream out) throws IOException {
        out.write((int) (value >>> 24));
        out.write((int) (value >>> 16));
        out.write((int) (value >>> 8));
        out.write((int) value);
    }

    /**
     * Write an 8-byte integer value to the output stream.
     * @param value The value to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs writing to the stream.
     */
    public static void writeLong(long value, OutputStream out) throws IOException {
        out.write((int) (value >>> 56));
        out.write((int) (value >>> 48));
        out.write((int) (value >>> 40));
        out.write((int) (value >>> 32));
        out.write((int) (value >>> 24));
        out.write((int) (value >>> 16));
        out.write((int) (value >>> 8));
        out.write((int) value);
    }
    
    /**
     * Write a String to an OutputStream followed by a NUL byte.
     * 
     * @param s
     *            The string to write
     * @param out
     *            The output stream to write to
     * @throws java.io.IOException
     *             If an I/O error occurs
     * @see java.io.OutputStream
     */
    public static void writeCString(String s, OutputStream out)
            throws java.io.IOException {
        writeString(s, out);
        out.write(0);
    }

    /**
     * Write a String of specified length to an OutputStream.
     * @param s The String to write
     * @param len The number of bytes to write. If <code>len</code> is greater
     * than the number of characters in the string, an exception will be
     * thrown.
     * @param out The OutputStream to write to.
     * @throws java.io.IOException If an I/O error occurs
     * @throws ArrayIndexOutOfBoundsException If there are not enough characters
     * to satisfy the <code>len</code> parameter.
     */
    public static void writeString(String s, int len, OutputStream out) throws IOException {
        if (s != null) {
            byte[] b = s.getBytes(US_ASCII);
            out.write(b, 0, len);
        }
    }

    /**
     * Write a String in it's entirety to an OutputStream
     * 
     * @param s
     *            The String to write
     * @param out
     *            The OutputStream to write to
     * @throws java.io.IOException
     *             If an I/O error occurs
     * @see java.io.OutputStream
     */
    public static void writeString(String s, OutputStream out) throws IOException {
        if (s != null) {
            out.write(s.getBytes(US_ASCII));
        }
    }
}
