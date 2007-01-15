package ie.omk.smpp;

import ie.omk.smpp.message.DataSM;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.Latin1Encoding;
import ie.omk.smpp.util.UCS2Encoding;

import java.util.Arrays;

import junit.framework.TestCase;

public class MessageTest extends TestCase {

    public void testWith7BitEncoding() throws Exception {
    }
    
    public void testUnconcatenatedWith8BitEncodingAndOptionalParameters() throws Exception {
        TextMessage message = new TextMessage("I am a short message.");
        message.setAlphabet(new Latin1Encoding());
        assertTrue(message.getMessageText().length() < message.getSegmentSize());
        
        SubmitSM[] smPackets = message.getSubmitSMPackets(true);
        assertNotNull(smPackets);
        assertEquals(1, smPackets.length);
        byte[] packetMessage =
            (byte[]) smPackets[0].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertTrue(Arrays.equals(message.getMessage(), packetMessage));

        DeliverSM[] dmPackets = message.getDeliverSMPackets(true);
        assertNotNull(dmPackets);
        assertEquals(1, dmPackets.length);
        packetMessage =
            (byte[]) dmPackets[0].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertTrue(Arrays.equals(message.getMessage(), packetMessage));

        DataSM[] dataPackets = message.getDataSMPackets();
        assertNotNull(dataPackets);
        assertEquals(1, dataPackets.length);
        packetMessage =
            (byte[]) dataPackets[0].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertTrue(Arrays.equals(message.getMessage(), packetMessage));
    }
    
    public void testConcatenatedWith8BitEncodingAndOptionalParameters() throws Exception {
        Latin1Encoding encoding = new Latin1Encoding();
        TextMessage message =
            new TextMessage("This message should be split in two.");
        message.setAlphabet(encoding);
        message.setSegmentSize(20);
        assertTrue(message.getMessageText().length() > message.getSegmentSize());
        
        SubmitSM[] smPackets = message.getSubmitSMPackets(true);
        assertNotNull(smPackets);
        assertEquals(2, smPackets.length);
        byte[] packetMessage =
            (byte[]) smPackets[0].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertEquals("This message should ", encoding.decodeString(packetMessage));
        packetMessage =
            (byte[]) smPackets[1].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertEquals("be split in two.", encoding.decodeString(packetMessage));

        int expectedSarIdentifier = ((Integer) smPackets[0].getOptionalParameter(
                Tag.SAR_MSG_REF_NUM)).intValue();
        for (int i = 0; i < smPackets.length; i++) {
            int seqNum = ((Integer) smPackets[i].getOptionalParameter(
                    Tag.SAR_SEGMENT_SEQNUM)).intValue();
            int total = ((Integer) smPackets[i].getOptionalParameter(
                    Tag.SAR_TOTAL_SEGMENTS)).intValue();
            int sarIdentifier = ((Integer) smPackets[i].getOptionalParameter(
                    Tag.SAR_MSG_REF_NUM)).intValue();
            assertEquals(expectedSarIdentifier, sarIdentifier);
            assertEquals(smPackets.length, total);
            assertEquals(i, seqNum);
        }
    }

    public void testConcatenatedWith8BitEncodingAndUDH() throws Exception {
        Latin1Encoding encoding = new Latin1Encoding();
        // Message is 180 characters.
        final String MSG = "123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890";
        final int SEGMENT_LEN = 134;
        TextMessage message = new TextMessage(MSG);
        message.setAlphabet(encoding);
        
        DeliverSM[] dmPackets = message.getDeliverSMPackets(false);
        assertNotNull(dmPackets);
        assertEquals(2, dmPackets.length);
        byte[] packetMessage = (byte[]) dmPackets[0].getMessage();
        assertEquals(140, packetMessage.length);
        assertEquals((byte) 5, packetMessage[0]);
        assertEquals((byte) 0, packetMessage[1]);
        assertEquals((byte) 3, packetMessage[2]);
        assertEquals((byte) dmPackets.length, packetMessage[4]);
        assertEquals((byte) 0, packetMessage[5]);
        assertEquals(MSG.substring(0, SEGMENT_LEN),
                encoding.decodeString(packetMessage, 6, packetMessage.length - 6));

        packetMessage = (byte[]) dmPackets[1].getMessage();
        assertEquals(52, packetMessage.length);
        assertEquals((byte) 5, packetMessage[0]);
        assertEquals((byte) 0, packetMessage[1]);
        assertEquals((byte) 3, packetMessage[2]);
        assertEquals((byte) dmPackets.length, packetMessage[4]);
        assertEquals((byte) 1, packetMessage[5]);
        assertEquals(MSG.substring(SEGMENT_LEN),
                encoding.decodeString(packetMessage, 6, packetMessage.length - 6));
    }
    
    public void testConcatenatedWith16BitEncodingAndOptionalParameters() throws Exception {
        UCS2Encoding encoding = new UCS2Encoding();
        TextMessage message =
            new TextMessage("Characters cannot be split across segments.");
        message.setAlphabet(encoding);
        message.setSegmentSize(27);
        assertTrue(message.getMessageText().length() > message.getSegmentSize());
        
        DataSM[] dataPackets = message.getDataSMPackets();
        assertNotNull(dataPackets);
        assertEquals(4, dataPackets.length);
        byte[] packetMessage =
            (byte[]) dataPackets[0].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertEquals("Characters ca", encoding.decodeString(packetMessage));
        packetMessage =
            (byte[]) dataPackets[1].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertEquals("nnot be split", encoding.decodeString(packetMessage));
        packetMessage =
            (byte[]) dataPackets[2].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertEquals(" across segme", encoding.decodeString(packetMessage));
        packetMessage =
            (byte[]) dataPackets[3].getOptionalParameter(Tag.MESSAGE_PAYLOAD);
        assertEquals("nts.", encoding.decodeString(packetMessage));
    }
}
