/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
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
 * Java SMPP API author: oran.kelly@ireland.com
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
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
      * @exception java.io.IOException If EOS is reached before <i>len</i> bytes
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

    /** Read in a string of specified length from an InputStream.
      * The String may contain NUL bytes.
      * @param in The InputStream to read from
      * @param len The number of bytes to read in from the InputStream
      * @return A String of length <i>len</i>. null if <i>len</i> is less than
      * 1.
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


    /** Convert an integer to a byte array in MSB first order
      * @param num The number to store
      * @param len The length of the integer to convert
      * @return An array of length len containing the byte representation of
      * num.
      */
    public static final byte[] intToBytes(int num, int len)
    {
	byte[] b = new byte[len];
	int sw = ((len - 1) * 8);
	int mask = (0xff << sw);

	for (int l = 0; l < len; l++) {
	    b[l] = (byte)((num & mask) >>> sw);
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
      * @param len The length of the String to write.  If this is longer than the length of the String, the whole String will be sent.
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
}
