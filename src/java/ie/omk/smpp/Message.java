package ie.omk.smpp;

import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.APIConfig;
import ie.omk.smpp.util.MessageEncoding;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.version.MandatoryParameter;
import ie.omk.smpp.version.SMPPVersion;
import ie.omk.smpp.version.VersionFactory;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A base class representing a message. A message consists of the message data
 * along with the {@link ie.omk.smpp.util.MessageEncoding}, indicating what
 * format the message is in (usually specifying the character encoding of a text
 * message). This class supports concatenated short messages, providing
 * methods to split a long message into a set of packets that can be delivered
 * via an SMPP connection.
 * <p>
 * This class determines if a message should be split into multiple
 * concatenated messages based on a <code>segmentSize</code>. When the
 * SMPP version in use supports optional parameters, there is no way to
 * programatically determine the maximum size of a segment. The SMPP
 * specification states <i>"The maximum size is SMSC and network implementation
 * specific."</i> (section 5.3.2.32, v3.4 spec, issue 1.2). A default can
 * be configured via the {@link ie.omk.smpp.util.APIConfig} but if none is
 * specified, a default value of <code>254</code> is used.
 * </p>
 * <p>
 * Applications sending concatenated short messages using optional parameters
 * will probably want to specify a max segment size over the default using
 * the {@link #setSegmentSize(int)} method.
 * </p>
 * @see ie.omk.smpp.MessageBuilder
 * @version $Id:$
 */
public abstract class Message implements Serializable {
    static final int UDHI = 0x40;
    private static final long serialVersionUID = 1;
    private static final AtomicInteger SAR_IDENT_SEQUENCE = new AtomicInteger(0);

    protected MessageEncoding encoding;
    private int sarIdentifier;
    private int segmentSize;

    public Message(MessageEncoding encoding) {
        this.encoding = encoding;
        sarIdentifier = SAR_IDENT_SEQUENCE.incrementAndGet();
        SMPPVersion version = VersionFactory.getDefaultVersion();
        if (version.isSupportOptionalParams()) {
            segmentSize =
                APIConfig.getInstance().getInt(APIConfig.SEGMENT_SIZE, 254);
        } else {
            segmentSize = version.getMaxLength(MandatoryParameter.SHORT_MESSAGE);
        }
    }
    
    public int getSarIdentifier() {
        return sarIdentifier;
    }

    public void setSarIdentifier(int sarIdentifier) {
        this.sarIdentifier = sarIdentifier;
    }

    public int getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(int segmentSize) {
        this.segmentSize = segmentSize;
    }

    public abstract byte[] getMessage();

    /**
     * Determine if this message&apos;s data is longer than the allowed size
     * for a single SMPP packet and therefore needs to be delivered using
     * concatenated short messages.
     * @param useOptionalParameters <code>true</code> to use optional parameters
     * or <code>false</code> to use mandatory parameters.
     * @return <code>true</code> if the message should be split into multiple
     * packets, <code>false</code> if the message can be delivered in a single
     * packet.
     */
    public boolean isConcatenated(boolean useOptionalParameters) {
        return isConcatenated(getMessage(), useOptionalParameters);
    }
    
    /**
     * Get a single {@link SubmitSM} packet containing this message.
     * @param useOptionalParameters <code>true</code> to use optional
     * parameters, <code>false</code> to place the message in the mandatory
     * parameters.
     * @return A <code>SubmitSM</code> packet wrapping the message.
     */
    public SubmitSM getSubmitSM(boolean useOptionalParameters) {
        SubmitSM submitSM =
            (SubmitSM) PacketFactory.newInstance(SMPPPacket.SUBMIT_SM);
        submitSM.setDataCoding(encoding.getDataCoding());
        setPayloadParameter(submitSM, useOptionalParameters);
        return submitSM;
    }
    
    /**
     * Get a single {@link DeliverSM} packet containing this message.
     * @param useOptionalParameters <code>true</code> to use optional
     * parameters, <code>false</code> to place the message in the mandatory
     * parameters.
     * @return A <code>DeliverSM</code> packet wrapping the message.
     */
    public DeliverSM getDeliverSM(boolean useOptionalParameters) {
        DeliverSM deliverSM =
            (DeliverSM) PacketFactory.newInstance(SMPPPacket.DELIVER_SM);
        deliverSM.setDataCoding(encoding.getDataCoding());
        setPayloadParameter(deliverSM, useOptionalParameters);
        return deliverSM;
    }

    /**
     * Get a single {@link DataSM} packet containing this message.
     * @return A <code>DataSM</code> packet wrapping the message.
     */
    public DataSM getDataSM() {
        DataSM dataSM = (DataSM) PacketFactory.newInstance(SMPPPacket.DATA_SM);
        dataSM.setDataCoding(encoding.getDataCoding());
        setPayloadParameter(dataSM, true);
        return dataSM;
    }
    
    /**
     * Get an array of {@link SubmitSM} packets representing a
     * concatenated short message.
     * @param useOptionalParameters <code>true</code> to use optional
     * parameters, <code>false</code> to place the message in the mandatory
     * parameters.
     * @return An array of <code>SubmitSM</code> packets.
     */
    public SubmitSM[] getSubmitSMPackets(boolean useOptionalParameters) {
        return (SubmitSM[]) getSMPackets(SMPPPacket.SUBMIT_SM,
                new SubmitSM[0],
                useOptionalParameters);
    }

    /**
     * Get an array of {@link DeliverSM} packets representing a
     * concatenated short message.
     * @param useOptionalParameters <code>true</code> to use optional
     * parameters, <code>false</code> to place the message in the mandatory
     * parameters.
     * @return An array of <code>DeliverSM</code> packets.
     */
    public DeliverSM[] getDeliverSMPackets(boolean useOptionalParameters) {
        return (DeliverSM[]) getSMPackets(SMPPPacket.DELIVER_SM,
                new DeliverSM[0],
                useOptionalParameters);
    }

    /**
     * Get an array of {@link DataSM} packets representing a
     * concatenated short message.
     * @return An array of <code>DataSM</code> packets.
     */
    public DataSM[] getDataSMPackets() {
        byte[][] segments = getSegments(true);
        DataSM[] packets = new DataSM[segments.length];
        fillWithOptionalParameters(segments, packets, SMPPPacket.DATA_SM);
        return packets;
    }
    
    private void setPayloadParameter(SMPPPacket packet, boolean useOptionalParameters) {
        if (useOptionalParameters) {
            packet.setOptionalParameter(Tag.MESSAGE_PAYLOAD, getMessage());
        } else {
            ((SMPacket) packet).setMessage(getMessage());
        }
    }
    
    private SMPacket[] getSMPackets(int commandId,
            SMPacket[] packets,
            boolean useOptionalParameters) {
        byte[][] segments = getSegments(useOptionalParameters);
        if (packets.length < segments.length) {
            packets = (SMPacket[]) Array.newInstance(
                    packets.getClass().getComponentType(), segments.length);
        }
        if (useOptionalParameters) {
            fillWithOptionalParameters(segments, packets, commandId);
        } else {
            for (int i = 0; i < segments.length; i++) {
                packets[i] = (SMPacket) PacketFactory.newInstance(commandId);
                // UDHI length is 5
                segments[i][0] = (byte) 5;
                // 0 means this is a concatenated message
                segments[i][1] = (byte) 0;
                // Information length is 3 bytes
                segments[i][2] = (byte) 3;
                segments[i][3] = (byte) sarIdentifier;
                segments[i][4] = (byte) segments.length;
                segments[i][5] = (byte) i;
                packets[i].setEsmClass(packets[i].getEsmClass() | UDHI);
                packets[i].setMessage(segments[i]);
            }
        }
        return packets;
    }
    
    private boolean isConcatenated(byte[] message, boolean useOptionalParameters) {
        int realLength = (message.length * encoding.getCharSize()) / 8;
        if (useOptionalParameters) {
            return realLength > segmentSize;
        } else {
            return realLength > 140;
        }
    }
    
    private void fillWithOptionalParameters(
            byte[][] segments, SMPPPacket[] packets, int commandId) throws BadCommandIDException {
        for (int i = 0; i < segments.length; i++) {
            packets[i] = PacketFactory.newInstance(commandId);
            packets[i].setOptionalParameter(Tag.MESSAGE_PAYLOAD, segments[i]);
            packets[i].setOptionalParameter(
                    Tag.SAR_SEGMENT_SEQNUM, new Integer(i));
            packets[i].setOptionalParameter(
                    Tag.SAR_TOTAL_SEGMENTS, new Integer(segments.length));
            packets[i].setOptionalParameter(
                    Tag.SAR_MSG_REF_NUM, new Integer(sarIdentifier));
        }
    }

    private byte[][] getSegments(boolean useOptionalParameters) {
        if (useOptionalParameters) {
            return getSegments(segmentSize, 0);
        } else {
            // When not using optional parameters, we're basically following
            // the GSM spec: so the payload may only be 140 bytes.
            int segmentSize;
            if (encoding.getCharSize() == 7) {
                // 160 minus 7 for UDH and padding
                segmentSize = 153;
            } else {
                // 140 minus 6 for UDH
                segmentSize = 134;
            }
            return getSegments(segmentSize, 6);
        }
    }
    
    private byte[][] getSegments(int segmentSize, int udhPadding) {
        byte[] message = getMessage();
        int charSize = encoding.getCharSize() / 8;
        if (charSize < 1) {
            charSize = 1;
        }
        int segmentSizeChars = segmentSize / charSize;
        // Have to recalculate, as it might round to a different number.
        int segmentSizeBytes = segmentSizeChars * charSize;
        int messageSize = message.length / charSize;
        int numSegments = messageSize / segmentSizeChars;
        int remainder = messageSize % segmentSizeChars;
        if (remainder != 0) {
            numSegments++;
        }
        byte[][] segments = new byte[numSegments][];
        int offset = 0;
        for (int i = 0; i < segments.length - 1; i++, offset += segmentSizeBytes) {
            segments[i] = new byte[segmentSizeBytes + udhPadding];
            System.arraycopy(
                    message,
                    offset,
                    segments[i],
                    udhPadding,
                    segmentSizeBytes);
        }
        segments[segments.length - 1] =
            new byte[(remainder * charSize) + udhPadding];
        System.arraycopy(
                message, 
                offset,
                segments[segments.length - 1],
                udhPadding,
                remainder * charSize);
        return segments;
    }
}
