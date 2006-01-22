package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.GenericNack;
import ie.omk.smpp.message.ParamRetrieveResp;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import org.apache.commons.logging.LogFactory;

/**
 * Base class for applications to extend for SMPP event handling. This class
 * handles incoming events and provides a set of blank handler methods for the
 * various events. This makes it easy for an application to extend this class
 * and only override the methods it's interested in.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public abstract class SMPPEventAdapter implements ConnectionObserver {
    /**
     * Default constructor.
     */
    public SMPPEventAdapter() {
    }

    public final void update(Connection source, SMPPEvent event) {
        try {
            switch (event.getType()) {
            case SMPPEvent.RECEIVER_START:
                receiverStart(source, (ReceiverStartEvent) event);
                break;

            case SMPPEvent.RECEIVER_EXIT:
                ReceiverExitEvent ree = (ReceiverExitEvent) event;
                if (ree.getReason() == ReceiverExitEvent.EXCEPTION) {
                    receiverExitException(source, ree);
                } else {
                    receiverExit(source, ree);
                }
                break;

            case SMPPEvent.RECEIVER_EXCEPTION:
                receiverException(source, (ReceiverExceptionEvent) event);
                break;

            default:
                userEvent(source, event);
            }
        } catch (ClassCastException x) {
            LogFactory.getLog(SMPPEventAdapter.class).error(
                    "Class cast exception", x);
        }
    }

    public final void packetReceived(Connection source, SMPPPacket pak) {
        // Keep high-incidence packet types at the top of this switch.
        switch (pak.getCommandId()) {
        case SMPPPacket.DELIVER_SM:
            deliverSM(source, (DeliverSM) pak);
            break;

        case SMPPPacket.SUBMIT_SM_RESP:
            submitSMResponse(source, (SubmitSMResp) pak);
            break;

        case SMPPPacket.SUBMIT_MULTI_RESP:
            submitMultiResponse(source, (SubmitMultiResp) pak);
            break;

        case SMPPPacket.CANCEL_SM_RESP:
            cancelSMResponse(source, (CancelSMResp) pak);
            break;

        case SMPPPacket.REPLACE_SM_RESP:
            replaceSMResponse(source, (ReplaceSMResp) pak);
            break;

        case SMPPPacket.PARAM_RETRIEVE_RESP:
            paramRetrieveResponse(source, (ParamRetrieveResp) pak);
            break;

        case SMPPPacket.QUERY_SM_RESP:
        case SMPPPacket.QUERY_LAST_MSGS_RESP:
        case SMPPPacket.QUERY_MSG_DETAILS_RESP:
            queryResponse(source, (SMPPResponse) pak);
            break;

        case SMPPPacket.ENQUIRE_LINK:
            queryLink(source, (EnquireLink) pak);
            break;

        case SMPPPacket.ENQUIRE_LINK_RESP:
            queryLinkResponse(source, (EnquireLinkResp) pak);
            break;

        case SMPPPacket.UNBIND:
            unbind(source, (Unbind) pak);
            break;

        case SMPPPacket.UNBIND_RESP:
            unbindResponse(source, (UnbindResp) pak);
            break;

        case SMPPPacket.BIND_TRANSMITTER_RESP:
        case SMPPPacket.BIND_TRANSCEIVER_RESP:
        case SMPPPacket.BIND_RECEIVER_RESP:
            bindResponse(source, (BindResp) pak);
            break;

        case SMPPPacket.GENERIC_NACK:
            genericNack(source, (GenericNack) pak);
            break;

        default:
            unidentified(source, pak);
            break;
        }
    }

    /**
     * Receiver thread exited normally. This method is called by the event
     * adapter when it receives a receiver exit event from the API that does not
     * contain an exception. This normally means that either the SMSC has
     * requested an unbind and the response has been successfully sent or that
     * an unbind request has been successfully acknowledged.
     * 
     * @param source
     *            the source connection of the event.
     * @param rev
     *            the receiver exit event object received from the API.
     */
    public void receiverExit(Connection source, ReceiverExitEvent rev) {
        // default: do nothing
    }

    /**
     * Receiver thread exited due to fatal exception. This method is called by
     * the event adapter when the receiver thread has exited due to an exception
     * it caught. Handling code for this method should assume that the SMPP link
     * to the SMSC and probably the network-specific connection has been lost.
     * It should do any clean up and either exit gracefully or re-establish the
     * network connection and re-bind to the SMSC.
     * 
     * @param source
     *            the source connection of the event.
     * @param rev
     *            the receiver exit event object received from the API.
     */
    public void receiverExitException(Connection source, ReceiverExitEvent rev) {
        // default: do nothing
    }

    /**
     * Receiver thread caught a non-fatal exception. This method is called when
     * an exception has been caught by the receiver thread but it is not exiting
     * due to it. If this method gets called it may be indicitive of a network
     * communications error or possibly an unidentified incoming packet type
     * from the SMSC.
     * 
     * @param source
     *            the source connection of the event.
     * @param rev
     *            the receiver exception event received from the API, which
     *            contains the caught exception.
     */
    public void receiverException(Connection source, ReceiverExceptionEvent rev) {
        // default: do nothing
    }

    /**
     * Receiver thread has started up. This method is called when the receiver
     * thread starts. Most applications will ignore this event and just use the
     * default (blank) implementation of this method.
     * 
     * @param source
     *            the source connection of the event.
     * @param rs
     *            the receiver start event received from the API.
     */
    public void receiverStart(Connection source, ReceiverStartEvent rs) {
        // default: do nothing
    }

    /**
     * PLACEHOLDER. This method will currently never be called.
     */
    public void userEvent(Connection source, SMPPEvent ev) {
        // default: do nothing
    }

    /**
     * DeliverSM packet received from the SMSC.
     */
    public void deliverSM(Connection source, DeliverSM dm) {
    }

    /**
     * SubmitSM response packet received from the SMSC.
     */
    public void submitSMResponse(Connection source, SubmitSMResp smr) {
    }

    /**
     * SubmitMulti response packet received from the SMSC.
     */
    public void submitMultiResponse(Connection source, SubmitMultiResp smr) {
    }

    /**
     * CancelSM response packet received from the SMSC.
     */
    public void cancelSMResponse(Connection source, CancelSMResp cmr) {
    }

    /**
     * ReplaceSM response packet received from the SMSC.
     */
    public void replaceSMResponse(Connection source, ReplaceSMResp rmr) {
    }

    /**
     * ParamRetrieve response packet received from the SMSC.
     */
    public void paramRetrieveResponse(Connection source, ParamRetrieveResp prr) {
    }

    /**
     * One of a QuerySM, QueryLastMsgs or QueryMsgDetails response packet has
     * been received from the SMSC.
     */
    public void queryResponse(Connection source, SMPPResponse qr) {
    }

    /**
     * EnquireLink packet received from the SMSC.
     */
    public void queryLink(Connection source, EnquireLink el) {
    }

    /**
     * EnquireLink response packet received from the SMSC.
     */
    public void queryLinkResponse(Connection source, EnquireLinkResp elr) {
    }

    /**
     * Unbind packet received from the SMSC.
     */
    public void unbind(Connection source, Unbind ubd) {
    }

    /**
     * Unbind response packet received from the SMSC.
     */
    public void unbindResponse(Connection source, UnbindResp ubr) {
    }

    /**
     * Bind response packet received from the SMSC.
     */
    public void bindResponse(Connection source, BindResp br) {
    }

    /**
     * GenericNack packet received from the SMSC.
     */
    public void genericNack(Connection source, GenericNack nack) {
    }

    /**
     * An unidentified packet has been received from the SMSC.
     */
    public void unidentified(Connection source, SMPPPacket pak) {
    }
}

