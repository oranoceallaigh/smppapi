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

import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;

import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.GenericNack;
import ie.omk.smpp.message.ParamRetrieve;
import ie.omk.smpp.message.ParamRetrieveResp;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import ie.omk.smpp.SmppConnection;

import ie.omk.debug.Debug;

/** Base class for applications to extend for SMPP event handling.
 * This class handles incoming events and provides a set of blank handler
 * methods for the various events. This makes it easy for an application to
 * extend this class and only override the methods it's interested in.
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class SMPPEventAdapter implements ConnectionObserver
{
    /** Default constructor.
     */
    public SMPPEventAdapter()
    {
    }

    
    public final void update(SmppConnection source, SMPPEvent event)
    {
	try {
	    switch (event.getType()) {
		case SMPPEvent.RECEIVER_START:
		    receiverStart(source, (ReceiverStartEvent)event);
		    break;

		case SMPPEvent.RECEIVER_EXIT:
		    ReceiverExitEvent ree = (ReceiverExitEvent)event;
		    if (ree.isException())
			receiverExitException(source, ree);
		    else
			receiverExit(source, ree);
		    break;

		case SMPPEvent.RECEIVER_EXCEPTION:
		    receiverException(source, (ReceiverExceptionEvent)event);
		    break;

		default:
		    userEvent(source, event);
	    }
	} catch (ClassCastException x) {
	    Debug.warn(this, "update", "Class cast exception\n"
		    + x.toString());
	}
    }

    public final void packetReceived(SmppConnection source, SMPPPacket pak)
    {
	// Keep high-incidence packet types at the top of this switch.
	switch (pak.getCommandId()) {
	case SMPPPacket.SMSC_DELIVER_SM:
	    deliverSM(source, (DeliverSM)pak);
	    break;

	case SMPPPacket.ESME_SUB_SM_RESP:
	    submitSMResponse(source, (SubmitSMResp)pak);
	    break;

	case SMPPPacket.ESME_SUB_MULTI_RESP:
	    submitMultiResponse(source, (SubmitMultiResp)pak);
	    break;

	case SMPPPacket.ESME_CANCEL_SM_RESP:
	    cancelSMResponse(source, (CancelSMResp)pak);
	    break;

	case SMPPPacket.ESME_REPLACE_SM_RESP:
	    replaceSMResponse(source, (ReplaceSMResp)pak);
	    break;

	case SMPPPacket.ESME_PARAM_RETRIEVE_RESP:
	    paramRetrieveResponse(source, (ParamRetrieveResp)pak);
	    break;

	case SMPPPacket.ESME_QUERY_SM_RESP:
	case SMPPPacket.ESME_QUERY_LAST_MSGS_RESP:
	case SMPPPacket.ESME_QUERY_MSG_DETAILS_RESP:
	    queryResponse(source, (SMPPResponse)pak);
	    break;

	case SMPPPacket.ESME_QRYLINK:
	    queryLink(source, (EnquireLink)pak);
	    break;

	case SMPPPacket.ESME_QRYLINK_RESP:
	    queryLinkResponse(source, (EnquireLinkResp)pak);
	    break;

	case SMPPPacket.ESME_UBD:
	    unbind(source, (Unbind)pak);
	    break;

	case SMPPPacket.ESME_UBD_RESP:
	    unbindResponse(source, (UnbindResp)pak);
	    break;

	case SMPPPacket.ESME_BNDTRN_RESP:
	case SMPPPacket.ESME_BNDRCV_RESP:
	    bindResponse(source, (BindResp)pak);
	    break;

	case SMPPPacket.ESME_NACK:
	    genericNack(source, (GenericNack)pak);
	    break;

	default:
	    unidentified(source, pak);
	    break;
	}
    }

    /** Receiver thread exited normally. This method is called by the event
     * adapter when it receives a receiver exit event from the API that does not
     * contain an exception. This normally means that either the SMSC has
     * requested an unbind and the response has been successfully sent or that
     * an unbind request has been successfully acknowledged.
     * @param source the source connection of the event.
     * @param rev the receiver exit event object received from the API.
     */
    public void receiverExit(SmppConnection source, ReceiverExitEvent rev)
    {
	// default: do nothing
    }


    /** Receiver thread exited due to fatal exception. This method is called by
     * the event adapter when the receiver thread has exited due to an exception
     * it caught. Handling code for this method should assume that the SMPP link
     * to the SMSC and probably the network-specific connection has been lost.
     * It should do any clean up and either exit gracefully or re-establish the
     * network connection and re-bind to the SMSC.
     * @param source the source connection of the event.
     * @param rev the receiver exit event object received from the API.
     */
    public void receiverExitException(SmppConnection source,
	    ReceiverExitEvent rev)
    {
	// default: do nothing
    }

    /** Receiver thread caught a non-fatal exception. This method is called when
     * an exception has been caught by the receiver thread but it is not exiting
     * due to it. If this method gets called it may be indicitive of a network
     * communications error or possibly an unidentified incoming packet type
     * from the SMSC.
     * @param source the source connection of the event.
     * @param rev the receiver exception event received from the API, which
     * contains the caught exception.
     */
    public void receiverException(SmppConnection source,
	    ReceiverExceptionEvent rev)
    {
	// default: do nothing
    }

    /** Receiver thread has started up. This method is called when the receiver
     * thread starts. Most applications will ignore this event and just use the
     * default (blank) implementation of this method.
     * @param source the source connection of the event.
     * @param rs the receiver start event received from the API.
     */
    public void receiverStart(SmppConnection source, ReceiverStartEvent rs)
    {
	// default: do nothing
    }

    /** PLACEHOLDER. This method will currently never be called.
     */
    public void userEvent(SmppConnection source, SMPPEvent ev)
    {
	// default: do nothing
    }
    

    /** DeliverSM packet received from the SMSC.
     */
    public void deliverSM(SmppConnection source, DeliverSM dm)
    {
    }
    
    /** SubmitSM response packet received from the SMSC.
     */
    public void submitSMResponse(SmppConnection source, SubmitSMResp smr)
    {
    }

    /** SubmitMulti response packet received from the SMSC.
     */
    public void submitMultiResponse(SmppConnection source, SubmitMultiResp smr)
    {
    }
    
    /** CancelSM response packet received from the SMSC.
     */
    public void cancelSMResponse(SmppConnection source, CancelSMResp cmr)
    {
    }
    
    /** ReplaceSM response packet received from the SMSC.
     */
    public void replaceSMResponse(SmppConnection source, ReplaceSMResp rmr)
    {
    }

    /** ParamRetrieve response packet received from the SMSC.
     */
    public void paramRetrieveResponse(SmppConnection source,
	    ParamRetrieveResp prr)
    {
    }
    
    /** One of a QuerySM, QueryLastMsgs or QueryMsgDetails response packet has
     * been received from the SMSC.
     */
    public void queryResponse(SmppConnection source, SMPPResponse qr)
    {
    }
    
    /** EnquireLink packet received from the SMSC.
     */
    public void queryLink(SmppConnection source, EnquireLink el)
    {
    }
    
    /** EnquireLink response packet received from the SMSC.
     */
    public void queryLinkResponse(SmppConnection source, EnquireLinkResp elr)
    {
    }
    
    /** Unbind packet received from the SMSC.
     */
    public void unbind(SmppConnection source, Unbind ubd)
    {
    }
    
    /** Unbind response packet received from the SMSC.
     */
    public void unbindResponse(SmppConnection source, UnbindResp ubr)
    {
    }
    
    /** Bind response packet received from the SMSC.
     */
    public void bindResponse(SmppConnection source, BindResp br)
    {
    }
    
    /** GenericNack packet received from the SMSC.
     */
    public void genericNack(SmppConnection source, GenericNack nack)
    {
    }
    
    /** An unidentified packet has been received from the SMSC.
     */
    public void unidentified(SmppConnection source, SMPPPacket pak)
    {
    }
}
