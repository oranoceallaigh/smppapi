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
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.smpp.message;

import java.io.*;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.debug.Debug;

/** SMSC response to a ParamRetrieve request.
  * Returns the value of the requested parameter.
  * @author Oran Kelly
  * @version 1.0
  */
public class ParamRetrieveResp
    extends ie.omk.smpp.message.SMPPResponse
{
    /** String value of the requested parameter */
    private String paramValue;

    /** Construct a new BindReceiverResp.
      */
    public ParamRetrieveResp()
    {
	super(ESME_PARAM_RETRIEVE_RESP);
	paramValue = null;
    }

    /** Construct a new BindReceiverResp with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public ParamRetrieveResp(int seqNum)
    {
	super(ESME_PARAM_RETRIEVE_RESP, seqNum);
	paramValue = null;
    }

    /** Read in a BindReceiverResp from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @exception java.io.IOException if there's an error reading from the
      * input stream.
      */
    public ParamRetrieveResp(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.ESME_PARAM_RETRIEVE_RESP)
	    throw new BadCommandIDException(SMPPPacket.ESME_PARAM_RETRIEVE_RESP,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	paramValue = SMPPIO.readCString(in);
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
      * @param v Value to be returned for the requested parameter (Up to 100
      * characters)
      * @exception ie.omk.smpp.StringTooLongException if the parameter value is
      * too long.
      */
    public void setParamValue(String v)
	throws ie.omk.smpp.SMPPException
    {
	if(v == null) {
	    paramValue = null;
	    return;
	}

	if(v.length() < 101)
	    this.paramValue = v;
	else
	    throw new StringTooLongException(100);
    }

    /** Get the value of the parameter */
    public String getParamValue()
    {
	return (paramValue);
    }


    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getCommandLen()
    {
	int len = (getHeaderLen()
		+ ((paramValue != null) ? paramValue.length() : 0));

	// 1 c-string
	return (len + 1);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @exception java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	SMPPIO.writeCString(paramValue, out);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("param_retrieve_resp");
    }
}
