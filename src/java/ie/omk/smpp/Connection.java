package ie.omk.smpp;

import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.EventDispatcher;
import ie.omk.smpp.event.SimpleEventDispatcher;
import ie.omk.smpp.message.Bind;
import ie.omk.smpp.message.BindReceiver;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.BindTransceiver;
import ie.omk.smpp.message.BindTransmitter;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.net.SmscLink;
import ie.omk.smpp.net.TcpLink;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.DefaultSequenceScheme;
import ie.omk.smpp.util.PropertyNotFoundException;
import ie.omk.smpp.util.SequenceNumberScheme;
import ie.omk.smpp.version.SMPPVersion;
import ie.omk.smpp.version.VersionException;
import ie.omk.smpp.version.VersionFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO documentation!
public class Connection {
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(1);
    
    private final Logger log;
    private String connectionId;
    private SMPPVersion version = VersionFactory.getDefaultVersion();
    private ConnectionType type;
    private AtomicInteger state = new AtomicInteger(ConnectionState.UNBOUND.intValue());
    private SmscLink smscLink;
    private SequenceNumberScheme numberScheme = new DefaultSequenceScheme();
    private EventDispatcher eventDispatcher;
    private Thread receiver;
    private boolean useOptionalParams = version.isSupportTLV();
    private boolean validating = true;
    
    public Connection(SmscLink link) {
        connectionId = "Connection-" + CONNECTION_ID.getAndIncrement();
        log = LoggerFactory.getLogger(Connection.class + "." + connectionId);
        this.smscLink = link;
        initFromConfig();
        receiver = new ReceiverThread(this);
        receiver.setName(connectionId + "-Receiver");
    }
    
    public Connection(String host, int port) throws UnknownHostException {
        this(new TcpLink(host, port));
    }

    public String getConnectionId() {
        return connectionId;
    }
    
    public void addObserver(ConnectionObserver observer) {
        eventDispatcher.addObserver(observer);
    }
    
    public void removeObserver(ConnectionObserver observer) {
        eventDispatcher.removeObserver(observer);
    }
    
    public SmscLink getSmscLink() {
        return smscLink;
    }
    
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
    
    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        EventDispatcher oldDispatcher = this.eventDispatcher;
        initNewDispatcher(oldDispatcher, eventDispatcher);
        this.eventDispatcher = eventDispatcher;
        oldDispatcher.destroy();
    }
    
    public SMPPVersion getVersion() {
        return version;
    }

    public void setVersion(SMPPVersion version) {
        this.version = version;
        this.useOptionalParams = version.isSupportTLV();
    }

    public SequenceNumberScheme getSequenceNumberScheme() {
        return numberScheme;
    }
    
    public void setSequenceNumberScheme(SequenceNumberScheme numberScheme) {
        this.numberScheme = numberScheme;
    }
    
    public boolean isValidating() {
        return validating;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public void bind(ConnectionType type,
            String systemID,
            String password,
            String systemType) throws IOException {
        bind(type, systemID, password, systemType, 0, 0, null);
    }
    
    public void bind(ConnectionType type,
            String systemID,
            String password,
            String systemType,
            int typeOfNumber,
            int numberPlanIndicator,
            String addressRange) throws IOException {
        Bind bindRequest;
        if (type == ConnectionType.TRANSMITTER) {
            bindRequest = new BindTransmitter();
        } else if (type == ConnectionType.RECEIVER) {
            bindRequest = new BindReceiver();
        } else {
            bindRequest = new BindTransceiver();
        }
        bindRequest.setVersion(version);
        bindRequest.setSystemId(systemID);
        bindRequest.setPassword(password);
        bindRequest.setSystemType(systemType);
        bindRequest.setAddressTon(typeOfNumber);
        bindRequest.setAddressNpi(numberPlanIndicator);
        bindRequest.setAddressRange(addressRange);
        bind(bindRequest);
    }
    
    public void bind(Bind bindRequest) throws IOException {
        if (getState() != ConnectionState.UNBOUND) {
            throw new IllegalStateException("Already binding or bound.");
        }
        if (bindRequest.getCommandId() == SMPPPacket.BIND_TRANSMITTER) {
            type = ConnectionType.TRANSMITTER;
        } else if (bindRequest.getCommandId() == SMPPPacket.BIND_RECEIVER) {
            type = ConnectionType.RECEIVER;
        } else {
            type = ConnectionType.TRANSCEIVER;
        }
        if (!smscLink.isConnected()) {
            smscLink.open();
        }
        setLinkTimeout(APIConfig.BIND_TIMEOUT);
        log.debug("Sending bind packet to the SMSC..");
        sendPacketInternal(bindRequest);
        receiver.start();
    }

    public void unbind() throws IOException {
        sendPacketInternal(new Unbind());
    }
    
    public void sendPacket(SMPPPacket packet) throws IOException {
        int commandId = packet.getCommandId();
        switch (commandId) {
        case SMPPPacket.BIND_TRANSMITTER:
        case SMPPPacket.BIND_TRANSCEIVER:
        case SMPPPacket.BIND_RECEIVER:
            bind((Bind) packet);
            return;
        }
        if (type == ConnectionType.RECEIVER) {
            // We allow the receiver to send any response type but a very
            // limited set of requests.
            if (packet.isRequest()
                    && !(commandId == SMPPPacket.UNBIND
                            || commandId == SMPPPacket.ENQUIRE_LINK)) {
                throw new UnsupportedOperationException(
                        "Receiver connection cannot send command " + commandId);
            }
        }
        sendPacketInternal(packet);
    }

    public void closeLink() throws IOException {
        if (getState() == ConnectionState.UNBOUND) {
            smscLink.close();
        } else {
            throw new IllegalStateException("Cannot close link while connection is bound.");
        }
    }
    
    public ConnectionState getState() {
        return ConnectionState.valueOf(state.get());
    }

    void processReceivedPacket(SMPPPacket packet) {
        switch (packet.getCommandId()) {
        case SMPPPacket.BIND_TRANSMITTER_RESP:
        case SMPPPacket.BIND_RECEIVER_RESP:
        case SMPPPacket.BIND_TRANSCEIVER_RESP:
            processReceivedBindResponse((BindResp) packet);
            break;
        case SMPPPacket.UNBIND:
            processReceivedUnbind((Unbind) packet);
            break;
        case SMPPPacket.UNBIND_RESP:
            processReceivedUnbindResponse((UnbindResp) packet);
            break;
        default:
            // Do nothing.
        }
    }

    private void setState(ConnectionState fromState, ConnectionState toState) {
        if (!state.compareAndSet(fromState.intValue(), toState.intValue())) {
            log.error("Race condition in setting state - expected {} but is {}",
                    fromState, getState());
        }
    }

    private void initFromConfig() {
        APIConfig config = APIConfig.getInstance();
        String s = config.getProperty(APIConfig.EVENT_DISPATCHER_CLASS, null);
        if (s != null) {
            eventDispatcher =
                (EventDispatcher) getClassInstance(s, EventDispatcher.class);
        }
        if (eventDispatcher == null) {
            eventDispatcher = new SimpleEventDispatcher();
        }
        initNewDispatcher(null, eventDispatcher);
    }

    private void initNewDispatcher(EventDispatcher oldDispatcher, EventDispatcher newDispatcher) {
        newDispatcher.init();
        if (oldDispatcher != null) {
            Collection<ConnectionObserver> observers =
                oldDispatcher.getObservers();
            for (ConnectionObserver observer : observers) {
                newDispatcher.addObserver(observer);
            }
        }
    }
    
    // TODO: do we still want to move this to APIConfig?
    private Object getClassInstance(String className, Class type) {
        Object obj = null;
        try {
            log.debug("Attempting to instantiate a class of type {}",
                    className);
            Class<?> clazz = Class.forName(className);
            if (!clazz.isAssignableFrom(type)) {
                log.error("{} is not of type {}", className, type.getClass());
            } else {
                obj = clazz.newInstance();
            }
        } catch (Exception x) {
            log.error("Could not instantiate a class of type {}: {}",
                    className, x.getMessage());
            log.debug("Stack trace", x);
        }
        return obj;
    }
    
    private void sendPacketInternal(SMPPPacket packet) throws IOException {
        if (packet.getSequenceNum() < 0L && numberScheme != null) {
            packet.setSequenceNum(numberScheme.nextNumber());
        }
        if (validating) {
            packet.validate(version);
        }
        smscLink.write(packet, useOptionalParams);
        processSentPacket(packet);
    }

    private void processSentPacket(SMPPPacket packet) {
        switch (packet.getCommandId()) {
        case SMPPPacket.BIND_TRANSMITTER:
        case SMPPPacket.BIND_RECEIVER:
        case SMPPPacket.BIND_TRANSCEIVER:
            processSentBind((Bind) packet);
            break;
        case SMPPPacket.UNBIND:
            processSentUnbind((Unbind) packet);
            break;
        case SMPPPacket.UNBIND_RESP:
            processSentUnbindResponse((UnbindResp) packet);
            break;
        default:
        }
    }
    
    private void processSentBind(Bind bindRequest) {
        setState(ConnectionState.UNBOUND, ConnectionState.BINDING);
    }
    
    private void processSentUnbind(Unbind unbindRequest) {
        setState(ConnectionState.BOUND, ConnectionState.UNBINDING);
    }
    
    private void processSentUnbindResponse(UnbindResp unbindResponse) {
        int status = unbindResponse.getCommandStatus();
        if (status == 0) {
            setState(ConnectionState.UNBINDING, ConnectionState.UNBOUND);
        }
    }
    
    private void processReceivedBindResponse(BindResp bindResponse) {
        int status = bindResponse.getCommandStatus();
        if (status == 0) {
            setState(ConnectionState.BINDING, ConnectionState.BOUND);
            negotiateVersion(bindResponse);
            setLinkTimeout(APIConfig.LINK_TIMEOUT);
        } else {
            log.warn("Received a bind response with status {}", status);
            setState(ConnectionState.BINDING, ConnectionState.UNBOUND);
        }
    }
    
    private void negotiateVersion(BindResp bindResponse) {
        if (bindResponse.isSet(Tag.SC_INTERFACE_VERSION)) {
            log.info("SMSC did not supply SC_INTERFACE_VERSION."
                    + " Disabling optional parameter support.");
            useOptionalParams = false;
            return;
        }
        int versionId = 0;
        try {
            versionId =
                bindResponse.getTLVTable().getInt(Tag.SC_INTERFACE_VERSION);
            SMPPVersion smscVersion =
                VersionFactory.getVersion(versionId);
            log.info("SMSC states its version as {}", smscVersion);
            if (smscVersion.isOlderThan(version)) {
                version = smscVersion;
                useOptionalParams = version.isSupportTLV();
            }
        } catch (VersionException x) {
            log.debug("SMSC implements a version I don't know: {}", versionId);
        }
    }
    
    private void processReceivedUnbind(Unbind unbindRequest) {
        setState(ConnectionState.BOUND, ConnectionState.UNBINDING);
    }
    
    private void processReceivedUnbindResponse(UnbindResp unbindResponse) {
        int status = unbindResponse.getCommandStatus();
        if (status == 0) {
            setState(ConnectionState.UNBINDING, ConnectionState.UNBOUND);
        } else {
            log.warn("Received an unbind response with status {}", status);
        }
    }
    
    private void setLinkTimeout(String propName) {
        try {
            if (smscLink.isTimeoutSupported()) {
                int linkTimeout = APIConfig.getInstance().getInt(propName);
                smscLink.setTimeout(linkTimeout);
                if (log.isDebugEnabled()) {
                    log.debug("Set the link timeout to {}", linkTimeout);
                }
            } else {
                log.info("SMSC link implementation does not support timeouts.");
            }
        } catch (PropertyNotFoundException x) {
            log.debug("Not setting link timeout as it is not configured.");
        }
    }
}
