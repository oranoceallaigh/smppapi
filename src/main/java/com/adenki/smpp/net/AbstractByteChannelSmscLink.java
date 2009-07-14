package com.adenki.smpp.net;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketDecoderImpl;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.util.PacketEncoderImpl;

/**
 * Abstract base class for {@link SmscLink} implementations which operate
 * on Java NIO {@link ByteChannel ByteChannels}.
 * <p>
 * Sub-classes should override the {@link #connect()} method to
 * perform any implementation-specific connection actions.
 * </p>
 * <p>
 * The {@link #setChannel(ByteChannel)} method <strong>must</strong> be
 * invoked before any input/output operations are attempted and should
 * probably be invoked as part of the sub-class' <tt>connect()</tt>
 * implementation. This base class' <tt>connect()</tt> does not require
 * the channel to be set before being invoked.
 * </p>
 * @version $Id$
 */
public abstract class AbstractByteChannelSmscLink extends AbstractSmscLink {

    private final Logger log;
    private ReadableByteChannel inChannel;
    private WritableByteChannel outChannel;
    private ByteBuffer inBuffer;
    private ByteBuffer outBuffer;
    private PacketEncoder encoder;
    private PacketDecoder decoder;
    
    protected AbstractByteChannelSmscLink() {
        log = LoggerFactory.getLogger(getClass());
    }

    /**
     * Connect to the SMSC. Sub-classes of this class <strong>must</strong>
     * ensure they call this method so that the buffers get allocated.
     */
    public void connect() throws IOException {
        APIConfig cfg = APIConfigFactory.getConfig();
        int initialInBufSize = cfg.getInt(APIConfig.LINK_BUFFERSIZE_IN, 1024);
        int initialOutBufSize = cfg.getInt(APIConfig.LINK_BUFFERSIZE_OUT, 1024);
        allocateIn(initialInBufSize, false);
        allocateOut(initialOutBufSize);
        // inBuffer should be in drain mode initially
        inBuffer.flip();
    }
    
    public SMPPPacket read() throws IOException {
        if (inChannel == null) {
            throw new IOException("inChannel has not been set.");
        }
        try {
            if (!isFullPacketAvailable(inBuffer)) {
                clearBuffer(inBuffer);
                int byteCount = inBuffer.limit() - inBuffer.remaining();
                while (byteCount < 4) {
                    byteCount += inChannel.read(inBuffer);
                }
                int commandLen = inBuffer.getInt(0);
                if (inBuffer.capacity() < commandLen) {
                    allocateIn(commandLen, true);
                }
                while (byteCount < commandLen) {
                    byteCount += inChannel.read(inBuffer);
                }
                inBuffer.flip();
            }
            return decodePacket(inBuffer);
        } catch (SocketTimeoutException x) {
            throw new ReadTimeoutException(x);
        }
    }
    
    public void write(SMPPPacket packet, boolean withTlvs) throws IOException {
        if (outChannel == null) {
            throw new IOException("outChannel has not been set.");
        }
        int length = packet.getLength();
        if (outBuffer.capacity() < length) {
            allocateOut(length);
        } else {
            outBuffer.clear();
        }
        packet.writeTo(encoder, withTlvs);
        outBuffer.flip();
        outChannel.write(outBuffer);
    }
    
    public WritableByteChannel getOutChannel() {
        return outChannel;
    }
    
    public ReadableByteChannel getInChannel() {
        return inChannel;
    }
    
    /**
     * Set the channels to read from and write to. Sub-classes
     * <strong>must</strong> call this method before I/O is attempted.
     * <p>
     * On {@link #disconnect()}, sub-classes should set the channels to
     * <tt>null</tt>.
     * </p>
     * @param inChannel The byte channel to read from.
     * @param outChannel The byte channel to write to.
     */
    protected void setChannels(
            ReadableByteChannel inChannel,
            WritableByteChannel outChannel) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
    }
    
    /**
     * Test if a full packet is available in a byte buffer.
     * @param buffer The buffer to test for a full packet.
     * @return <tt>true</tt> if <tt>buffer.remaining()</tt> returns a value
     * greater than or equal to the next packet's command length, which
     * is determined by calling <tt>buffer.getInt(buffer.position() + 4)</tt>.
     * <tt>false</tt> if there are insufficient bytes in the buffer.
     */
    protected boolean isFullPacketAvailable(ByteBuffer buffer) {
        if (buffer.remaining() > 3) {
            int commandLen = buffer.getInt(buffer.position());
            return buffer.remaining() >= commandLen;
        } else {
            return false;
        }
    }
    
    private SMPPPacket decodePacket(ByteBuffer buffer) throws IOException {
        int commandId = buffer.getInt(buffer.position() + 4);
        SMPPPacket packet = packetFactory.newInstance(commandId);
        packet.readFrom(decoder);
        return packet;
    }
    
    private void allocateIn(int capacity, boolean copy) {
        ByteBuffer oldBuffer = inBuffer;
        inBuffer = allocateBuffer(capacity);
        decoder = new PacketDecoderImpl(inBuffer);
        if (copy) {
            oldBuffer.flip();
            byte[] bytes = new byte[oldBuffer.remaining()];
            oldBuffer.get(bytes);
            inBuffer.put(bytes);
        }
    }
    
    private void allocateOut(int capacity) {
        outBuffer = allocateBuffer(capacity);
        encoder = new PacketEncoderImpl(outBuffer);
    }
    
    private ByteBuffer allocateBuffer(int size) {
        ByteBuffer buf;
        APIConfig cfg = APIConfigFactory.getConfig();
        boolean useDirectBuffers =
            cfg.getBoolean(APIConfig.IO_USE_DIRECT_BUFFERS, true);
        if (useDirectBuffers) {
            log.debug("Allocating a direct buffer of size {}", size);
            buf = ByteBuffer.allocateDirect(size);
        } else {
            log.debug("Allocating a buffer of size {}", size);
            buf = ByteBuffer.allocate(size);
        }
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf;
    }
    
    private void clearBuffer(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        buffer.clear();
        buffer.put(bytes);
    }
}
