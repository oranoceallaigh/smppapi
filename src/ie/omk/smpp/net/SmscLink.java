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
package ie.omk.smpp.net;

import java.net.InetAddress;
import java.io.*;
import java.net.*;
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** Abstract super class of all classes that implement a network link
  * to the SMSC.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class SmscLink
{
    /** Tells whether the connection is open or not */
    protected boolean connected = false;

    /** Open the connection to the SMSC.
      * @exception java.io.IOException If a communication error occurs
      */
    public abstract void open()
	throws IOException;

    /** Close the connection to the SMSC.
      * @exception java.io.IOException If a communication error occurs
      */
    public abstract void close()
	throws IOException;

    /** Get the output stream of the virtual circuit.
      * @exception java.io.IOException If a communication error occurs
      */
    public abstract OutputStream getOutputStream()
	throws IOException;

    /** Get the input stream of the virtual circuit
      * @exception java.io.IOException If a communication error occurs
      */
    public abstract InputStream getInputStream()
	throws IOException;

    /** Check whether or not the connection to the SMSC is open
      */
    public boolean isConnected()
    {
	return connected;
    }
}
