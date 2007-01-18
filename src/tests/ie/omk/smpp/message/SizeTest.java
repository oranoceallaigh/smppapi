package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.BytesParamDescriptor;
import ie.omk.smpp.message.param.DestinationTableParamDescriptor;
import ie.omk.smpp.message.param.ListParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.version.SMPPVersion;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

/**
 * Test that the value reported by <code>getLength</code> matches the actual
 * length a packet serializes to and deserializes from.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class SizeTest extends TestCase {

    // List of all the message types.
    private static final Class[] classList = {
        AlertNotification.class,
        BindReceiver.class,
        BindReceiverResp.class,
        BindTransceiver.class,
        BindTransceiverResp.class,
        BindTransmitter.class,
        BindTransmitterResp.class,
        CancelSM.class,
        CancelSMResp.class,
        DataSM.class,
        DataSMResp.class,
        DeliverSM.class,
        DeliverSMResp.class,
        EnquireLink.class,
        EnquireLinkResp.class,
        GenericNack.class,
        ParamRetrieve.class,
        ParamRetrieveResp.class,
        QueryLastMsgs.class,
        QueryLastMsgsResp.class,
        QueryMsgDetails.class,
        QueryMsgDetailsResp.class,
        QuerySM.class,
        QuerySMResp.class,
        ReplaceSM.class,
        ReplaceSMResp.class,
        SubmitMulti.class,
        SubmitMultiResp.class,
        SubmitSM.class,
        SubmitSMResp.class,
        Unbind.class,
        UnbindResp.class,
    };

    public void testSizesMatchWithSerializationWithDefaultInitalisation() throws Exception {
        String className = "";
        try {
            for (Class<? extends SMPPPacket> clazz : classList) {
                className = clazz.getName().replaceFirst(".+\\.([^.]+)$", "$1");
                SMPPPacket packet = clazz.newInstance();
                testGetLengthMatchesReality(packet);
            }
        } catch (Exception x) {
            fail("Failed on " + className);
        }
    }
    
    public void testSizesMatchWithSerializationWithFieldsSet() throws Exception {
        String className = "";
        try {
            for (Class<? extends SMPPPacket> clazz : classList) {
                className = clazz.getName().replaceFirst(".+\\.([^.]+)$", "$1");
                SMPPPacket packet = clazz.newInstance();
                initialiseFields(packet);
                testGetLengthMatchesReality(packet);
            }
        } catch (Exception x) {
            fail("Failed on " + className);
        }
    }
    
    /**
     * Test that after serializing and deserializing a packet, the mandatory
     * parameters all match.
     * @throws Exception
     */
    public void testDeserializedFieldsMatchOriginalPacket() throws Exception {
        String className = "";
        try {
            for (Class<? extends SMPPPacket> clazz : classList) {
                className = clazz.getName().replaceFirst(".+\\.([^.]+)$", "$1");
                SMPPPacket original = clazz.newInstance();
                SMPPPacket decodedPacket = clazz.newInstance();
                initialiseFields(original);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                original.writeTo(out);
                decodedPacket.readFrom(out.toByteArray(), 0);
                testPacketFieldsAreEqual(original, decodedPacket);
            }
        } catch (Exception x) {
            x.printStackTrace(System.err);
            fail("Failed on " + className);
        }
    }
    
    private void testPacketFieldsAreEqual(SMPPPacket packet1, SMPPPacket packet2) {
        Object[] params1 = packet1.getMandatoryParameters();
        Object[] params2 = packet2.getMandatoryParameters();
        assertNotNull(params1);
        assertNotNull(params2);
        assertEquals(params1.length, params2.length);
        for (int i = 0; i < params1.length; i++) {
            if (params1[i].getClass().isArray()) {
                compareArrays(params1[i], params2[i]);
            } else {
                assertEquals(packet1.getClass().getName(), params1[i], params2[i]);
            }
        }
    }
    
    private void compareArrays(Object array1, Object array2) {
        Class<?> type = array1.getClass().getComponentType();
        if (type.isPrimitive()) {
            if (type.isAssignableFrom(Boolean.class)) {
                assertTrue(Arrays.equals((boolean[]) array1, (boolean[]) array2));
            } else if (type.isAssignableFrom(Byte.class)) {
                assertTrue(Arrays.equals((byte[]) array1, (byte[]) array2));
            } else if (type.isAssignableFrom(Integer.class)) {
                assertTrue(Arrays.equals((int[]) array1, (int[]) array2));
            } else if (type.isAssignableFrom(Long.class)) {
                assertTrue(Arrays.equals((long[]) array1, (long[]) array2));
            }
        } else {
            assertTrue(Arrays.equals((Object[]) array1, (Object[]) array2));
        }
    }
    
    /**
     * Test an individual packet. This method serializes the packet to a byte
     * array and then deserializes a second packet from that byte array. It then
     * asserts that <code>getLength</code> on the original packet matches the
     * length of the byte array and that the length of the byte array matches
     * the value returned from <code>getLength</code> on the deserialized
     * packet.
     */
    private void testGetLengthMatchesReality(SMPPPacket packet) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        packet.writeTo(out);

        byte[] array = out.toByteArray();
        int id = SMPPIO.bytesToInt(array, 4);
        SMPPPacket deserialized = PacketFactory.newInstance(id);
        if (deserialized == null) {
            fail("Got null for command Id 0x" + Integer.toHexString(id));
        }
        deserialized.readFrom(array, 0);
        assertEquals(packet.getLength(), array.length);
        assertEquals(array.length, deserialized.getLength());
    }

    /**
     * Initialise field contents for the filled field test.
     */
    private void initialiseFields(SMPPPacket packet) throws ie.omk.smpp.SMPPException {
        Random random = new Random();
        List<Object> body = new ArrayList<Object>();
        BodyDescriptor descriptor = packet.getBodyDescriptor();
        if (descriptor == null) {
            return;
        }
        for (ParamDescriptor param : descriptor.getBody()) {
            if (param instanceof ListParamDescriptor) {
                List<Object> list = new ArrayList<Object>();
                for (int i = 0; i < random.nextInt(15) + 5; i++) {
                    addBodyParams(((ListParamDescriptor) param).getListType(),
                            list, random);
                }
                body.set(((ListParamDescriptor) param).getLinkIndex(),
                        new Integer(list.size()));
                body.add(list);
            } else {
                addBodyParams(param, body, random);
            }
        }
        // Hack for bind packets - can't have a random version number..
        if (packet instanceof Bind) {
            body.set(3, SMPPVersion.V34.getVersionID());
        }
        packet.setSequenceNum(189);
        packet.setMandatoryParameters(body);
    }
    
    private void addBodyParams(ParamDescriptor param, List<Object> body, Random random) {
        // We make an assumption in this test that any parameter which has
        // another parameter specifying its size (for example, a byte array
        // or destination table) that the size parameter immediately precedes
        // the parameter being specified.
        if (param == ParamDescriptor.ADDRESS) {
            body.add(new Address(0, 0, "1234567890"));
        } else if (param == ParamDescriptor.INTEGER1) {
            body.add(new Integer(random.nextInt(255)));
        } else if (param == ParamDescriptor.INTEGER2) {
            body.add(new Integer(random.nextInt(65535)));
        } else if (param == ParamDescriptor.INTEGER4) {
            body.add(new Long(random.nextLong()));
        } else if (param == ParamDescriptor.INTEGER8) {
            body.add(new Long(random.nextLong()));
        } else if (param == ParamDescriptor.CSTRING) {
            body.add(new String("C-String"));
        } else if (param == ParamDescriptor.DATE) {
            Calendar calendar = Calendar.getInstance();
            SMPPDate date = SMPPDate.getAbsoluteInstance(calendar);
            body.add(date);
        } else if (param instanceof BytesParamDescriptor) {
            byte[] data = new byte[random.nextInt(90) + 10];
            random.nextBytes(data);
//            body.set(((BytesParamDescriptor) param).getLinkIndex(),
//                    new Integer(data.length));
            body.add(data);
        } else if (param instanceof DestinationTableParamDescriptor) {
            DestinationTable table = new DestinationTable();
            table.add(new Address(0, 0, "111111111"));
            table.add("distList1");
            table.add(new Address(0, 0, "222222222"));
            table.add(new Address(0, 0, "333333333"));
            table.add("distList2");
            table.add(new Address(0, 0, "444444444"));
            table.add("distList3");
            table.add("distList4");
            table.add("distList5");
//            body.set(((DestinationTableParamDescriptor) param).getLinkIndex(),
//                    new Integer(table.size()));
            body.add(table);
        }
    }
}
