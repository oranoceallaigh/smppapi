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
package ie.omk.smpp;

import ie.omk.smpp.message.SMPPPacket;

/** Class representing an SMPP protocol version. Instances of this object are
 * used by the rest of the API to determine is an SMPP message is supported by a
 * certain version of the protocol.
 * @since 0.3.0
 * @author Oran Kelly
 */
public class SMPPVersion
{
    /** Integer representing version 3.3 of the API.
     * This value is defined in the specification.
     */
    public static final int V33_ID = 0x33;

    /** Integer representing version 3.4 of the API.
     * This value is defined in the specification.
     */
    public static final int V34_ID = 0x34;

    /** Static SMPPVersion instance representing version SMPP v3.3. */
    public static final SMPPVersion V33 = new SMPPVersion(V33_ID, "v3.3");

    /** Static SMPPVersion instance representing version SMPP v3.4. */
    public static final SMPPVersion V34 = new SMPPVersion(V34_ID, "v3.4");
    
    /** Integer representing this version number. The SMPP specification states
     * integer values that represent protocol revisions. These values are used
     * mainly in the bind_* and bind response messages. Integer value 0x33
     * represents version 3.3 of the protocol, integer value 0x34 represents
     * version 3.4...it's assumed further major and minor revisions of the SMPP
     * specification will continue this numbering scheme.
     */
    private int versionID = 0;

    /** Descriptive text for this protocol version. This value is used only to
     * return a representative string from toString.
     */
    private String versionString = null;

    /** Create a new SMPPVersion object.
     */
    private SMPPVersion(int versionID, String versionString)
    {
	this.versionID = versionID;
	this.versionString = versionString;
    }

    /** Get the integer value for this protocol version object.
     */
    public int getVersionID()
    {
	return (versionID);
    }

    /** Check if a version is older than this one.
     */
    public boolean isOlder(SMPPVersion ver)
    {
	return (ver.versionID < this.versionID);
    }

    /** Check if a version is newer than or equal to this one.
     */
    public boolean isNewer(SMPPVersion ver)
    {
	return (ver.versionID >= this.versionID);
    }

    /** Determine if a particular command is supported by this protocol version.
     * This method takes any valid SMPP command ID (for both requests and
     * responses) and returns true or false based on whether the protocol
     * version this object represents supports that command or not.
     * @param commandID the SMPP command ID to check support for.
     */
    public boolean isSupported(int commandID)
    {
	switch (versionID) {
	case V33_ID:
	    return (v33_isSupported(commandID));

	case V34_ID:
	    return (v34_isSupported(commandID));

	default:
	    return (false);
	}
    }

    private boolean v33_isSupported(int commandID)
    {
	// Turn off the msb, which is used to signify a response packet..
	commandID &= 0x7fffffff;

	switch (commandID) {
	case SMPPPacket.BIND_TRANSCEIVER:
	case SMPPPacket.DATA_SM:
	case SMPPPacket.ALERT_NOTIFICATION:
	    return (false);

	default:
	    return (true);
	}
    }

    private boolean v34_isSupported(int commandID)
    {
	// Turn off the msb, which is used to signify a response packet..
	commandID &= 0x7fffffff;

	switch (commandID) {
	case SMPPPacket.QUERY_LAST_MSGS:
	case SMPPPacket.QUERY_MSG_DETAILS:
	    return (false);

	default:
	    return (true);
	}
    }

    /** Return a descriptive string of this protocol version.
     */
    public String toString()
    {
	return (versionString);
    }
}
