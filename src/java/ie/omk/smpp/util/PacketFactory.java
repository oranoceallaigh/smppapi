package ie.omk.smpp.util;

import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.SMPPRuntimeException;
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
import ie.omk.smpp.message.Outbind;
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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to create new SMPP packet objects.
 * 
 * @since 1.0
 * @author Oran Kelly
 */
public final class PacketFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PacketFactory.class);
    private static final PacketFactory INSTANCE = new PacketFactory();
    
    private final Map<Integer, Class<? extends SMPPPacket>> commands;
    private final Map<Integer, Class<? extends SMPPPacket>> userCommands =
        new HashMap<Integer, Class<? extends SMPPPacket>>();
    
    private PacketFactory() {
        Map<Integer, Class<? extends SMPPPacket>> commands =
            new HashMap<Integer, Class<? extends SMPPPacket>>();
        commands.put(Integer.valueOf(SMPPPacket.ALERT_NOTIFICATION), AlertNotification.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_RECEIVER), BindReceiver.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_RECEIVER_RESP), BindReceiverResp.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSCEIVER), BindTransceiver.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSCEIVER_RESP), BindTransceiverResp.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSMITTER), BindTransmitter.class);
        commands.put(Integer.valueOf(SMPPPacket.BIND_TRANSMITTER_RESP), BindTransmitterResp.class);
        commands.put(Integer.valueOf(SMPPPacket.CANCEL_SM), CancelSM.class);
        commands.put(Integer.valueOf(SMPPPacket.CANCEL_SM_RESP), CancelSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.DATA_SM), DataSM.class);
        commands.put(Integer.valueOf(SMPPPacket.DATA_SM_RESP), DataSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.DELIVER_SM), DeliverSM.class);
        commands.put(Integer.valueOf(SMPPPacket.DELIVER_SM_RESP), DeliverSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.ENQUIRE_LINK), EnquireLink.class);
        commands.put(Integer.valueOf(SMPPPacket.ENQUIRE_LINK_RESP), EnquireLinkResp.class);
        commands.put(Integer.valueOf(SMPPPacket.GENERIC_NACK), GenericNack.class);
        commands.put(Integer.valueOf(SMPPPacket.OUTBIND), Outbind.class);
        commands.put(Integer.valueOf(SMPPPacket.PARAM_RETRIEVE), ParamRetrieve.class);
        commands.put(Integer.valueOf(SMPPPacket.PARAM_RETRIEVE_RESP), ParamRetrieveResp.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_LAST_MSGS), QueryLastMsgs.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_LAST_MSGS_RESP), QueryLastMsgsResp.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_MSG_DETAILS), QueryMsgDetails.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_MSG_DETAILS_RESP), QueryMsgDetailsResp.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_SM), QuerySM.class);
        commands.put(Integer.valueOf(SMPPPacket.QUERY_SM_RESP), QuerySMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.REPLACE_SM), ReplaceSM.class);
        commands.put(Integer.valueOf(SMPPPacket.REPLACE_SM_RESP), ReplaceSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_MULTI), SubmitMulti.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_MULTI_RESP), SubmitMultiResp.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_SM), SubmitSM.class);
        commands.put(Integer.valueOf(SMPPPacket.SUBMIT_SM_RESP), SubmitSMResp.class);
        commands.put(Integer.valueOf(SMPPPacket.UNBIND), Unbind.class);
        commands.put(Integer.valueOf(SMPPPacket.UNBIND_RESP), UnbindResp.class);
        this.commands = Collections.unmodifiableMap(commands);
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
    public static SMPPPacket newInstance(int id) {
        return INSTANCE.newInstance(id, null);
    }
    
    /**
     * Get a response packet for the specified request. The returned response
     * packet will have its sequence number initialised to the same value
     * as <code>packet</code>.
     * @param packet The request packet to get a response for.
     * @return An SMPP response packet.
     */
    public static SMPPPacket newResponse(SMPPPacket packet) {
        if (packet.isResponse()) {
            throw new IllegalArgumentException(
                    "Cannot create a response to a response!");
        }
        try {
            int id = packet.getCommandId();
            SMPPPacket response = INSTANCE.newInstance(id | 0x80000000, packet);
            response.setSequenceNum(packet.getSequenceNum());
            return response;
        } catch (BadCommandIDException x) {
            throw new SMPPRuntimeException("Internal error in the smppapi.", x);
        }
    }

    // TODO document
    public static void registerVendorPacket(int id,
            Class<? extends SMPPPacket> requestType,
            Class<? extends SMPPPacket> responseType) {
        INSTANCE.userCommands.put(Integer.valueOf(id), requestType);
        if (responseType != null) {
            INSTANCE.userCommands.put(
                    Integer.valueOf(id | 0x80000000), responseType);
        }
    }

    public static void unregisterVendorPacket(int id) {
        INSTANCE.userCommands.remove(Integer.valueOf(id));
        INSTANCE.userCommands.remove(Integer.valueOf(id | 0x80000000));
    }
    
    // TODO throws badcommandidexception - now a runtime exception.
    private SMPPPacket newInstance(int id, SMPPPacket request) {
        SMPPPacket response = null;
        Class<? extends SMPPPacket> clazz = getClassForId(id);
        if (clazz == null) {
            throw new BadCommandIDException(
                    "Unrecognized command id " + Integer.toHexString(id), id);
        }
        try {
            if (request != null) {
                try {
                    Constructor<? extends SMPPPacket> cons = clazz.getConstructor(
                            new Class[] {SMPPPacket.class});
                    response = cons.newInstance(
                            new Object[] {request});
                } catch (NoSuchMethodException x) {
                    LOG.debug("No SMPPPacket constructor - using the default.");
                }
            }
            if (response == null) {
                response = clazz.newInstance();
            }
        } catch (Exception x) {
            throw new BadCommandIDException("Exception while calling constructor", x);
        }
        return response;
    }
    
    private Class<? extends SMPPPacket> getClassForId(int id) {
        Integer commandId = Integer.valueOf(id);
        Class<? extends SMPPPacket> clazz = commands.get(Integer.valueOf(commandId));
        if (clazz == null) {
            clazz = userCommands.get(commandId);
        }
        return clazz;
    }
}
