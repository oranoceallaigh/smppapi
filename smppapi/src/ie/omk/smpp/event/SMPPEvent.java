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

package ie.omk.smpp.event;

import ie.omk.smpp.SmppConnection;

/** Abstract super class of SMPP control events.
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class SMPPEvent
{
    /** ReceiverStartEvent enumeration type. */
    public static final int RECEIVER_START = 2;

    /** ReceiverExitEvent enumeration type. */
    public static final int RECEIVER_EXIT = 3;

    /** ReceiverExceptionEvent enumeration type. */
    public static final int RECEIVER_EXCEPTION = 4;


    /** The source SmppConnection of this event. */
    private SmppConnection source = null;

    /** The type of this event. */
    private int type = 0;


    /** Construct a new event. The <code>type</code> parameter should match one
     * of the enumeration constants defined in this class.
     */
    protected SMPPEvent(int type, SmppConnection source)
    {
	this.source = source;
	this.type = type;
    }

    /** Get the source connection of this event.
     */
    public SmppConnection getSource()
    {
	return (source);
    }

    /** Get the enumeration type of this event.
     * @see #RECEIVER_EXIT
     * @see #RECEIVER_EXCEPTION
     */
    public int getType()
    {
	return (type);
    }
}
