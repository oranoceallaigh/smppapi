package ie.omk.smpp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * Read an Integer from an InputStream. The integer read from the stream is
     * assumed to be in network byte order (ie Big Endian).
     * 
     * @param in
     *            The InputStream to read from
     * @param len
     *            The number of bytes to form the integer from (usually either 1
     *            or 4, limited to 1 &lt;= len &lt;= 8)
     * @return An integer representation of the len bytes read from in.
     * @throws java.io.IOException
     *             If EOS is reached before <code>len</code> bytes are read.
     * @see java.io.InputStream
     */
    public static int readInt(InputStream in, int len)
            throws java.io.IOException {
        byte[] b = new byte[len];
        int p = 0;
        for (int loop = 0; loop < (len - p); loop++) {
            int r = in.read(b, p, len - p);
            if (r == -1) {
                break;
            }

            p += r;
        }

        return bytesToInt(b, 0, len);
    }

    /**
     * Read in a NUL-terminated string from an InputStream
     * 
     * @param in
     *            The InputStream to read from
     * @return A String representation with the NUL byte removed.
     * @throws java.io.IOException
     *             If EOS is reached before a NUL byte
     * @see java.io.InputStream
     */
    public static String readCString(InputStream in)
            throws java.io.IOException {
        StringBuffer s = new StringBuffer();

        int b = in.read();
        while (b != 0) {
            if (b == -1) {
                throw new IOException("End of Input Stream before NULL byte");
            }

            s.append((char) b);
            b = in.read();
        }

        if (s.length() == 0) {
            return null;
        } else {
            return s.toString();
        }
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
     * Read in a string of specified length from an InputStream. The String may
     * contain NUL bytes.
     * 
     * @param in
     *            The InputStream to read from
     * @param len
     *            The number of bytes to read in from the InputStream
     * @return A String of length <code>len</code>. null if <code>len</code>
     *         is less than 1.
     * @throws java.io.IOException
     *             If EOS is reached before a NUL byte
     * @see java.io.InputStream
     */
    public static String readString(InputStream in, int len)
            throws java.io.IOException {
        String s = null;
        if (len >= 1) {
            byte[] b = new byte[len];
            int l = 0;
            StringBuffer buf = new StringBuffer();
    
            while (l < len) {
                int r = in.read(b, 0, len - l);
                if (r == -1) {
                    throw new IOException("EOS before NUL byte read.");
                }
    
                l += r;
                buf.append(new String(b, 0, r, US_ASCII));
            }

            if (buf.length() > 0) {
                s = buf.toString();
            }
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
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return s;
    }

    /**
     * Convert an integer to a byte array in MSB first order
     * 
     * @param num
     *            The number to store
     * @param len
     *            The length of the integer to convert
     * @return An array of length len containing the byte representation of num.
     */
    public static byte[] intToBytes(int num, int len) {
        return intToBytes(num, len, null, 0);
    }

    /**
     * Convert an integer to a byte array in MSB first order. This method exists
     * as well as the <code>longToBytes</code> method for performance reasons.
     * More often than not, a 4-byte value is the largest being
     * converted...doing that using <code>ints</code> instead of
     * <code>longs</code> will offer a slight performance increase. The code
     * for the two methods is identical except for using ints instead of longs
     * to hold mask, shiftwidth and number values.
     * 
     * @param num
     *            The number to store
     * @param len
     *            The length of the integer to convert (that is, the number of
     *            bytes to generate).
     * @param array
     *            the byte array to write the integer to.
     * @param offset
     *            the offset in <code>b</code> to write the integer to.
     * @return An array of length len containing the byte representation of num.
     */
    public static byte[] intToBytes(int num, int len, byte[] array, int offset) {

        byte[] b = array;
        if (array == null) {
            b = new byte[len];
            offset = 0;
        }
        int sw = (len - 1) * 8;
        int mask = 0xff << sw;

        for (int l = 0; l < len; l++) {
            b[offset + l] = (byte) ((num & mask) >>> sw);

            sw -= 8;
            mask >>>= 8;
        }

        return b;
    }

    /**
     * Convert a long to a byte array in MSB first order.
     * 
     * @param num
     *            The number to store
     * @param len
     *            The length of the integer to convert (that is, the number of
     *            bytes to generate).
     * @return An array of length len containing the byte representation of num.
     */
    public static byte[] longToBytes(long num, int len) {
        return longToBytes(num, len, null, 0);
    }

    /**
     * Convert a long to a byte array in MSB first order.
     * 
     * @param num
     *            The number to store
     * @param len
     *            The length of the integer to convert (that is, the number of
     *            bytes to generate).
     * @param b
     *            the byte array to write the integer to.
     * @param offset
     *            the offset in <code>b</code> to write the integer to.
     * @return An array of length len containing the byte representation of num.
     */
    public static byte[] longToBytes(long num, int len, byte[] b,
            int offset) {

        if (b == null) {
            b = new byte[len];
            offset = 0;
        }
        long sw = (len - 1) * 8;
        long mask = 0xffL << sw;

        for (int l = 0; l < len; l++) {
            b[offset + l] = (byte) ((num & mask) >>> sw);

            sw -= 8;
            mask >>>= 8;
        }

        return b;
    }

    /**
     * Convert a byte array (or part thereof) into an integer. The byte array
     * should be in big-endian form. That is, the byte at index 'offset' should
     * be the MSB.
     * 
     * @param b
     *            The array containing the bytes
     * @param offset
     *            The array index of the MSB
     * @param size
     *            The number of bytes to convert into the integer
     * @return An integer value represented by the specified bytes.
     */
    public static int bytesToInt(byte[] b, int offset, int size) {
        int num = 0;
        int sw = 8 * (size - 1);

        for (int loop = 0; loop < size; loop++) {
            num |= ((int) b[offset + loop] & 0x00ff) << sw;
            sw -= 8;
        }

        return num;
    }

    /**
     * Convert a byte array (or part thereof) into a long. The byte array should
     * be in big-endian form. That is, the byte at index 'offset' should be the
     * MSB.
     * 
     * @param b
     *            The array containing the bytes
     * @param offset
     *            The array index of the MSB
     * @param size
     *            The number of bytes to convert into the long
     * @return An long value represented by the specified bytes.
     */
    public static long bytesToLong(byte[] b, int offset, int size) {
        long num = 0;
        long sw = 8L * ((long) size - 1L);

        for (int loop = 0; loop < size; loop++) {
            num |= ((long) b[offset + loop] & 0x00ff) << sw;
            sw -= 8;
        }

        return num;
    }

    /**
     * Write the byte representation of an integer to an OutputStream in MSB
     * order.
     * 
     * @param x
     *            The integer to write
     * @param len
     *            The number of bytes in this integer (usually either 1 or 4)
     * @param out
     *            The OutputStream to write the integer to
     * @throws java.io.IOException
     *             If an I/O error occurs.
     * @see java.io.OutputStream
     */
    public static void writeInt(int x, int len, OutputStream out)
            throws java.io.IOException {
        out.write(intToBytes(x, len));
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
    public static void writeString(String s, int len, OutputStream out)
    throws java.io.IOException {
        if (s == null) {
            return;
        }

        if (len > s.length()) {
            writeString(s, out);
        } else {
            writeString(s.substring(0, len), out);
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
    public static void writeString(String s, OutputStream out)
    throws java.io.IOException {
        if (s == null) {
            return;
        }
        out.write(s.getBytes());
    }
}
