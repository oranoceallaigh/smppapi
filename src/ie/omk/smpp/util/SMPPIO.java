/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 * $Id$
 */

package ie.omk.smpp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Class that provides input and output methods for writing Java types
  * encoded as SMPP types. This class cannot be instantiated...all it's methods
  * are static.
  */
public class SMPPIO
{
    /** Read an Integer from an InputStream.
      * The integer read from the stream is assumed to be in network byte order
      * (ie Big Endian).
      * @param in The InputStream to read from
      * @param len The number of bytes to form the integer from (usually
      * either 1 or 4, limited to 1 &lt;= len &lt;= 8)
      * @return An integer representation of the len bytes read from in.
      * @exception java.io.IOException If EOS is reached before <code>len</code>
      * bytes
      * are read.
      * @see java.io.InputStream
      */
    public static final int readInt(InputStream in, int len)
	throws java.io.IOException
    {
	byte[] b = new byte[len];
	int p = 0;
	int x = 0;

	for (int loop = 0; loop < (len - p); loop++) {
	    int r = in.read(b, p, (len - p));
	    if (r == -1)
		break;

	    p += r;
	}

	return (bytesToInt(b, 0, len));
    }

    /** Read in a NUL-terminated string from an InputStream
      * @param in The InputStream to read from
      * @return A String representation with the NUL byte removed.
      * @exception java.io.IOException If EOS is reached before a NUL byte
      * @see java.io.InputStream
      */
    public static final String readCString(InputStream in)
	throws java.io.IOException
    {
	StringBuffer s = new StringBuffer();

	int b = in.read();
	while (b != 0) {
	    if (b == -1)
		throw new IOException("End of Input Stream before NULL byte");

	    s.append((char)b);
	    b = in.read();
	}

	if (s.length() == 0)
	    return (null);
	else
	    return (s.toString());
    }

    /** Read a nul-terminated ASCII string from a byte array.
     * @return A java String object representing the read string without the
     * terminating nul.
     */
    public static final String readCString(byte[] b, int offset)
    {
	try {
	    int p = offset;
	    while (b[p] != (byte)0)
		p++;

	    if (p > offset)
		return (new String(b, offset, p - offset, "US-ASCII"));
	    else
		return ("");
	} catch (java.io.UnsupportedEncodingException x) {
	    return ("");
	}
    }

    /** Read in a string of specified length from an InputStream.
      * The String may contain NUL bytes.
      * @param in The InputStream to read from
      * @param len The number of bytes to read in from the InputStream
      * @return A String of length <code>len</code>. null if <code>len</code> is
      * less than 1.
      * @exception java.io.IOException If EOS is reached before a NUL byte
      * @see java.io.InputStream
      */
    public static final String readString(InputStream in, int len)
	throws java.io.IOException
    {
	if (len < 1)
	    return (null);

	byte[] b = new byte[len];
	int l = 0;
	StringBuffer s = new StringBuffer();

	while (l < len) {
	    int r = in.read(b, 0, (len - l));
	    if (r == -1)
		throw new IOException("EOS before NUL byte read.");

	    l += r;
	    s.append(new String(b, 0, r));
	}

	if (s.length() == 0)
	    return (null);
	else
	    return (s.toString());
    }

    /** XXX write the javadoc.
     */
    public static final String readString(byte[] b, int offset, int len)
    {
	try {
	    if (len > 0)
		return (new String(b, offset, len - offset, "US-ASCII"));
	    else
		return ("");
	} catch (java.io.UnsupportedEncodingException x) {
	    return ("");
	}
    }

    /** Convert an integer to a byte array in MSB first order
      * @param num The number to store
      * @param len The length of the integer to convert
      * @return An array of length len containing the byte representation of
      * num.
      */
    public static final byte[] intToBytes(int num, int len)
    {
	return (intToBytes(num, len, null, 0));
    }

    /** Convert an integer to a byte array in MSB first order. This method
     * exists as well as the <code>longToBytes</code> method for performance
     * reasons. More often than not, a 4-byte value is the largest being
     * converted...doing that using <code>ints</code> instead of
     * <code>longs</code> will offer a slight performance increase. The code for
     * the two methods is identical except for using ints instead of longs to
     * hold mask, shiftwidth and number values.
     * @param num The number to store
     * @param len The length of the integer to convert (that is, the number of
     * bytes to generate).
     * @param b the byte array to write the integer to.
     * @param offset the offset in <code>b</code> to write the integer to.
     * @return An array of length len containing the byte representation of
     * num.
     */
    public static final byte[] intToBytes(int num, int len, byte[] b,
	    int offset) {

	if (b == null) {
	    b = new byte[len];
	    offset = 0;
	}
	int sw = ((len - 1) * 8);
	int mask = (0xff << sw);

	for (int l = 0; l < len; l++) {
	    b[offset + l] = (byte)((num & mask) >>> sw);

	    sw -= 8;
	    mask >>>= 8;
	}

	return (b);
    }

    /** Convert a long to a byte array in MSB first order.
      * @param num The number to store
      * @param len The length of the integer to convert (that is, the number of
      * bytes to generate).
      * @return An array of length len containing the byte representation of
      * num.
      */
    public static final byte[] longToBytes(long num, int len) {
	return (longToBytes(num, len, null, 0));
    }

    /** Convert a long to a byte array in MSB first order.
      * @param num The number to store
      * @param len The length of the integer to convert (that is, the number of
      * bytes to generate).
      * @param b the byte array to write the integer to.
      * @param offset the offset in <code>b</code> to write the integer to.
      * @return An array of length len containing the byte representation of
      * num.
      */
    public static final byte[] longToBytes(long num, int len, byte[] b,
	    int offset) {

	if (b == null) {
	    b = new byte[len];
	    offset = 0;
	}
	long sw = ((len - 1) * 8);
	long mask = (0xffL << sw);

	for (int l = 0; l < len; l++) {
	    b[offset + l] = (byte)((num & mask) >>> sw);

	    sw -= 8;
	    mask >>>= 8;
	}

	return (b);
    }

    /** Convert a byte array (or part thereof) into an integer.
      * The byte array should be in big-endian form. That is, the byte at index
      * 'offset' should be the MSB.
      * @param b The array containing the bytes
      * @param offset The array index of the MSB
      * @param size The number of bytes to convert into the integer
      * @return An integer value represented by the specified bytes.
      */
    public static final int bytesToInt(byte[] b, int offset, int size)
    {
	int num = 0;
	int sw = 8 * (size - 1);

	for (int loop = 0; loop < size; loop++) {
	    num |= ((int)b[offset + loop] & 0x00ff) << sw;
	    sw -= 8;
	}

	return (num);
    }

    /** Convert a byte array (or part thereof) into a long.
      * The byte array should be in big-endian form. That is, the byte at index
      * 'offset' should be the MSB.
      * @param b The array containing the bytes
      * @param offset The array index of the MSB
      * @param size The number of bytes to convert into the long
      * @return An long value represented by the specified bytes.
      */
    public static final long bytesToLong(byte[] b, int offset, int size)
    {
	long num = 0;
	long sw = 8L * ((long)size - 1L);

	for (int loop = 0; loop < size; loop++) {
	    num |= ((long)b[offset + loop] & 0x00ff) << sw;
	    sw -= 8;
	}

	return (num);
    }

    /** Write the byte representation of an integer to an OutputStream in MSB
      * order.
      * @param x The integer to write
      * @param len The number of bytes in this integer (usually either 1 or 4)
      * @param out The OutputStream to write the integer to
      * @exception java.io.IOException If an I/O error occurs.
      * @see java.io.OutputStream
      */
    public static void writeInt(int x, int len, OutputStream out)
	throws java.io.IOException
    {
	out.write(intToBytes(x, len));
    }

    /** Write a String to an OutputStream followed by a NUL byte
      * @param s The string to write
      * @param out The output stream to write to
      * @exception java.io.IOException If an I/O error occurs
      * @see java.io.OutputStream
      */
    public static void writeCString(String s, OutputStream out)
	throws java.io.IOException
    {
	writeString(s, out);
	out.write((byte)0);
    }

    /** Write a String of specified length to an OutputStream
      * @param s The String to write
      * @param len The length of the String to write.  If this is longer than
      * the length of the String, the whole String will be sent.
      * @param out The OutputStream to use
      * @exception java.io.IOException If an I/O error occurs
      * @see java.io.OutputStream
      */
    public static void writeString(String s, int len, OutputStream out)
	throws java.io.IOException
    {
	if (s == null)
	    return;

	if (len > s.length())
	    writeString(s, out);
	else
	    writeString(s.substring(0, len), out);
    }

    /** Write a String in it's entirety to an OutputStream
      * @param s The String to write
      * @param out The OutputStream to write to
      * @exception java.io.IOException If an I/O error occurs
      * @see java.io.OutputStream
      */
    public static void writeString(String s, OutputStream out)
	throws java.io.IOException
    {
	if (s == null)
	    return;
	out.write(s.getBytes());
    }

    /*public static final void main(String[] args)
    {
	// Integer/byte conversion tests
	int[] twoByte_vals = {
	    0x4512, 0xdead, 0xcafe, 0xfcfc
	};
	byte[][] twoByte_bytes = {
	    { 0x45, 0x12 },
	    { (byte)0xde, (byte)0xad },
	    { (byte)0xca, (byte)0xfe },
	    { (byte)0xfc, (byte)0xfc }
	};

	int[] threeByte_vals = {
	    0x112233, 0x432165, 0xf88ee2
	};
	byte[][] threeByte_bytes = {
	    { 0x11, 0x22, 0x33 },
	    { 0x43, 0x21, 0x65 },
	    { (byte)0xf8, (byte)0x8e, (byte)0xe2 }
	};

	int[] fourByte_vals = {
	    0xdeadbeef, 0xcafefeed, 0xbeeffeed
	};
	byte[][] fourByte_bytes = {
	    { (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef },
	    { (byte)0xca, (byte)0xfe, (byte)0xfe, (byte)0xed },
	    { (byte)0xbe, (byte)0xef, (byte)0xfe, (byte)0xed }
	};

	boolean passed = true;
	passed &= b2iTest(twoByte_bytes, twoByte_vals, 2);
	passed &= b2iTest(threeByte_bytes, threeByte_vals, 3);
	passed &= b2iTest(fourByte_bytes, fourByte_vals, 4);

	// C-string test..
	try {
	    String s = "This is a CString test. ASCII chars only, please!";
	    byte[] sb = s.getBytes("US-ASCII");
	    byte[] sb1 = new byte[sb.length + 1];
	    byte[] sb2;
	    java.io.ByteArrayOutputStream os =
		    new java.io.ByteArrayOutputStream();

	    System.arraycopy(sb, 0, sb1, 0, sb.length);
	    sb1[sb.length] = (byte)0;
	    writeCString(s, os);
	    sb2 = os.toByteArray();

	    String s1 = readCString(sb1, 0);
	    passed &= (s1.equals(s));
	    passed &= java.util.Arrays.equals(sb1, sb2);
	} catch (java.io.UnsupportedEncodingException x) {
	    passed = false;
	    System.out.println("Unsupported encoding!");
	    x.printStackTrace(System.out);
	} catch (java.io.IOException x) {
	    passed = false;
	    x.printStackTrace(System.out);
	}
	

	if (!passed)
	    System.out.println("Test failed.");
	else
	    System.out.println("All tests passed.");
    }
    // Run a byte array/integer conversion test.
    // @param bArray array of array of bytes representing the values.
    // @param vArray array of values.
    // @param size the number of bytes per integer.
    private static final boolean b2iTest(byte[][] bArray,
	    int[] vArray, int size)
    {
	boolean ret = true;
	for (int i = 0; i < vArray.length; i++) {
	    int ri = bytesToInt(bArray[i], 0, size);
	    byte[] bo = intToBytes(vArray[i], size);

	    if (ri != vArray[i]) {
		ret = false;
		System.out.println("bytesToInt failed on "
			+ size + " bytes, pos " + i);
		System.out.println("\tValue = " + vArray[i] + ", ri = " + ri);
	    }

	    if (!java.util.Arrays.equals(bo, bArray[i])) {
		ret = false;
		System.out.println("intToBytes failed in "
			+ size + " bytes, pos " + i);

		System.out.print("Fixed:    ");
		for (int j = 0; j < bo.length; j++) {
		    System.out.print(Integer.toHexString(new Byte(
				    bo[j]).intValue()&0xff) + " ");
		}
		System.out.print("Generated:");
		for (int j = 0; j < bArray[i].length; j++) {
		    System.out.print(Integer.toHexString(new Byte(
				    bArray[i][j]).intValue()&0xff) + " ");
		}
	    }
	}
	return (ret);
    }*/
}
