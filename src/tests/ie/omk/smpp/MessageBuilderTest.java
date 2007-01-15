package ie.omk.smpp;

import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.ASCIIEncoding;
import ie.omk.smpp.util.AlphabetEncoding;
import junit.framework.TestCase;

public class MessageBuilderTest extends TestCase {

    public void testGetMessageFailsOnIncompleteMessage() throws Exception {
        MessageBuilder builder = new MessageBuilder();
        assertFalse(builder.isComplete());
        try {
            builder.getMessage();
            fail("Should exception on incomplete message.");
        } catch (IllegalStateException x) {
        }
    }
    
    public void testAssembleWithOptionalParameters() throws Exception {
        AlphabetEncoding encoding = new ASCIIEncoding();
        DataSM[] segments = getSegmentsWithOptionalParameters(encoding);
        MessageBuilder builder = new MessageBuilder();
        assertFalse(builder.isComplete());
        builder.add(segments[0]);
        assertFalse(builder.isComplete());
        builder.add(segments[1]);
        assertFalse(builder.isComplete());
        builder.add(segments[2]);
        assertTrue(builder.isComplete());
        Message message = builder.getMessage();
        assertTrue(message instanceof TextMessage);
        TextMessage textMessage = (TextMessage) message;
        assertTrue(textMessage.getAlphabet() instanceof ASCIIEncoding);
        assertEquals("Segment_1Segment_2Segment_3", textMessage.getMessageText());
    }

    public void testAssembleOutOfOrderWithOptionalParameters() throws Exception {
        AlphabetEncoding encoding = new ASCIIEncoding();
        DataSM[] segments = getSegmentsWithOptionalParameters(encoding);
        MessageBuilder builder = new MessageBuilder();
        assertFalse(builder.isComplete());
        builder.add(segments[2]);
        assertFalse(builder.isComplete());
        builder.add(segments[0]);
        assertFalse(builder.isComplete());
        builder.add(segments[1]);
        assertTrue(builder.isComplete());
        Message message = builder.getMessage();
        assertTrue(message instanceof TextMessage);
        TextMessage textMessage = (TextMessage) message;
        assertTrue(textMessage.getAlphabet() instanceof ASCIIEncoding);
        assertEquals("Segment_1Segment_2Segment_3", textMessage.getMessageText());
    }
    
    public void testAssembleWithUDH() throws Exception {
        // TODO
    }
    
    private DataSM[] getSegmentsWithOptionalParameters(AlphabetEncoding encoding) {
        DataSM[] segments = new DataSM[3];
        segments[0] = new DataSM();
        segments[0].setDataCoding(encoding.getDataCoding());
        createSegment(segments[0], 1, 3, 0, encoding.encodeString("Segment_1"));
        
        segments[1] = new DataSM();
        segments[1].setDataCoding(encoding.getDataCoding());
        createSegment(segments[1], 1, 3, 1, encoding.encodeString("Segment_2"));

        segments[2] = new DataSM();
        segments[2].setDataCoding(encoding.getDataCoding());
        createSegment(segments[2], 1, 3, 2, encoding.encodeString("Segment_3"));
        return segments;
    }
    
    private void createSegment(SMPPPacket packet,
            int id,
            int total,
            int index,
            byte[] payload) {
        packet.setOptionalParameter(Tag.MESSAGE_PAYLOAD, payload);
        packet.setOptionalParameter(Tag.SAR_MSG_REF_NUM, new Integer(id));
        packet.setOptionalParameter(Tag.SAR_TOTAL_SEGMENTS, new Integer(total));
        packet.setOptionalParameter(Tag.SAR_SEGMENT_SEQNUM, new Integer(index));
    }
}
