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
package ie.omk.smpp;

/** UnsupportedSMPPVersionException
  * @author Oran Kelly
  * @version 1.0
  */
public class UnsupportedSMPPVersionException
    extends ie.omk.smpp.SMPPException
{
    private int version = 0;

    /** Construct a new UnsupportedSMPPVersion exception, specifying the version
     * number that caused the exception.
     * @param version the version number that caused the exception.
     */
    public UnsupportedSMPPVersionException(int version)
    {
	this.version = version;
    }

    /** Construct a new UnsupportedSMPPVersion exception, specifying the version
     * number that caused the exception.
     * @param version the version number that caused the exception.
     * @param msg the exception message.
     */
    public UnsupportedSMPPVersionException(int version, String msg)
    {
	super(msg);
	this.version = version;
    }

    /** Get the version number that caused the exception.
     */
    public int getVersion()
    {
	return (this.version);
    }
}
