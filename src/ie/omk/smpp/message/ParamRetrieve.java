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
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Retrieve the value of a parameter from the SMSC
  * @author Oran Kelly
  * @version 1.0
  */
public class ParamRetrieve
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Name of the parameter to retrieve */
    String			paramName;

    /** Construct a new ParamRetrieve with specified sequence number.
     * @param seqNo The sequence number to use
     */
    public ParamRetrieve(int seqNo)
    {
	super(ESME_PARAM_RETRIEVE, seqNo);
	paramName = null;
    }

    /** Read in a ParamRetrieve from an InputStream.  A full packet,
     * including the header fields must exist in the stream.
     * @param in The InputStream to read from
     * @exception ie.omk.smpp.SMPPException If the stream does not
     * contain a ParamRetrieve packet.
     * @see java.io.InputStream
     */
    public ParamRetrieve(InputStream in)
    {
	super(in);

	if(cmdStatus != 0)
	    return;

	try {
	    paramName = readCString(in);
	} catch(IOException iox) {
	    throw new SMPPException("Input stream does not contain a "
		    + "pararetrieve packet.");
	}
    }

    /** Set the name of the parameter to retrieve
     * @param s Parameter name: Up to 31 characters
     * @exception ie.omk.smpp.SMPPException If the param name is invalid
     */
    public void setParamName(String s)
    {
	if(s == null)
	{ paramName = null; return; }

	if(s.length() < 32) {
	    paramName = new String(s);
	} else {
	    throw new SMPPException("Parameter name must be < 32 chars");
	}
    }

    /** Get the parameter name */
    public String getParamName()
    {
	return (paramName == null) ? null : new String(paramName);
    }


    /** Get the size in bytes of this packet */
    public int size()
    {
	return (super.size() + 1
		+ ((paramName != null) ? paramName.length() : 0));
    }

    /** Write a byte representation of this packet to an OutputStream
     * @param out The OutputStream to write to
     * @exception ie.omk.smpp.SMPPException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public void writeTo(OutputStream out)
    {
	try {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();

	    super.writeTo(b);
	    writeCString(paramName, b);

	    b.writeTo(out);
	} catch(IOException x) {
	    Debug.d(this, "writeTo", "Error writing packet to output",
		    Debug.DBG_1);
	    throw new SMPPException("Error writing ParamRetrieve packet to "
		    + "Output Stream");
	}
    }

    public String toString()
    {
	return new String("param_retrieve");
    }
}
