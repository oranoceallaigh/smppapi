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

import ie.omk.smpp.Address;

import java.io.OutputStream;

/** Alert notification. This packet type is sent from the SMSC to an ESME to
 * signal that a particular mobile subscriber has become available and a
 * delivery pending flag had previously been set for that subscriber by a
 * <code>data_sm</code> packet.
 * <p>Note that there is no response packet to an
 * <code>alert_notification</code>.
 * @version 1.0
 * @author Oran Kelly
 */
public class AlertNotification extends SMPPRequest
{
    /** Create a new alert_notification object.
     */
    public AlertNotification()
    {
	super(ALERT_NOTIFICATION);
    }

    /** Create a new alert_notification object with sequence number
     * <code>seqNum</code>.
     */
    public AlertNotification(int seqNum)
    {
	super(ALERT_NOTIFICATION, seqNum);
    }

    public int getBodyLength()
    {
	return (((source != null) ? source.getLength() : 3)
		+ ((destination != null) ? destination.getLength() : 3));
    }

    public void encodeBody(OutputStream out)
	throws java.io.IOException
    {
	if (source != null)
	    source.writeTo(out);
	else
	    new Address().writeTo(out);

	if (destination != null)
	    destination.writeTo(out);
	else
	    new Address().writeTo(out);
    }

    public void readBodyFrom(byte[] body, int offset) throws SMPPProtocolException {
	source = new Address();
	source.readFrom(body, offset);
	offset += source.getLength();

	destination = new Address();
	destination.readFrom(body, offset);
    }

    public String toString()
    {
	return ("alert_notification");
    }
}
