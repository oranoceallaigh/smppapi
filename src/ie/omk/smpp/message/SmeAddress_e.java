/*
 * Java implementation of the SMPP v3.3 API
 * Copyright (C) 1998 - 2000 by Oran Kelly
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
 */
package ie.omk.smpp.message;

import java.io.*;
import ie.omk.smpp.message.SmeAddress;
import ie.omk.smpp.SMPPException;

/** This class used in the submit_multi_resp packet to indicate which
  * SME addresses were unsuccessfully submitted to.  It extends SmeAddress
  * to include the error code reported by the SMSC.
  * @author Oran Kelly
  * @version 1.0
  */
public class SmeAddress_e
    extends SmeAddress
{
    /** Error status */
    public int			errorStatus;

    /** Construct a new SmeAddress_e */
    public SmeAddress_e()
    {
	super();
	errorStatus = 0;
    }

    /** Read in an SmeAddress_e from an InputStream
     * @param in InputStream to read from
     * @exception java.io.IOException If an I/O error occurs
     * @see java.io.InputStream
     */
    public SmeAddress_e(InputStream in)
	throws IOException
    {
	super(in);
	errorStatus = SMPPPacket.readInt(in, 4);
    }

    /** Get the size in bytes of this packet */
    public int size()
    {
	return (super.size() + 4);
    }

    /** Write a byte representation of this packet to an OutputStream
     * @param out The OutputStream to write to
     * @exception java.io.IOException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public void writeTo(OutputStream out)
    {
	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();

	    super.writeTo(b);
	    SMPPPacket.writeInt(errorStatus, 4, b);

	    b.writeTo(out);
	} catch(IOException x) {
	    throw new SMPPException("Error writing SmeAddress_e packet to "
		    + "output stream");
	}
    }
}
