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

import java.net.*;
import java.io.*;
import ie.omk.debug.Debug;

/** Implementation of an Smsc link over the tcp/ip protocol
  * @author Oran Kelly
  * @version 1.0
  */
public class TcpLink
    extends ie.omk.smpp.net.SmscLink
{
    /** Default IP port to use if none are specified */
    public static final int DEFAULT_PORT = 5016;

    /** The ip address of the SMSC */
    private InetAddress addr = null;

    /** The ip port to connect to */
    private int port = 0;

    /** The socket corresponding to the virtual connection */
    private Socket sock = null;

    /** Are we connected? */
    private boolean connected = false;


    /** Create a new TcpLink
      * @param address IP address or hostname of SMSC
      * @exception java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(String address)
	throws java.net.UnknownHostException
    {
	this(address, DEFAULT_PORT);
    }

    /** Create a new TcpLink
      * @param address IP address or hostname of SMSC
      * @param port The port number to connect to
      * @exception java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(String address, int port)
	throws java.net.UnknownHostException
    {
	this.addr = InetAddress.getByName(address);
	this.port = port;
    }

    /** Create a new TcpLink
      * @param address IP address SMSC
      * @exception java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(InetAddress address)
	throws java.net.UnknownHostException
    {
	this(address, DEFAULT_PORT);
    }

    /** Create a new TcpLink
      * @param address IP address of SMSC
      * @param port The port number to connect to
      * @exception java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(InetAddress address, int port)
	throws java.net.UnknownHostException
    {
	this.addr = address;
	this.port = port;
    }

    /** Create a new TcpLink using an already connected Socket.
      * The network connection is assumed to be established already.
      * @param sock The socket connection to use.
      */
    public TcpLink(Socket sock)
    {
	this.connected = true;
	this.sock = sock;
    }

    /** Connect the input and output sockets to the SMSC to create the
      * virtual circuit.
      * @exception java.io.IOException If an error occurs connecting to the SMSC
      */
    public synchronized void open()
	throws java.io.IOException
    {
	if (!connected) {
	    sock = new Socket(addr, port);
	    connected = true;
	}
    }

    /** Close the virtual circuit to the SMSC
      * @exception java.io.IOException If a network error occurs closing the connection
      */
    public synchronized void close()
	throws java.io.IOException
    {
	if (connected && sock != null) {
	    try {
		sock.close();
		sock = null;
		connected = false;
	    } catch(IOException ix) {
		connected = false;
		ix.fillInStackTrace();
		throw ix;
	    }
	}
    }

    /** Get the output stream of the output socket of the virtual connection.
      * @exception java.io.IOException If the socket connections are not open
      * @see java.io.OutputStream
      * @see java.net.Socket#getOutputStream
      */
    public OutputStream getOutputStream()
	throws java.io.IOException
    {
	if(sock == null)
	    throw new IOException("Socket connection is not open");
	else
	    return sock.getOutputStream();
    }

    /** Get the input stream of the input socket of the virtual connection.
      * @exception java.io.IOException If the socket connections are not open
      * @see java.io.InputStream
      * @see java.net.Socket#getInputStream
      */
    public InputStream getInputStream()
	throws java.io.IOException
    {
	if(sock == null)
	    throw new IOException("Socket connection is not open");
	else
	    return sock.getInputStream();
    }

    /** Get the address we're connected (or connecting) to.
      */
    public InetAddress getAddress()
    {
	return (addr);
    }

    /** Get the port we're connected (or connecting) to.
      */
    public int getPort()
    {
	return (port);
    }

    /** Check connection status.
      * @return false if unconnected, true if connected.
      */
    public boolean isConnected()
    {
	return (connected);
    }
}