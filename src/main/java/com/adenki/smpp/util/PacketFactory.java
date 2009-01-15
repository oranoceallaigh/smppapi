package com.adenki.smpp.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.BadCommandIDException;
import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.message.AlertNotification;
import com.adenki.smpp.message.BindReceiver;
import com.adenki.smpp.message.BindReceiverResp;
import com.adenki.smpp.message.BindTransceiver;
import com.adenki.smpp.message.BindTransceiverResp;
import com.adenki.smpp.message.BindTransmitter;
import com.adenki.smpp.message.BindTransmitterResp;
import com.adenki.smpp.message.BroadcastSM;
import com.adenki.smpp.message.BroadcastSMResp;
import com.adenki.smpp.message.CancelBroadcastSM;
import com.adenki.smpp.message.CancelBroadcastSMResp;
import com.adenki.smpp.message.CancelSM;
import com.adenki.smpp.message.CancelSMResp;
import com.adenki.smpp.message.DataSM;
import com.adenki.smpp.message.DataSMResp;
import com.adenki.smpp.message.DeliverSM;
import com.adenki.smpp.message.DeliverSMResp;
import com.adenki.smpp.message.EnquireLink;
import com.adenki.smpp.message.EnquireLinkResp;
import com.adenki.smpp.message.GenericNack;
import com.adenki.smpp.message.Outbind;
import com.adenki.smpp.message.ParamRetrieve;
import com.adenki.smpp.message.ParamRetrieveResp;
import com.adenki.smpp.message.QueryBroadcastSM;
import com.adenki.smpp.message.QueryBroadcastSMResp;
import com.adenki.smpp.message.QueryLastMsgs;
import com.adenki.smpp.message.QueryLastMsgsResp;
import com.adenki.smpp.message.QueryMsgDetails;
import com.adenki.smpp.message.QueryMsgDetailsResp;
import com.adenki.smpp.message.QuerySM;
import com.adenki.smpp.message.QuerySMResp;
import com.adenki.smpp.message.ReplaceSM;
import com.adenki.smpp.message.ReplaceSMResp;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.SubmitMulti;
import com.adenki.smpp.message.SubmitMultiResp;
import com.adenki.smpp.message.SubmitSM;
import com.adenki.smpp.message.SubmitSMResp;
import com.adenki.smpp.message.Unbind;
import com.adenki.smpp.message.UnbindResp;

/**
 * Factory class for SMPP packets.
 * @version $Id$
 */
public final class PacketFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PacketFactory.class);
    
    private final Map<Integer, Class<? extends SMPPPacket>> commands =
        new HashMap<Integer, Class<? extends SMPPPacket>>();
    private final Map<Integer, Class<? extends SMPPPacket>> userCommands =
        new HashMap<Integer, Class<? extends SMPPPacket>>();
    
    public PacketFactory() {
        add(new AlertNotification());
        add(new BindReceiver(), new BindReceiverResp());
        add(new BindTransceiver(), new BindTransceiverResp());
        add(new BindTransmitter(), new BindTransmitterResp());
        add(new BroadcastSM(), new BroadcastSMResp());
        add(new CancelBroadcastSM(), new CancelBroadcastSMResp());
        add(new CancelSM(), new CancelSMResp());
        add(new DataSM(), new DataSMResp());
        add(new DeliverSM(), new DeliverSMResp());
        add(new EnquireLink(), new EnquireLinkResp());
        add(new GenericNack());
        add(new Outbind());
        add(new ParamRetrieve(), new ParamRetrieveResp());
        add(new QueryBroadcastSM(), new QueryBroadcastSMResp());
        add(new QueryLastMsgs(), new QueryLastMsgsResp());
        add(new QueryMsgDetails(), new QueryMsgDetailsResp());
        add(new QuerySM(), new QuerySMResp());
        add(new ReplaceSM(), new ReplaceSMResp());
        add(new SubmitMulti(), new SubmitMultiResp());
        add(new SubmitSM(), new SubmitSMResp());
        add(new Unbind(), new UnbindResp());
    }

    /**
     * Create a new instance of the appropriate sub class of SMPPPacket. Packet
     * fields are all left at their default initial state.
     * 
     * @param id
     *            The SMPP command ID of the packet type to return.
     * @return A sub-class instance of {@link com.adenki.smpp.message.SMPPPacket}
     *         representing SMPP command <code>id</code>.
     * @throws com.adenki.smpp.BadCommandIDException
     *             if the command ID is not recognized.
     */
    public SMPPPacket newInstance(int id) {
        return newInstance(id, null);
    }
    
    /**
     * Get a response packet for the specified request. The returned response
     * packet will have its sequence number initialised to the same value
     * as <code>packet</code>.
     * @param packet The request packet to get a response for.
     * @return An SMPP response packet.
     * @throws BadCommandIDException If there is no response packet for the
     * specified request (for example, an <code>AlertNotification</code>).
     * @throws SMPPRuntimeException If an attempt is made to create a
     * response to a response packet.
     */
    public SMPPPacket newResponse(SMPPPacket packet) {
        if (packet.isResponse()) {
            throw new SMPPRuntimeException(
                    "Cannot create a response to a response!");
        }
        int id = packet.getCommandId();
        SMPPPacket response = newInstance(id | 0x80000000, packet);
        response.setSequenceNum(packet.getSequenceNum());
        return response;
    }

    /**
     * Register a vendor packet with the factory. The SMPP allows for
     * vendor-specific packets to be defined. In order for these to be
     * usable with the API, primarily so that they can be identified and
     * decoded when received from an SMSC, they must be registered with
     * the packet factory.
     * <p>
     * This implementation assumes that the ID of the response packet will
     * be the ID of the request packet ORed with <code>0x80000000</code>.
     * This implementation also accepts <code>null</code> for the
     * <code>responseType</code> since there is at least one incidence in
     * the specification of such a case (<code>AlertNotification</code> has
     * no response packet).
     * </p>
     * @param id The command ID of the request packet.
     * @param requestType The class which implements the vendor request packet.
     * @param responseType The class which implements the vendor response
     * packet.
     */
    public void registerVendorPacket(int id,
            Class<? extends SMPPPacket> requestType,
            Class<? extends SMPPPacket> responseType) {
        userCommands.put(Integer.valueOf(id), requestType);
        if (responseType != null) {
            userCommands.put(
                    Integer.valueOf(id | 0x80000000), responseType);
        }
    }

    /**
     * Remove a vendor packet definition from this factory.
     * @param id The ID of the vendor packet to remove. This will also
     * unregister the response packet if it exists.
     */
    public void unregisterVendorPacket(int id) {
        userCommands.remove(Integer.valueOf(id));
        userCommands.remove(Integer.valueOf(id | 0x80000000));
    }

    /**
     * Add an internal API-defined packet type.
     * @param command The request packet to add.
     */
    private void add(SMPPPacket command) {
        int commandId = command.getCommandId();
        Class<? extends SMPPPacket> commandClass = command.getClass();
        commands.put(Integer.valueOf(commandId), commandClass);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Mapping id 0x{} to class {}",
                    Integer.toHexString(commandId), commandClass.getName());
        }
    }
    
    /**
     * Add an internal API-defined packet type.
     * @param requestClass The request packet to add.
     * @param responseClass The response packet to add.
     */
    private void add(SMPPPacket request, SMPPPacket response) {
        add(request);
        if (response != null) {
            add(response);
        }
    }

    /**
     * Get a new instance of an SMPP packet for the specified ID.
     * @param id The command ID to get the packet object for.
     * @param request If a response packet is being created, this parameter
     * may be optionally supplied and an attempt will be made to call a
     * constructor which accepts an SMPPPacket as its argument. All of the
     * response packets that are supplied as part of the API have such
     * a constructor.
     * @return A new instance of the relevant SMPPPacket implementation.
     * @throws BadCommandIDException If no matching class can be found for
     * <code>id</code>.
     * @throws SMPPRuntimeException If a packet&apos;s constructor throws
     * an exception.
     */
    private SMPPPacket newInstance(int id, SMPPPacket request) {
        SMPPPacket response = null;
        Class<? extends SMPPPacket> clazz = getClassForId(id);
        if (clazz == null) {
            throw new BadCommandIDException(
                    "Unrecognized command id " + Integer.toHexString(id), id);
        }
        try {
            if (request != null) {
                response = constructWithPacketArg(clazz, request);
            }
            if (response == null) {
                response = clazz.newInstance();
            }
        } catch (Exception x) {
            throw new SMPPRuntimeException(
                    "Packet constructor threw an exception.", x);
        }
        return response;
    }
    
    /**
     * Construct an SMPPPacket implementation class using a single-argument
     * constructor which takes an SMPPPacket object as its argument.
     * @param clazz The class to instantiate.
     * @param request The object to pass to the constructor.
     * @return The instantiated class, or <code>null</code> if the class does
     * not implement a single-argument constructor which accepts an SMPPPacket.
     * @throws Exception Any exception that is thrown by
     * {@link Constructor#newInstance(java.lang.Object[])} can be thrown
     * by this method.
     */
    private SMPPPacket constructWithPacketArg(
            Class<? extends SMPPPacket> clazz,
            SMPPPacket request) throws Exception {
        SMPPPacket packet = null;
        try {
            Constructor<? extends SMPPPacket> cons = clazz.getConstructor(
                    new Class[] {SMPPPacket.class});
            packet = cons.newInstance(
                    new Object[] {request});
        } catch (NoSuchMethodException x) {
            LOG.debug("No SMPPPacket constructor; will fall back to default.");
        }
        return packet;
    }
    
    /**
     * Get the implementation class for SMPP <code>commandId</code>.
     * The internally supplied SMPPPacket implementations will be queried
     * first, followed by all registered vendor packets.
     * @param commandId The command ID of the packet to get.
     * @return The implementing class, or <code>null</code> if there is
     * no class for the specified command ID.
     */
    private Class<? extends SMPPPacket> getClassForId(int commandId) {
        Integer id = Integer.valueOf(commandId);
        Class<? extends SMPPPacket> clazz = commands.get(id);
        if (clazz == null) {
            clazz = userCommands.get(id);
        }
        return clazz;
    }
}
