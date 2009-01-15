package com.adenki.smpp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.event.ReceiverExitEvent;
import com.adenki.smpp.event.SMPPEvent;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.net.ReadTimeoutException;
import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;
import com.adenki.smpp.util.PacketFactory;

/**
 * Receiver thread for the connection.
 * @version $Id$
 */
public class ReceiverThread implements Receiver, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiverThread.class);

    private Thread thread;
    private boolean running;
    private Session session;
    private PacketFactory packetFactory = new PacketFactory();

    public ReceiverThread() {
        thread = new Thread(this);
        thread.setDaemon(true);
    }
    
    public ReceiverThread(Session session) {
        this();
        this.session = session;
    }
    
    public PacketFactory getPacketFactory() {
        return packetFactory;
    }

    public void setPacketFactory(PacketFactory packetFactory) {
        this.packetFactory = packetFactory;
    }

    public Session getSession() {
        return session;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    public String getName() {
        return thread.getName();
    }
    
    public void setName(String name) {
        thread.setName(name);
    }
    
    public void run() {
        LOG.debug("Receiver thread starting.");
        SMPPEvent exitEvent = null;
        try {
            running = true;
            exitEvent = processPackets();
        } catch (Exception x) {
            exitEvent = new ReceiverExitEvent(session, x);
        }
        session.getEventDispatcher().notifyObservers(session, exitEvent);
        LOG.debug("Destroying event dispatcher.");
        session.getEventDispatcher().destroy();
        LOG.debug("Receiver thread exiting.");
    }

    public boolean isStarted() {
        return thread.isAlive();
    }

    public void start() {
        thread.start();
    }
    
    public void stop() {
        running = false;
    }
    
    private ReceiverExitEvent processPackets() throws Exception {
        ReceiverExitEvent exitEvent = null;
        int ioExceptions = 0;
        APIConfig config = APIConfigFactory.getConfig();
        final int ioExceptionLimit =
            config.getInt(APIConfig.TOO_MANY_IO_EXCEPTIONS, 5);
        
        SMPPPacket packet = null;
        while (running && session.getState() != SessionState.UNBOUND) {
            try {
                packet = readNextPacket();
                if (packet == null) {
                    continue;
                }
                session.processReceivedPacket(packet);
                session.getEventDispatcher().notifyObservers(session, packet);
                ioExceptions = 0;
            } catch (ReadTimeoutException x) {
                SessionState state = session.getState();
                if (state == SessionState.BINDING) {
                    LOG.debug("Bind timeout occurred.");
                    exitEvent = new ReceiverExitEvent(session, null, state);
                    exitEvent.setReason(ReceiverExitEvent.BIND_TIMEOUT);
                    break;
                }
            } catch (IOException x) {
                LOG.debug("Exception in receiver", x);
                ioExceptions++;
                if (ioExceptions >= ioExceptionLimit) {
                    SessionState state = session.getState();
                    exitEvent = new ReceiverExitEvent(session, x, state);
                    exitEvent.setReason(ReceiverExitEvent.EXCEPTION);
                    break;
                }
            }
        }
        if (exitEvent == null) {
            exitEvent = new ReceiverExitEvent(session);
        }
        return exitEvent;
    }
    
    private SMPPPacket readNextPacket() throws IOException {
        return session.getSmscLink().read();
    }
}
