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
package ie.omk.smpp.message;

import java.io.*;
import ie.omk.smpp.message.SmeAddress;
import ie.omk.smpp.util.SMPPIO;
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
    private int			errorStatus;

    /** Construct a new SmeAddress_e */
    public SmeAddress_e()
    {
	super();
	errorStatus = 0;
    }

    /** Construct a new SmeAddress_e with the specified parameters.
      */
    public SmeAddress_e(int ton, int npi, String addr, int errorCode)
	throws ie.omk.smpp.SMPPException
    {
	super(ton, npi, addr);
	this.errorStatus = errorCode;
    }

    /** Read in an SmeAddress_e from an InputStream
      * @param in InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public SmeAddress_e(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);
	this.errorStatus = SMPPIO.readInt(in, 4);
    }

    /** Get the error status.
      */
    public int getErrorStatus()
    {
	return (this.errorStatus);
    }

    /** Set the error status for this SmeAddress.
      */
    public void setErrorStatus(int error)
    {
	this.errorStatus = error;
    }

    /** Return the number of bytes this address would be encoded as to an
      * OutputStream.
      * @return the number of bytes this object would encode as.
      */
    public int size()
    {
	// error status is a 4-byte integer.
	return (super.size() + 4);
    }

    /** Write a byte representation of this address to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    public void writeTo(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super.writeTo(out);
	SMPPIO.writeInt(errorStatus, 4, out);
    }
}
