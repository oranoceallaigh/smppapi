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

package ie.omk.smpp.event;

import java.util.Iterator;

import ie.omk.smpp.Connection;

import ie.omk.smpp.message.SMPPPacket;

/** This interface defines the observable side of the observer pattern for
 * asynchronous SMPP event notification. Each {@link ie.omk.smpp.Connection}
 * object will have an implementation of the <code>EventDispatcher</code>
 * interface which it uses to deliver events to interested listeners. By
 * removing the actual dispatching of events from the internals of the
 * Connection, applications may provide their own event dispatch implementations
 * for their <code>Connection</code> objects which better suit how those
 * applications work.
 * @author Oran Kelly
 * @see ie.omk.smpp.event.SimpleEventDispatcher
 */
public interface EventDispatcher {

    /** The name of the default logger to use for logging event info. If an
     * implementation wishes to log events to the same logger as other event
     * objects, call
     * <code>Logger.getLogger(EventDispatcher.DEFAULT_LOGGER_NAME)</code>.
     */
    public static final String DEFAULT_LOGGER_NAME = "ie.omk.smpp.event";

    /** Initialise the event dispatcher. The <code>init</code> method will be
     * called by the <code>Connection</code> before it makes any attempt to add
     * any observers or deliver any events via the dispatcher.
     */
    public void init();

    /** Event dispatcher clean up. The <code>destroy</code> method will be
     * called by the <code>Connection</code> when it is finished delivering
     * events to it and the receiver daemon thread is exiting. Any initialising
     * done in the <code>init</code> method can be cleaned up here.
     * <p>The <code>destroy</code> method <b>must not</b> interfere with the
     * delivery of any events notified to the event dispatcher before the call
     * to this method.</p>
     */
    public void destroy();

    /** Add an observer to this event dispatcher.
     * @param observer the observer object to add.
     */
    public void addObserver(ConnectionObserver observer);

    /** Remove an observer from this event dispatcher.
     * @param observer the observer object to remove from the registered
     * observers.
     */
    public void removeObserver(ConnectionObserver observer);

    /** Get an iterator over the currently registered observers.
     * @return an iterator object which iterates over all registered observers.
     */
    public Iterator observerIterator();

    /** Notify all registered observers of an SMPP event.
     * @param event the SMPP event to notify observers of.
     */
    public void notifyObservers(Connection conn, SMPPEvent event);

    /** Notify all registered observers of a received SMPP packet.
     * @param packet the SMPP packet to notify observers of.
     */
    public void notifyObservers(Connection conn, SMPPPacket packet);
}
