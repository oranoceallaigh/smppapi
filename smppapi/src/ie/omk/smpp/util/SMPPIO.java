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
  * encoded as SMPP types.
  */
public class SMPPIO
{
    /** Read an Integer from an InputStream.
      * The integer read from the stream is assumed to be in network byte order
      * (ie Big Endian).
      * @param in The InputStream to read from
      * @param len The number of bytes to form the integer from (usually either 1 or 4)
      * @return An integer representation of the <i>len</i> bytes read in
      * @exception java.io.IOException If EOS is reached before <i>len</i> bytes
      * @see java.io.InputStream
      */
    public static final int readInt(InputStream in, int len)
	throws IOException
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

	int shiftwidth = 8 * (len - 1);
	for (int loop = 0; loop < b.length; loop++) {
	    x |= ((int)b[loop]) << shiftwidth;
	    shiftwidth -= 8;
	}

	return (x);
    }

    /** Read in a NUL-terminated string from an InputStream
      * @param in The InputStream to read from
      * @return A String representation with the NUL byte removed.
      * @exception java.io.IOException If EOS is reached before a NUL byte
      * @see java.io.InputStream
      */
    public static final String readCString(InputStream in)
	throws IOException
    {
	StringBuffer s = new StringBuffer();

	int b = in.read();
	while (b != 0) {
	    if (b == -1)
		throw new IOException("End of Input Stream before NULL byte");

	    s.append((char)b);
	    b = in.read();
	}

	return (s.toString());
    }

    /** Read in a string of specified length from an InputStream.
      * The String may contain NUL bytes.
      * @param in The InputStream to read from
      * @param len The number of bytes to read in from the InputStream
      * @return A String of length <i>len</i>
      * @exception java.io.IOException If EOS is reached before a NUL byte
      * @see java.io.InputStream
      */
    public static final String readString(InputStream in, int len)
	throws IOException
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

	return (s.toString());
    }


    /** Convert a 4-byte integer to a byte array in MSB first order
      * @param b The array to store the integer in
      * @param offset The position in the array to store the integer in
      * @param num The number to store
      */
    public static final void intToByteArray(byte b[], int offset, int num)
    {
	b[offset]   = (byte)((num & 0xff000000) >> 24);
	b[offset+1] = (byte)((num & 0x00ff0000) >> 16);
	b[offset+2] = (byte)((num & 0x0000ff00) >> 8);
	b[offset+3] = (byte)(num & 0x00000ff);
    }

    /** Convert a byte array (or part thereof) into an integer.
      * The byte array should be in big-endian form. That is, the byte at index
      * 'offset' should be the MSB.
      * @param b The array containing the bytes
      * @param offset The array index of the MSB
      * @param size The number of bytes to convert into the integer
      */
    public static final int bytesToInt(byte[] b, int offset, int size)
    {
	int num = 0;
	int sw = 8 * (size - 1);

	for (int loop = 0; loop < size; loop++) {
	    num |= ((int)b[offset + loop]) << sw;
	    sw -= 8;
	}

	return (num);
    }

    /** Write the byte representation of an integer to an OutputStream in MSB
      * order
      * @param x The integer to write
      * @param len The number of bytes in this integer (usually either 1 or 4)
      * @param out The OutputStream to write the integer to
      * @exception java.io.IOException If an I/O error occurs.
      * @see java.io.OutputStream
      */
    public static void writeInt(int x, int len, OutputStream out)
	throws IOException
    {
	byte[] b = new byte[len];
	int sw = (8 * (len - 1));
	int mask = (0xff << sw);

	for (int loop = 0; loop < len; loop++) {
	    b[loop] = (byte)((x & mask) >>> sw);

	    mask = (mask >>> 8);
	    sw -= 8;
	}

	out.write(b);
    }

    /** Write a String to an OutputStream followed by a NUL byte
      * @param s The string to write
      * @param out The output stream to write to
      * @exception java.io.IOException If an I/O error occurs
      * @see java.io.OutputStream
      */
    public static void writeCString(String s, OutputStream out)
	throws IOException
    {
	if (s == null)
	    s = new String();
	writeString(new String(s + (char)0), out);
    }

    /** Write a String of specified length to an OutputStream
      * @param s The String to write
      * @param len The length of the String to write.  If this is longer than the length of the String, the whole String will be sent.
      * @param out The OutputStream to use
      * @exception java.io.IOException If an I/O error occurs
      * @see java.io.OutputStream
      */
    public static void writeString(String s, int len, OutputStream out)
	throws IOException
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
	throws IOException
    {
	if (s == null)
	    return;
	byte[] b = s.getBytes();
	out.write(b);
    }
}
