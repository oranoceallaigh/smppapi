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
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** Returns the value of a requested parameter
  * @author Oran Kelly
  * @version 1.0
  */
public class ParamRetrieveResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** String value of the requested parameter */
    String				paramValue;

    /** Construct a new BindReceiverResp with specified sequence number.
      * @param seqNum The sequence number to use
      */
    public ParamRetrieveResp(int seqNum)
    {
	super(ESME_PARAM_RETRIEVE_RESP, seqNum);
	paramValue = null;
    }

    /** Read in a BindReceiverResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception ie.omk.smpp.SMPPException If the stream does not
      * contain a ParamRetrieveResp packet.
      * @see java.io.InputStream
      */
    public ParamRetrieveResp(InputStream in)
    {
	super(in);

	if(commandStatus != 0)
	    return;

	try {
	    paramValue = SMPPIO.readCString(in);
	} catch(IOException x) {
	    throw new SMPPException("Input stream does not contain a "
		    + "pararetrieve_resp packet.");
	}
    }

    /** Create a new ParamRetrieveResp packet in response to a BindReceiver.
      * This constructor will set the sequence number to it's expected value.
      * @param r The Request packet the response is to
      */
    public ParamRetrieveResp(ParamRetrieve r)
    {
	super(r);
    }

    /** Set the parameter value.
      * @param v Value to be returned for the requested parameter (Up to 100 characters)
      * @exception ie.omk.smpp.SMPPException If the value is invalid
      */
    public void setParamValue(String v)
    {
	if(v == null)
	{ paramValue = null; return; }

	if(v.length() < 101)
	    paramValue = new String(v);
	else
	    throw new SMPPException("Paramater value must be < 101 chars");
    }

    /** Get the value of the parameter */
    public String getParamValue()
    {
	return (paramValue == null) ? null : new String(paramValue);
    }


    /** Get the size in bytes of this packet */
    public int getCommandLen()
    {
	return (getHeaderLen() + 1
		+ ((paramValue != null) ? paramValue.length() : 0));
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception ie.omk.smpp.SMPPException If an I/O error occurs
      * @see java.io.OutputStream
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(paramValue, out);
    }

    public String toString()
    {
	return new String("param_retrieve_resp");
    }
}
