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

import ie.omk.smpp.Connection;

/** Event generated by the receiver thread when a non-fatal exception is caught.
 * An application will receive this event type if the receiver thread catches an
 * exception which does not cause it to terminate. The exception which was
 * caught and the state the connection was in when it was caught are saved in
 * this event.
 * @author Oran Kelly
 * @version 1.0
 */
public class ReceiverExceptionEvent extends SMPPEvent
{
    /** The exception that was caught. */
    private Throwable exception = null;

    /** The state the Connection was in when the exception was caught. */
    private int connectionState = 0;


    /** Create a new ReceiverExceptionEvent.
     * @param source the source Connection of this event.
     * @param t the exception being reported.
     */
    public ReceiverExceptionEvent(Connection source, Throwable t)
    {
	super (RECEIVER_EXCEPTION, source);
	this.exception = t;
    }

    /** Create a new ReceiverExceptionEvent.
     * @param source the source Connection of this event.
     * @param t the exception being reported.
     * @param state the state the connection was in when the exception was
     * caught.
     */
    public ReceiverExceptionEvent(Connection source, Throwable t, int state)
    {
	super (RECEIVER_EXCEPTION, source);
	this.exception = t;
	this.connectionState = state;
    }

    /** Get the exception which was caught.
     */
    public Throwable getException()
    {
	return (exception);
    }

    /** Get the state the connection was in when the exception was caught.
     * @return the integer value representing the state of the connection.
     * @see ie.omk.smpp.Connection#BOUND
     * @see ie.omk.smpp.Connection#UNBOUND
     * @see ie.omk.smpp.Connection#BINDING
     * @see ie.omk.smpp.Connection#UNBINDING
     */
    public int getState()
    {
	return (connectionState);
    }
}
