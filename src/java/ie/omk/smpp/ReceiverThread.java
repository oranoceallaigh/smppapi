package ie.omk.smpp;

import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receiver thread for the connection.
 * @version $Id:$
 */
class ReceiverThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiverThread.class);
    
    private Connection connection;
    private byte[] buffer = new byte[128];
    
    ReceiverThread(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void run() {
        LOG.debug("Receiver thread starting.");
        SMPPEvent exitEvent = null;
        try {
            exitEvent = processPackets();
        } catch (Exception x) {
            exitEvent = new ReceiverExitEvent(connection, x);
        }
        connection.getEventDispatcher().notifyObservers(connection, exitEvent);
        LOG.debug("Destroying event dispatcher.");
        connection.getEventDispatcher().destroy();
        LOG.debug("Receiver thread exiting.");
    }

    private ReceiverExitEvent processPackets() throws Exception {
        ReceiverExitEvent exitEvent = null;
        int ioExceptions = 0;
        final int ioExceptionLimit = APIConfig.getInstance().getInt(
                APIConfig.TOO_MANY_IO_EXCEPTIONS, 5);
        
        SMPPPacket packet = null;
        while (connection.getState() != ConnectionState.UNBOUND) {
            try {
                packet = readNextPacket();
                if (packet == null) {
                    continue;
                }
                connection.processReceivedPacket(packet);
                connection.getEventDispatcher().notifyObservers(connection, packet);
                ioExceptions = 0;
            } catch (SocketTimeoutException x) {
                ConnectionState state = connection.getState();
                if (state == ConnectionState.BINDING) {
                    LOG.debug("Bind timeout occurred.");
                    exitEvent = new ReceiverExitEvent(connection, null, state);
                    exitEvent.setReason(ReceiverExitEvent.BIND_TIMEOUT);
                    break;
                }
            } catch (IOException x) {
                LOG.debug("Exception in receiver", x);
                ioExceptions++;
                if (ioExceptions >= ioExceptionLimit) {
                    ConnectionState state = connection.getState();
                    exitEvent = new ReceiverExitEvent(connection, x, state);
                    exitEvent.setReason(ReceiverExitEvent.EXCEPTION);
                    break;
                }
            }
        }
        if (exitEvent == null) {
            exitEvent = new ReceiverExitEvent(connection);
        }
        return exitEvent;
    }
    
    private SMPPPacket readNextPacket() throws IOException {
        buffer = connection.getSmscLink().read(buffer);
        return decodePacket(buffer);
    }
    
    private SMPPPacket decodePacket(byte[] array) {
        int commandId = SMPPIO.bytesToInt(array, 4);
        SMPPPacket packet = PacketFactory.newInstance(commandId);
        if (packet != null) {
            packet.readFrom(array, 0);
        } else {
            LOG.warn("Received an unparsable packet with command id {}",
                    commandId);
        }
        return packet;
    }
}
