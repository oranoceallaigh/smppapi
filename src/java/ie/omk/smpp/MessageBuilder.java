package ie.omk.smpp;

import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPacket;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.util.MessageEncoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class for assembling concatenated short messages.
 * @version $Id:$
 */
public class MessageBuilder {
    // TODO: remove illegalargumentexception
    private static final String PACKET_NOT_PART = "Packet is not part of this message";
    private static final String MESSAGE_NOT_COMPLETE = "Message is not yet complete.";
    private byte[][] segments;
    private int sarIdentifier;
    private int dataCoding;
    
    public MessageBuilder() {
    }
    
    public boolean isComplete() {
        boolean complete = true;
        if (segments != null) {
            for (byte[] segment : segments) {
                if (segment == null) {
                    complete = false;
                    break;
                }
            }
        } else {
            complete = false;
        }
        return complete;
    }
    
    public void add(SMPacket packet) {
        if (!addFromOptionalParameters(packet)) {
            addFromMandatory(packet);
        }
    }
    
    public void add(DataSM dataSM) {
        addFromOptionalParameters(dataSM);
    }
    
    public byte[] getRawMessage() {
        if (!isComplete()) {
            throw new IllegalStateException(MESSAGE_NOT_COMPLETE);
        }
        return assembleSegments();
    }
    
    /**
     * Get a text message using the specified encoding, overriding the data
     * coding value read from the packets.
     * @param encoding The alphabet to use to decode the message.
     * @return A text message object.
     */
    public TextMessage getTextMessage(AlphabetEncoding encoding) {
        if (!isComplete()) {
            throw new IllegalStateException(MESSAGE_NOT_COMPLETE);
        }
        byte[] data = assembleSegments();
        TextMessage message = new TextMessage();
        message.setAlphabet(encoding);
        message.setMessageText(encoding.decodeString(data));
        return message;
    }

    /**
     * Get the message object. The returned type depends on the data coding
     * value present.
     * @return A concrete implementation of the {@link Message} abstract class.
     */
    public Message getMessage() {
        Message message = null;
        MessageEncoding encoding = EncodingFactory.getInstance().getEncoding(dataCoding);
        if (encoding != null) {
            if (encoding instanceof AlphabetEncoding) {
                message = getTextMessage((AlphabetEncoding) encoding);
            }
        }
        if (message == null) {
            message = new BinaryMessage();
            ((BinaryMessage) message).setMessage(assembleSegments());
        }
        return message;
    }
    
    private boolean addFromOptionalParameters(SMPPPacket packet) {
        byte[] segment =
            (byte[]) packet.getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        if (segment == null) {
            return false;
        }
        if (segments == null) {
            sarIdentifier = getSarIdentifier(packet);
            int total = getSarTotal(packet);
            segments = new byte[total][];
            if (packet.getCommandId() == SMPPPacket.DATA_SM) {
                dataCoding = ((DataSM) packet).getDataCoding();
            } else {
                dataCoding = ((SMPacket) packet).getDataCoding();
            }
        } else {
            if (getSarIdentifier(packet) != sarIdentifier) {
                throw new IllegalArgumentException(PACKET_NOT_PART);
            }
        }
        int index = getSarIndex(packet);
        segments[index] = segment;
        return true;
    }

    private void addFromMandatory(SMPacket packet) {
        byte[] segment = packet.getMessage();
        if (segment == null) {
            throw new IllegalArgumentException("Packet is not a message segment.");
        }
        if (segments == null) {
            sarIdentifier = getSarIdentifier(segment);
            int total = getSarTotal(segment);
            segments = new byte[total][];
            dataCoding = packet.getDataCoding();
        } else {
            if (getSarIdentifier(segment) != sarIdentifier) {
                throw new IllegalArgumentException(PACKET_NOT_PART);
            }
        }
        int index = getSarIndex(segment);
        segments[index] = new byte[segment.length - 6];
        System.arraycopy(segment, 6, segments[index], 0, segment.length - 6);
    }
    
    private int getSarIdentifier(SMPPPacket packet) {
        Integer id = (Integer) packet.getOptionalParameter(Tag.SAR_MSG_REF_NUM);
        return id.intValue();
    }
    
    private int getSarIdentifier(byte[] data) {
        return (int) data[3] & 0xff;
    }
    
    private int getSarIndex(SMPPPacket packet) {
        Integer index = (Integer) packet.getOptionalParameter(
                Tag.SAR_SEGMENT_SEQNUM);
        return index.intValue();
    }

    private int getSarIndex(byte[] data) {
       return (int) data[5] & 0xff;
    }
    
    private int getSarTotal(SMPPPacket packet) {
        Integer total = (Integer) packet.getOptionalParameter(
                Tag.SAR_TOTAL_SEGMENTS);
        return total.intValue();
    }
    
    private int getSarTotal(byte[] data) {
        return (int) data[4] & 0xff;
    }
    
    private byte[] assembleSegments() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (byte[] segment : segments) {
                out.write(segment);
            }
            return out.toByteArray();
        } catch (IOException x) {
            // ByteArrayOutputStream doesn't throw IOException.
            throw new RuntimeException("Should never happen!", x);
        }
    }
}
