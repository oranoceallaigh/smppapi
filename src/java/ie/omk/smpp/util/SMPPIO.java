package ie.omk.smpp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

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
     * Read a nul-terminated ASCII string from a byte array.
     * 
     * @return A java String object representing the read string without the
     *         terminating nul.
     */
    public static String readCString(byte[] b, int offset) {
        String s;
        try {
            int p = offset;
            while (b[p] != (byte) 0) {
                p++;
            }

            if (p > offset) {
                s = new String(b, offset, p - offset, US_ASCII);
            } else {
                s = "";
            }
        } catch (java.io.UnsupportedEncodingException x) {
            s = "";
        }
        return s;
    }

    /**
     * Read an ASCII string from a byte array.
     * @param b The byte array to read from.
     * @param offset The offset into <code>b</code> to begin reading from.
     * @param len The length of the string to read.
     * @return A string decoded from <code>b</code> of length <code>len</code>.
     * ASCII is used to convert the bytes into characters.
     */
    public static String readString(byte[] b, int offset, int len) {
        String s = "";
        try {
            if (len > 0) {
                s = new String(b, offset, len - offset, US_ASCII);
            }
        } catch (UnsupportedEncodingException x) {
            // JVM is required to support US-ASCII
        }
        return s;
    }

    /**
     * Decode a 1-byte integer from a byte array.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the short resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static int bytesToByte(byte[] b, int offset) {
        return (int) b[offset] & 0xff;
    }
    
    /**
     * Decode a 2-byte integer from a byte array.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the short resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static int bytesToShort(byte[] b, int offset) {
        int value = (int) b[offset + 1] & 0xff;
        value |= ((int) b[offset] & 0xff) << 8;
        return value;
    }
    
    /**
     * Decode a 4-byte integer from a byte array.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the int resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static int bytesToInt(byte[] b, int offset) {
        int value = (int) b[offset + 3] & 0xff;
        value |= ((int) b[offset] & 0xff) << 24;
        value |= ((int) b[offset + 1] & 0xff) << 16;
        value |= ((int) b[offset + 2] & 0xff) << 8;
        return value;
    }

    /**
     * Decode a 4-byte integer from a byte array, returning a long. This
     * method is provided as SMPP bytes are unsigned, so we need a
     * <code>long</code> to contain them in Java.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the int resides.
     * @return The decoded integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static long bytesToLongInt(byte[] b, int offset) {
        long value = (long) b[offset + 3] & 0xff;
        value |= ((long) b[offset] & 0xff) << 24;
        value |= ((long) b[offset + 1] & 0xff) << 16;
        value |= ((long) b[offset + 2] & 0xff) << 8;
        return value;
    }

    /**
     * Decode an 8-byte integer from a byte array.
     * @param b The byte array to read the integer from.
     * @param offset The offset at which the (most significant) first byte of
     * the int resides.
     * @return The decoded long integer value.
     * @throws ArrayIndexOutOfBoundsException If there are not enough bytes in
     * the array.
     */
    public static long bytesToLong(byte[] b, int offset) {
        long value = (long) b[offset + 7] & 0xff;
        value |= ((long) b[offset] & 0xff) << 56;
        value |= ((long) b[offset + 1] & 0xff) << 48;
        value |= ((long) b[offset + 2] & 0xff) << 40;
        value |= ((long) b[offset + 3] & 0xff) << 32;
        value |= ((long) b[offset + 4] & 0xff) << 24;
        value |= ((long) b[offset + 5] & 0xff) << 16;
        value |= ((long) b[offset + 6] & 0xff) << 8;
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
        out.write(value & 0xff);
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
        out.write(value & 0xff);
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
        out.write((int) (value >>> 24L));
        out.write((int) (value >>> 16L));
        out.write((int) (value >>> 8L));
        out.write((int) (value & 0xffL));
    }

    /**
     * Write an 8-byte integer value to the output stream.
     * @param value The value to write.
     * @param out The output stream to write to.
     * @throws IOException If an error occurs writing to the stream.
     */
    public static void writeLong(long value, OutputStream out) throws IOException {
        out.write((int) (value >>> 56L));
        out.write((int) (value >>> 48L));
        out.write((int) (value >>> 40L));
        out.write((int) (value >>> 32L));
        out.write((int) (value >>> 24L));
        out.write((int) (value >>> 16L));
        out.write((int) (value >>> 8L));
        out.write((int) (value & 0xffL));
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
        out.write((byte) 0);
    }

    /**
     * Write a String of specified length to an OutputStream.
     * 
     * @param s
     *            The String to write
     * @param len
     *            The length of the String to write. If this is longer than the
     *            length of the String, the whole String will be sent.
     * @param out
     *            The OutputStream to use
     * @throws java.io.IOException
     *             If an I/O error occurs
     * @see java.io.OutputStream
     */
    public static void writeString(String s, int len, OutputStream out) throws IOException {
        if (s != null) {
            if (len > s.length()) {
                writeString(s, out);
            } else {
                writeString(s.substring(0, len), out);
            }
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
