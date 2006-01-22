package ie.omk.smpp.util;

import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.message.AlertNotification;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.BindReceiverResp;
import ie.omk.smpp.message.BindTransceiver;
import ie.omk.smpp.message.BindTransceiverResp;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.BindTransmitterResp;
import ie.omk.smpp.message.CancelSM;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DataSMResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.DeliverSMResp;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.EnquireLinkResp;
import ie.omk.smpp.message.GenericNack;
import ie.omk.smpp.message.ParamRetrieve;
import ie.omk.smpp.message.ParamRetrieveResp;
import ie.omk.smpp.message.QueryLastMsgs;
import ie.omk.smpp.message.QueryLastMsgsResp;
import ie.omk.smpp.message.QueryMsgDetails;
import ie.omk.smpp.message.QueryMsgDetailsResp;
import ie.omk.smpp.message.QuerySM;
import ie.omk.smpp.message.QuerySMResp;
import ie.omk.smpp.message.ReplaceSM;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitMulti;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

/**
 * Helper class to create new SMPP packet objects.
 * 
 * @since 1.0
 * @author Oran Kelly
 */
public final class PacketFactory {
    private PacketFactory() {
    }

    /**
     * Create a new instance of the appropriate sub class of SMPPPacket.
     * 
     * @deprecated
     */
    public static SMPPPacket newPacket(int id) throws BadCommandIDException {
        return newInstance(id);
    }

    /**
     * Create a new instance of the appropriate sub class of SMPPPacket. Packet
     * fields are all left at their default initial state.
     * 
     * @param id
     *            The SMPP command ID of the packet type to return.
     * @return A sub-class instance of {@link ie.omk.smpp.message.SMPPPacket}
     *         representing SMPP command <code>id</code>.
     * @throws ie.omk.smpp.BadCommandIDException
     *             if the command ID is not recognized.
     */
    public static SMPPPacket newInstance(int id) throws BadCommandIDException {
        SMPPPacket response = null;

        switch (id) {
        case SMPPPacket.GENERIC_NACK:
            response = new GenericNack();
            break;

        case SMPPPacket.BIND_RECEIVER:
            response = new BindReceiver();
            break;

        case SMPPPacket.BIND_RECEIVER_RESP:
            response = new BindReceiverResp();
            break;

        case SMPPPacket.BIND_TRANSMITTER:
            response = new BindTransmitter();
            break;

        case SMPPPacket.BIND_TRANSMITTER_RESP:
            response = new BindTransmitterResp();
            break;

        case SMPPPacket.BIND_TRANSCEIVER:
            response = new BindTransceiver();
            break;

        case SMPPPacket.BIND_TRANSCEIVER_RESP:
            response = new BindTransceiverResp();
            break;

        case SMPPPacket.UNBIND:
            response = new Unbind();
            break;

        case SMPPPacket.UNBIND_RESP:
            response = new UnbindResp();
            break;

        case SMPPPacket.SUBMIT_SM:
            response = new SubmitSM();
            break;

        case SMPPPacket.SUBMIT_SM_RESP:
            response = new SubmitSMResp();
            break;

        case SMPPPacket.DATA_SM:
            response = new DataSM();
            break;

        case SMPPPacket.DATA_SM_RESP:
            response = new DataSMResp();
            break;

        case SMPPPacket.ALERT_NOTIFICATION:
            response = new AlertNotification();
            break;

        case SMPPPacket.SUBMIT_MULTI:
            response = new SubmitMulti();
            break;

        case SMPPPacket.SUBMIT_MULTI_RESP:
            response = new SubmitMultiResp();
            break;

        case SMPPPacket.DELIVER_SM:
            response = new DeliverSM();
            break;

        case SMPPPacket.DELIVER_SM_RESP:
            response = new DeliverSMResp();
            break;

        case SMPPPacket.QUERY_SM:
            response = new QuerySM();
            break;

        case SMPPPacket.QUERY_SM_RESP:
            response = new QuerySMResp();
            break;

        case SMPPPacket.QUERY_LAST_MSGS:
            response = new QueryLastMsgs();
            break;

        case SMPPPacket.QUERY_LAST_MSGS_RESP:
            response = new QueryLastMsgsResp();
            break;

        case SMPPPacket.QUERY_MSG_DETAILS:
            response = new QueryMsgDetails();
            break;

        case SMPPPacket.QUERY_MSG_DETAILS_RESP:
            response = new QueryMsgDetailsResp();
            break;

        case SMPPPacket.CANCEL_SM:
            response = new CancelSM();
            break;

        case SMPPPacket.CANCEL_SM_RESP:
            response = new CancelSMResp();
            break;

        case SMPPPacket.REPLACE_SM:
            response = new ReplaceSM();
            break;

        case SMPPPacket.REPLACE_SM_RESP:
            response = new ReplaceSMResp();
            break;

        case SMPPPacket.ENQUIRE_LINK:
            response = new EnquireLink();
            break;

        case SMPPPacket.ENQUIRE_LINK_RESP:
            response = new EnquireLinkResp();
            break;

        case SMPPPacket.PARAM_RETRIEVE:
            response = new ParamRetrieve();
            break;

        case SMPPPacket.PARAM_RETRIEVE_RESP:
            response = new ParamRetrieveResp();
            break;

        default:
            throw new BadCommandIDException();
        }
        return response;
    }
}
