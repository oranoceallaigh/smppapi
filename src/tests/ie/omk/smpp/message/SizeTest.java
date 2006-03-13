package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.ErrorAddress;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.PacketFactory;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

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

    public void testPacketsWithDefaultConstructor() {
        testPacketSizes(false);
    }
    
    public void testPacketsWithFieldsSet() {
        testPacketSizes(true);
    }
    
    /**
     * Test that packets report their sizes correctly. The <code>filled</code>
     * parameter determines if the test run uses all the default values for the
     * fields as determined by a message's constructor or if the test will fill
     * in test values for all relevant fields in the message.
     */
    private void testPacketSizes(boolean filled) {
        for (int i = 0; i < classList.length; i++) {
            String className = classList[i].getName();
            className = className.substring(className.lastIndexOf('.'));

            try {
                SMPPPacket p = (SMPPPacket) classList[i].newInstance();
                if (filled) {
                    initialiseFields(p);
                }
                testPacket(className, p);
            } catch (SMPPException x) {
                fail(className + " field initialisation caused an SMPP exception:\n"
                        + x.toString());
            } catch (InstantiationException x) {
                fail(className + " is implemented incorrectly. Exception thrown:\n"
                        + x.toString());
            } catch (IllegalAccessException x) {
                fail(className + " constructor is not public.\n" + x.toString());
            } catch (ExceptionInInitializerError x) {
                fail(className + " constructor threw an exception.\n" + x.toString());
            } catch (SecurityException x) {
                fail("SecurityException instantiating " + className + "\n"
                        + x.toString());
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
    private void testPacket(String n, SMPPPacket original) {
        try {
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
        } catch (BadCommandIDException x) {
            fail(n + " serialization caused BadCommandIDException:\n"
                    + x.toString());
        } catch (SMPPProtocolException x) {
            fail(n + " serialization caused SMPPProtocolException:\n"
                    + x.toString());
        } catch (IOException x) {
            fail(n + " serialization caused I/O Exception:\n" + x.toString());
            return;
        }
    }

    /**
     * Initialise field contents for the filled field test.
     */
    private void initialiseFields(SMPPPacket p)
            throws ie.omk.smpp.SMPPException {
        int id = p.getCommandId();

        switch (id) {
        case SMPPPacket.ALERT_NOTIFICATION:
            p.setSequenceNum(34);
            p.setSource(new Address(0, 0, "445445445"));
            p.setDestination(new Address(0, 0, "67676767676767"));
            break;

        case SMPPPacket.BIND_TRANSMITTER:
        case SMPPPacket.BIND_RECEIVER:
        case SMPPPacket.BIND_TRANSCEIVER:
            Bind b = (Bind) p;
            b.setSequenceNum(1);
            b.setSystemId("sysId");
            b.setSystemType("sysType");
            b.setPassword("passwd");
            b.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534[1-3]"));
            break;

        case SMPPPacket.BIND_TRANSMITTER_RESP:
        case SMPPPacket.BIND_RECEIVER_RESP:
        case SMPPPacket.BIND_TRANSCEIVER_RESP:
            p.setSequenceNum(2);
            BindResp br = (BindResp) p;
            br.setSystemId("SMSC-ID");
            break;

        case SMPPPacket.CANCEL_SM:
            p.setSequenceNum(3);
            p.setMessageId("deadbeef");
            p.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534111"));
            p.setDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534222"));
            break;

        case SMPPPacket.DELIVER_SM:
        case SMPPPacket.SUBMIT_SM:
        case SMPPPacket.SUBMIT_MULTI:
            p.setSequenceNum(5);
            p.setServiceType("svcTp");
            p.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534111"));
            if (id == SMPPPacket.SUBMIT_MULTI) {
                SubmitMulti sml = (SubmitMulti) p;
                sml.addDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                        GSMConstants.GSM_NPI_UNKNOWN, "991293211"));
                sml.addDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                        GSMConstants.GSM_NPI_UNKNOWN, "991293212"));
                sml.addDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                        GSMConstants.GSM_NPI_UNKNOWN, "991293213"));
            } else {
                p.setDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                        GSMConstants.GSM_NPI_UNKNOWN, "65534222"));
            }
            //p.setProtocolId();
            p.setPriority(1);
            p.setDeliveryTime(new SMPPDate(new Date()));
            p.setExpiryTime(new SMPPDate(new Date()));
            p.setRegistered(1);
            p.setReplaceIfPresent(1);
            //p.setDataCoding();
            p.setMessageText("This is a short message");
            break;

        case SMPPPacket.DATA_SM:
            p.setSequenceNum(45);
            p.setServiceType("svcTp");
            p.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534111"));
            p.setDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534222"));
            p.setRegistered(1);
            break;

        case SMPPPacket.DATA_SM_RESP:
            p.setSequenceNum(46);
            p.setMessageId("deadbeef");
            break;

        case SMPPPacket.SUBMIT_SM_RESP:
        case SMPPPacket.SUBMIT_MULTI_RESP:
            p.setSequenceNum(6);
            p.setMessageId("deadbeef");
            if (id == SMPPPacket.SUBMIT_MULTI_RESP) {
                SubmitMultiResp smr = (SubmitMultiResp) p;
                smr.add(new ErrorAddress(0, 0, "12345", 65));
                smr.add(new ErrorAddress(0, 0, "12346", 66));
                smr.add(new ErrorAddress(0, 0, "12347", 90));
                smr.add(new ErrorAddress(0, 0, "99999", 999));
            }
            break;

        case SMPPPacket.PARAM_RETRIEVE:
            p.setSequenceNum(7);
            ((ParamRetrieve) p).setParamName("getParam");
            break;

        case SMPPPacket.PARAM_RETRIEVE_RESP:
            p.setSequenceNum(8);
            ((ParamRetrieveResp) p).setParamValue("paramValue - can be long.");
            break;

        case SMPPPacket.QUERY_LAST_MSGS:
            p.setSequenceNum(9);
            p.setSource(new Address(0, 0, "65534111"));
            ((QueryLastMsgs) p).setMsgCount(45);
            break;

        case SMPPPacket.QUERY_LAST_MSGS_RESP:
            p.setSequenceNum(10);
            QueryLastMsgsResp q = (QueryLastMsgsResp) p;
            q.addMessageId("deadbeef");
            q.addMessageId("cafecafe");
            q.addMessageId("12345678");
            q.addMessageId("77777777");
            q.addMessageId("beefdead");
            break;

        case SMPPPacket.QUERY_MSG_DETAILS:
            p.setSequenceNum(11);
            p.setSource(new Address(0, 0, "65534111"));
            p.setMessageId("deadbeef");
            ((QueryMsgDetails) p).setSmLength(160);
            break;

        case SMPPPacket.QUERY_MSG_DETAILS_RESP:
            p.setSequenceNum(15);
            QueryMsgDetailsResp q1 = (QueryMsgDetailsResp) p;
            q1.setServiceType("svcTp");
            q1.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534111"));
            q1.addDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "991293211"));
            q1.addDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "991293212"));
            q1.addDestination(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "991293213"));
            q1.setPriority(1);
            q1.setDeliveryTime(new SMPPDate());
            q1.setExpiryTime(new SMPPDate());
            q1.setRegistered(1);
            q1.setReplaceIfPresent(1);
            q1.setMessageText("This is a short message");
            q1.setMessageId("deadbeef");
            q1.setFinalDate(new SMPPDate());
            q1.setMessageStatus(1);
            q1.setErrorCode(2);
            break;

        case SMPPPacket.QUERY_SM:
            p.setSequenceNum(17);
            p.setMessageId("deadbeef");
            p.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534111"));
            break;

        case SMPPPacket.QUERY_SM_RESP:
            p.setSequenceNum(20);
            p.setMessageId("deadbeef");
            p.setFinalDate(new SMPPDate());
            p.setMessageStatus(1);
            p.setErrorCode(4);
            break;

        case SMPPPacket.REPLACE_SM:
            p.setSequenceNum(22);
            p.setMessageId("deadbeef");
            p.setServiceType("svcTp");
            p.setSource(new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "65534111"));
            p.setDeliveryTime(new SMPPDate());
            p.setExpiryTime(new SMPPDate());
            p.setRegistered(1);
            p.setMessageText("This is a short message");
            break;

        default:
            p.setSequenceNum(4);
            break;
        }
    }
}
