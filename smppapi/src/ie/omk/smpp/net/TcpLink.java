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
package ie.omk.smpp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

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
      * @throws java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(String address)
	throws java.net.UnknownHostException
    {
	this(address, DEFAULT_PORT);
    }

    /** Create a new TcpLink
      * @param address IP address or hostname of SMSC
      * @param port The port number to connect to
      * @throws java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(String address, int port)
	throws java.net.UnknownHostException
    {
	this.addr = InetAddress.getByName(address);
	if (port < 1)
	    this.port = DEFAULT_PORT;
	else
	    this.port = port;
    }

    /** Create a new TcpLink
      * @param address IP address SMSC
      * @throws java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(InetAddress address)
	throws java.net.UnknownHostException
    {
	this(address, DEFAULT_PORT);
    }

    /** Create a new TcpLink
      * @param address IP address of SMSC
      * @param port The port number to connect to
      * @throws java.net.UnknownHostException If the host is not found.
      */
    public TcpLink(InetAddress address, int port)
	throws java.net.UnknownHostException
    {
	this.addr = address;
	if (port < 1)
	    this.port = DEFAULT_PORT;
	else
	    this.port = port;
    }

    /** Create a new Socket connection to the SMSC. This implementation creates
     * a new instance of a java.net.Socket with the host name and port supplied
     * to the constructor an instance of this class was created with.
     * @throws java.io.IOException If an error occurs while creating the socket
     * connection to the SMSC.
     * @see java.net.Socket#Socket(java.net.InetAddress, int)
     */
    protected void implOpen()
	throws java.io.IOException
    {
	logger.info("Opening TCP socket to " + addr + ":" + port);
	sock = new Socket(addr, port);
	connected = true;
    }

    /** Close the Socket connection to the SMSC.
     * @throws java.io.IOException If an I/O error occurs closing the
     * socket connection.
     * @see java.net.Socket#close
     */
    protected void implClose()
	throws java.io.IOException
    {
	if (connected && sock != null) {
	    logger.info("Shutting down socket connection");
	    try {
		sock.close();
		sock = null;
		connected = false;
	    } catch(IOException ix) {
		logger.warn("I/O exception closing socket", ix);
		connected = false;
		ix.fillInStackTrace();
		throw ix;
	    }
	}
    }

    /** Get the output stream of the Socket connection to the SMSC.
      * @throws java.io.IOException If the socket connection is not open or an
      * I/O error occurs when creating the output stream.
      * @see java.io.OutputStream
      * @see java.net.Socket#getOutputStream
      */
    protected OutputStream getOutputStream()
	throws java.io.IOException
    {
	if(sock == null)
	    throw new IOException("Socket connection is not open");
	else
	    return sock.getOutputStream();
    }

    /** Get the input stream of the Socket connection to the SMSC.
      * @throws java.io.IOException If the socket connection is not open or an
      * I/O error occurs when creating the input stream.
      * @see java.io.InputStream
      * @see java.net.Socket#getInputStream
      */
    protected InputStream getInputStream()
	throws java.io.IOException
    {
	if(sock == null)
	    throw new IOException("Socket connection is not open");
	else
	    return sock.getInputStream();
    }

    /** Get the address we're connected (or connecting) to.
     * @return The address of the SMSC this link is connected to.
     */
    public InetAddress getAddress()
    {
	return (addr);
    }

    /** Get the service port to connect to at the SMSC to establish a TCP
     * connection.
     * @return The service port at the SMSC to connect to.
     */
    public int getPort()
    {
	return (port);
    }

    /** Get the port at the SMSC that this link is connected to. This is the
     * remote port that this link is connected to after a successful connection
     * has been made.
     * @return The remote port this link is connected to.
     * @throws java.io.IOException If the connection is not open.
     */
    public int getConnectedPort() throws java.io.IOException {
	if (sock == null)
	    throw new IOException("Socket connection is not open");
	else
	    return (sock.getPort());
    }

    /** Get the local port number this link is connected to.
     * @return The local port number this link is connected to.
     * @throws java.io.IOException If the connection is not open.
     */
    public int getLocalPort() throws java.io.IOException {
	if (sock == null)
	    throw new IOException("Socket connection is not open");
	else
	    return (sock.getLocalPort());
    }

    /** Check connection status.
      * @return false if unconnected, true if connected.
      */
    public boolean isConnected()
    {
	return (connected);
    }
    
    public void setTimeout(long timeout) {
        try {
            sock.setSoTimeout((int)timeout);
        } catch (Throwable t) {
            logger.error("Failed to set timeout on socket: " + t.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug("Stack trace:", t);
            }
        }
    }
    
    public long getTimeout() {
        try {
            return ((long)sock.getSoTimeout());
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("Stack trace:", t);
            }
        }
        
        return (-1L);
    }
}
