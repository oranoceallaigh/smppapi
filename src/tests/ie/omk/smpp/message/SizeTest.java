package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.version.SMPPVersion;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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

    public void testPacketsWithDefaultConstructor() throws Exception {
        testPacketSizes(false);
    }
    
    public void testPacketsWithFieldsSet() throws Exception {
        testPacketSizes(true);
    }
    
    /**
     * Test that packets report their sizes correctly. The <code>filled</code>
     * parameter determines if the test run uses all the default values for the
     * fields as determined by a message's constructor or if the test will fill
     * in test values for all relevant fields in the message.
     */
    private void testPacketSizes(boolean filled) throws Exception {
        for (int i = 0; i < classList.length; i++) {
            String className = classList[i].getName();
            className = className.substring(className.lastIndexOf('.'));

            try {
                SMPPPacket p = (SMPPPacket) classList[i].newInstance();
                if (filled) {
                    initialiseFields(p);
                }
                testPacket(className, p);
            } catch (Exception x) {
                x.printStackTrace(System.err);
                fail(className + " failed.");
            }
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
    private void testPacket(String n, SMPPPacket original) throws Exception {
        SMPPPacket deserialized;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        original.writeTo(out);

        byte[] array = out.toByteArray();
        int id = SMPPIO.bytesToInt(array, 4, 4);
        deserialized = PacketFactory.newInstance(id);
        if (deserialized == null) {
            fail(n + " - PacketFactory returned null for Id 0x"
                    + Integer.toHexString(id));
            return;
        }
        deserialized.readFrom(array, 0);

        assertEquals(n + " serialized length does not match.",
                original.getLength(), array.length);
        assertEquals(n + " deserialized length does not match.",
                array.length, deserialized.getLength());
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
            if (param.getType() == Types.LIST) {
                List<Object> list = new ArrayList<Object>();
                for (int i = 0; i < random.nextInt(15) + 5; i++) {
                    addBodyParams(param.getListType(), list, random);
                }
                body.set(param.getLinkIndex(), new Integer(list.size()));
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
        if (param.getType() == Types.ADDRESS) {
            body.add(new Address(0, 0, "1234567890"));
        } else if (param.getType() == Types.INTEGER) {
            body.add(new Integer(random.nextInt(255)));
        } else if (param.getType() == Types.CSTRING) {
            body.add(new String("C-String"));
        } else if (param.getType() == Types.DATE) {
            Calendar calendar = Calendar.getInstance();
            SMPPDate date = SMPPDate.getAbsoluteInstance(calendar);
            body.add(date);
        } else if (param.getType() == Types.BYTES) {
            int len = param.getLength();
            if (len < 0) {
                len = 10;
            }
            byte[] data = new byte[len];
            random.nextBytes(data);
            body.set(param.getLinkIndex(), new Integer(len));
            body.add(data);
        } else if (param.getType() == Types.DEST_TABLE) {
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
            body.set(param.getLinkIndex(), new Integer(table.size()));
            body.add(table);
        }
    }
}
