package ie.omk.smpp.message.tlv;

import java.util.Arrays;

import ie.omk.smpp.util.APIConfig;

import junit.framework.TestCase;

public class StringEncoderTest extends TestCase {

    public void testMbloxHackIsEnabledOnWrite() throws Exception {
        final String testString = "TestString";
        APIConfig.getInstance().setProperty(APIConfig.MBLOX_HACK, "true");
        StringEncoder encoder = new StringEncoder();
        assertEquals(10, encoder.getValueLength(null, testString));
        byte[] array = new byte[15];
        Arrays.fill(array, (byte) 0xbe);
        encoder.writeTo(Tag.RECEIPTED_MESSAGE_ID, testString, array, 0);
        assertEquals(0x54, array[0]);
        assertEquals(0x65, array[1]);
        assertEquals(0x73, array[2]);
        assertEquals(0x74, array[3]);
        assertEquals(0x53, array[4]);
        assertEquals(0x74, array[5]);
        assertEquals(0x72, array[6]);
        assertEquals(0x69, array[7]);
        assertEquals(0x6e, array[8]);
        assertEquals(0x67, array[9]);
        assertEquals((byte) 0xbe, array[10]);
        assertEquals((byte) 0xbe, array[11]);
        assertEquals((byte) 0xbe, array[12]);
        assertEquals((byte) 0xbe, array[13]);
        assertEquals((byte) 0xbe, array[14]);
    }

    public void testMbloxHackIsEnabledOnRead() throws Exception {
        final byte[] array = {
                0x54, 0x65, 0x73, 0x74, 0x53, 0x74, 0x72, 0x69, 0x6e, 0x67,
                (byte) 0xbe, (byte) 0xbe, (byte) 0xbe,
        };
        APIConfig.getInstance().setProperty(APIConfig.MBLOX_HACK, "true");
        StringEncoder encoder = new StringEncoder();
        String string =
            (String) encoder.readFrom(Tag.RECEIPTED_MESSAGE_ID, array, 0, 10);
        assertEquals("TestString", string);
    }
}
