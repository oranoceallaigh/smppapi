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
package ie.omk.smpp.message;

import java.io.IOException;
import java.io.OutputStream;

import ie.omk.smpp.SMPPException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.util.SMPPIO;
import org.apache.log4j.Logger;

/** Parameter retrieve.
  * Gets the current value of a configurable parameter at the SMSC.
  * @author Oran Kelly
  * @version 1.0
  */
public class ParamRetrieve
    extends ie.omk.smpp.message.SMPPRequest
{
    /** Name of the parameter to retrieve */
    private String paramName;

    /** Construct a new ParamRetrieve.
      */
    public ParamRetrieve()
    {
	super(PARAM_RETRIEVE);
	paramName = null;
    }

    /** Construct a new ParamRetrieve with specified sequence number.
      * @param seqNum The sequence number to use
      * @deprecated
      */
    public ParamRetrieve(int seqNum)
    {
	super(PARAM_RETRIEVE, seqNum);
	paramName = null;
    }

    /** Read in a ParamRetrieve from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @throws java.io.IOException if there's an error reading from the
      * input stream.
      */
    /*public ParamRetrieve(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandId() != SMPPPacket.PARAM_RETRIEVE)
	    throw new BadCommandIDException(SMPPPacket.PARAM_RETRIEVE,
		    getCommandId());

	if (getCommandStatus() != 0)
	    return;

	paramName = SMPPIO.readCString(in);
    }*/

    /** Set the name of the parameter to retrieve
      * @param paramName Parameter name, up to 31 characters
      * @throws ie.omk.smpp.StringTooLongException if the parameter name is
      * too long.
      */
    public void setParamName(String paramName)
	throws StringTooLongException
    {
	if(paramName == null) {
	    this.paramName = null;
	    return;
	}

	if(paramName.length() < 32) {
	    this.paramName = paramName;
	} else {
	    throw new StringTooLongException(31);
	}
    }

    /** Get the parameter name */
    public String getParamName()
    {
	return (paramName);
    }


    public int getBodyLength()
    {
	int len = (((paramName != null) ? paramName.length() : 0));

	// 1 c-string
	return (len + 1);
    }

    /** Write a byte representation of this packet to an OutputStream
      * @param out The OutputStream to write to
      * @throws java.io.IOException if there's an error writing to the
      * output stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException
    {
	SMPPIO.writeCString(paramName, out);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	paramName = SMPPIO.readCString(body, offset);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("param_retrieve");
    }
}
