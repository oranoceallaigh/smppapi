package com.adenki.smpp.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.Session;
import com.adenki.smpp.message.BindResp;
import com.adenki.smpp.message.CancelSMResp;
import com.adenki.smpp.message.DeliverSM;
import com.adenki.smpp.message.EnquireLink;
import com.adenki.smpp.message.EnquireLinkResp;
import com.adenki.smpp.message.GenericNack;
import com.adenki.smpp.message.ParamRetrieveResp;
import com.adenki.smpp.message.QueryLastMsgsResp;
import com.adenki.smpp.message.QueryMsgDetailsResp;
import com.adenki.smpp.message.QuerySMResp;
import com.adenki.smpp.message.ReplaceSMResp;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.SubmitMultiResp;
import com.adenki.smpp.message.SubmitSMResp;
import com.adenki.smpp.message.Unbind;
import com.adenki.smpp.message.UnbindResp;

/**
 * Base class for applications to extend for SMPP event handling. This class
 * handles incoming events and provides a set of blank handler methods for the
 * various events. This makes it easy for an application to extend this class
 * and only override the methods it's interested in.
 * 
 * @version $Id$
 */
public abstract class SMPPEventAdapter implements SessionObserver {
    private static final Map<Class<? extends SMPPPacket>, Method> HANDLERS =
        new HashMap<Class<? extends SMPPPacket>, Method>();

    static {
        initHandlers();
    }
    
    /**
     * Default constructor.
     */
    public SMPPEventAdapter() {
    }

    public final void update(Session source, SMPPEvent event) {
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
            LoggerFactory.getLogger(SMPPEventAdapter.class).error(
                    "Class cast exception", x);
        }
    }

    public final void packetReceived(Session source, SMPPPacket pak) {
        try {
            Method handler = HANDLERS.get(pak.getClass());
            if (handler != null) {
                Object[] args = new Object[] {
                        source,
                        pak,
                };
                handler.invoke(this, args);
            } else {
                unidentified(source, pak);
            }
        } catch (Exception e) {
            throw new SMPPRuntimeException(
                    "Exception calling packet handler", e);
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
    public void receiverExit(Session source, ReceiverExitEvent rev) {
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
    public void receiverExitException(Session source, ReceiverExitEvent rev) {
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
    public void receiverException(Session source, ReceiverExceptionEvent rev) {
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
    public void receiverStart(Session source, ReceiverStartEvent rs) {
        // default: do nothing
    }

    /**
     * PLACEHOLDER. This method will currently never be called.
     */
    public void userEvent(Session source, SMPPEvent ev) {
        // default: do nothing
    }

    /**
     * DeliverSM packet received from the SMSC.
     */
    public void deliverSM(Session source, DeliverSM dm) {
    }

    /**
     * SubmitSM response packet received from the SMSC.
     */
    public void submitSMResponse(Session source, SubmitSMResp smr) {
    }

    /**
     * SubmitMulti response packet received from the SMSC.
     */
    public void submitMultiResponse(Session source, SubmitMultiResp smr) {
    }

    /**
     * CancelSM response packet received from the SMSC.
     */
    public void cancelSMResponse(Session source, CancelSMResp cmr) {
    }

    /**
     * ReplaceSM response packet received from the SMSC.
     */
    public void replaceSMResponse(Session source, ReplaceSMResp rmr) {
    }

    /**
     * ParamRetrieve response packet received from the SMSC.
     */
    public void paramRetrieveResponse(Session source, ParamRetrieveResp prr) {
    }

    /**
     * One of a QuerySM, QueryLastMsgs or QueryMsgDetails response packet has
     * been received from the SMSC.
     */
    public void queryResponse(Session source, SMPPPacket qr) {
    }

    /**
     * EnquireLink packet received from the SMSC.
     */
    public void queryLink(Session source, EnquireLink el) {
    }

    /**
     * EnquireLink response packet received from the SMSC.
     */
    public void queryLinkResponse(Session source, EnquireLinkResp elr) {
    }

    /**
     * Unbind packet received from the SMSC.
     */
    public void unbind(Session source, Unbind ubd) {
    }

    /**
     * Unbind response packet received from the SMSC.
     */
    public void unbindResponse(Session source, UnbindResp ubr) {
    }

    /**
     * Bind response packet received from the SMSC.
     */
    public void bindResponse(Session source, BindResp br) {
    }

    /**
     * GenericNack packet received from the SMSC.
     */
    public void genericNack(Session source, GenericNack nack) {
    }

    /**
     * An unidentified packet has been received from the SMSC.
     */
    public void unidentified(Session source, SMPPPacket pak) {
    }
    
    private static void initHandlers() {
        try {
            addHandler(DeliverSM.class, "deliverSM");
            addHandler(SubmitSMResp.class, "submitSMResponse");
            addHandler(SubmitMultiResp.class, "submitMultiResponse");
            addHandler(CancelSMResp.class, "cancelSMResponse");
            addHandler(ReplaceSMResp.class, "replaceSMResponse");
            addHandler(ParamRetrieveResp.class, "paramRetrieveResponse");
            addHandler(QuerySMResp.class, SMPPPacket.class, "queryResponse");
            addHandler(QueryLastMsgsResp.class, SMPPPacket.class, "queryResponse");
            addHandler(QueryMsgDetailsResp.class, SMPPPacket.class, "queryResponse");
            addHandler(EnquireLink.class, "queryLink");
            addHandler(EnquireLinkResp.class, "queryLinkResponse");
            addHandler(Unbind.class, "unbind");
            addHandler(UnbindResp.class, "unbindResponse");
            addHandler(BindResp.class, "bindResponse");
            addHandler(GenericNack.class, "genericNack");
        } catch (NoSuchMethodException x) {
            throw new RuntimeException("Illegal handler mapping", x);
        }
    }

    private static void addHandler(
            Class<? extends SMPPPacket> clazz,
            String methodName) throws NoSuchMethodException {
        HANDLERS.put(clazz, getMethod(methodName, clazz));
    }
    
    private static void addHandler(Class<? extends SMPPPacket> clazz,
            Class<? extends SMPPPacket> argClass,
            String methodName) throws NoSuchMethodException {
        HANDLERS.put(clazz, getMethod(methodName, argClass));
    }
    
    private static Method getMethod(String name, Class<?> argClass) throws NoSuchMethodException {
        Class<?>[] args = new Class[] {
                Session.class,
                argClass,
        };
        return SMPPEventAdapter.class.getMethod(name, args);
    }
}
