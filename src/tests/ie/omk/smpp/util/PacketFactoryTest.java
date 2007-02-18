package ie.omk.smpp.util;

import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.SMPPRuntimeException;
import ie.omk.smpp.message.SMPPPacket;
import junit.framework.TestCase;

public class PacketFactoryTest extends TestCase {

    public static final int VENDOR_ID = 0x10201;
    private final int[] allIds = new int[] {
            SMPPPacket.ALERT_NOTIFICATION,
            SMPPPacket.BIND_RECEIVER,
            SMPPPacket.BIND_RECEIVER_RESP,
            SMPPPacket.BIND_TRANSCEIVER,
            SMPPPacket.BIND_TRANSCEIVER_RESP,
            SMPPPacket.BIND_TRANSMITTER,
            SMPPPacket.BIND_TRANSMITTER_RESP,
            SMPPPacket.CANCEL_SM,
            SMPPPacket.CANCEL_SM_RESP,
            SMPPPacket.DATA_SM,
            SMPPPacket.DATA_SM_RESP,
            SMPPPacket.DELIVER_SM,
            SMPPPacket.DELIVER_SM_RESP,
            SMPPPacket.ENQUIRE_LINK,
            SMPPPacket.ENQUIRE_LINK_RESP,
            SMPPPacket.GENERIC_NACK,
            SMPPPacket.OUTBIND,
            SMPPPacket.PARAM_RETRIEVE,
            SMPPPacket.PARAM_RETRIEVE_RESP,
            SMPPPacket.QUERY_LAST_MSGS,
            SMPPPacket.QUERY_LAST_MSGS_RESP,
            SMPPPacket.QUERY_MSG_DETAILS,
            SMPPPacket.QUERY_MSG_DETAILS_RESP,
            SMPPPacket.QUERY_SM,
            SMPPPacket.QUERY_SM_RESP,
            SMPPPacket.REPLACE_SM,
            SMPPPacket.REPLACE_SM_RESP,
            SMPPPacket.SUBMIT_MULTI,
            SMPPPacket.SUBMIT_MULTI_RESP,
            SMPPPacket.SUBMIT_SM,
            SMPPPacket.SUBMIT_SM_RESP,
            SMPPPacket.UNBIND,
            SMPPPacket.UNBIND_RESP,
    };
    
    public void testCreatePackets() throws Exception {
        for (int id : allIds) {
            PacketFactory.newInstance(id);
        }
    }
    
    public void testCreateResponses() throws Exception {
        for (int id : allIds) {
            SMPPPacket p = PacketFactory.newInstance(id);
            if (p.isResponse()) {
                continue;
            }
            // Commands that have no responses
            if (id == SMPPPacket.ALERT_NOTIFICATION || id == SMPPPacket.OUTBIND) {
                continue;
            }
            p.setSequenceNum(89);
            SMPPPacket o = PacketFactory.newResponse(p);
            assertEquals(id | 0x80000000, o.getCommandId());
            assertEquals(p.getSequenceNum(), o.getSequenceNum());
        }
    }
    
    public void testCreateResponseFailsWithResponse() throws Exception {
        for (int id : allIds) {
            if ((id & 0x80000000) == 0) {
                continue;
            }
            SMPPPacket p = PacketFactory.newInstance(id);
            try {
                PacketFactory.newResponse(p);
                fail("Should not create a response to a response.");
            } catch (SMPPRuntimeException x) {
                // Pass
            }
        }
    }
    
    public void testCustomCommand() throws Exception {
        try {
            PacketFactory.newInstance(VENDOR_ID);
            fail("Vendor ID should not be recognized yet.");
        } catch (BadCommandIDException x) {
            // Pass
        }
        PacketFactory.registerVendorPacket(
                VENDOR_ID, VendorRequest.class, VendorResponse.class);
        SMPPPacket packet = PacketFactory.newInstance(VENDOR_ID);
        assertTrue(packet instanceof VendorRequest);
        assertEquals(VENDOR_ID, packet.getCommandId());
        
        packet = PacketFactory.newInstance(VENDOR_ID | 0x80000000);
        assertTrue(packet instanceof VendorResponse);
        assertEquals(VENDOR_ID | 0x80000000, packet.getCommandId());
        
        packet = PacketFactory.newInstance(VENDOR_ID);
        packet.setSequenceNum(101);
        SMPPPacket response = PacketFactory.newResponse(packet);
        assertTrue(response instanceof VendorResponse);
        assertEquals(101, response.getSequenceNum());
    }
}

class VendorRequest extends SMPPPacket {
    VendorRequest() {
        super(PacketFactoryTest.VENDOR_ID);
    }
}
class VendorResponse extends SMPPPacket {
    public VendorResponse() {
        super(PacketFactoryTest.VENDOR_ID | 0x80000000);
    }
    public VendorResponse(SMPPPacket request) {
        super(request);
    }
}