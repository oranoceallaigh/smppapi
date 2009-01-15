package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import com.adenki.smpp.BadCommandIDException;
import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.message.CommandId;
import com.adenki.smpp.message.SMPPPacket;

@Test
public class PacketFactoryTest {

    public static final int VENDOR_ID = 0x10201;
    private final int[] allIds = new int[] {
            CommandId.ALERT_NOTIFICATION,
            CommandId.BIND_RECEIVER,
            CommandId.BIND_RECEIVER_RESP,
            CommandId.BIND_TRANSCEIVER,
            CommandId.BIND_TRANSCEIVER_RESP,
            CommandId.BIND_TRANSMITTER,
            CommandId.BIND_TRANSMITTER_RESP,
            CommandId.BROADCAST_SM,
            CommandId.BROADCAST_SM_RESP,
            CommandId.CANCEL_BROADCAST_SM,
            CommandId.CANCEL_BROADCAST_SM_RESP,
            CommandId.CANCEL_SM,
            CommandId.CANCEL_SM_RESP,
            CommandId.DATA_SM,
            CommandId.DATA_SM_RESP,
            CommandId.DELIVER_SM,
            CommandId.DELIVER_SM_RESP,
            CommandId.ENQUIRE_LINK,
            CommandId.ENQUIRE_LINK_RESP,
            CommandId.GENERIC_NACK,
            CommandId.OUTBIND,
            CommandId.PARAM_RETRIEVE,
            CommandId.PARAM_RETRIEVE_RESP,
            CommandId.QUERY_BROADCAST_SM,
            CommandId.QUERY_BROADCAST_SM_RESP,
            CommandId.QUERY_LAST_MSGS,
            CommandId.QUERY_LAST_MSGS_RESP,
            CommandId.QUERY_MSG_DETAILS,
            CommandId.QUERY_MSG_DETAILS_RESP,
            CommandId.QUERY_SM,
            CommandId.QUERY_SM_RESP,
            CommandId.REPLACE_SM,
            CommandId.REPLACE_SM_RESP,
            CommandId.SUBMIT_MULTI,
            CommandId.SUBMIT_MULTI_RESP,
            CommandId.SUBMIT_SM,
            CommandId.SUBMIT_SM_RESP,
            CommandId.UNBIND,
            CommandId.UNBIND_RESP,
    };
    private PacketFactory packetFactory = new PacketFactory();
    
    public void testCreatePackets() throws Exception {
        for (int id : allIds) {
            packetFactory.newInstance(id);
        }
    }
    
    public void testCreateResponses() throws Exception {
        for (int id : allIds) {
            SMPPPacket p = packetFactory.newInstance(id);
            if (p.isResponse()) {
                continue;
            }
            // Commands that have no responses
            if (id == CommandId.ALERT_NOTIFICATION || id == CommandId.OUTBIND) {
                continue;
            }
            p.setSequenceNum(89);
            SMPPPacket o = packetFactory.newResponse(p);
            assertEquals(o.getCommandId(), id | 0x80000000);
            assertEquals(o.getSequenceNum(), p.getSequenceNum());
        }
    }
    
    public void testCreateResponseFailsWithResponse() throws Exception {
        for (int id : allIds) {
            if ((id & 0x80000000) == 0) {
                continue;
            }
            SMPPPacket p = packetFactory.newInstance(id);
            try {
                packetFactory.newResponse(p);
                fail("Should not create a response to a response.");
            } catch (SMPPRuntimeException x) {
                // Pass
            }
        }
    }
    
    public void testCustomCommand() throws Exception {
        try {
            packetFactory.newInstance(VENDOR_ID);
            fail("Vendor ID should not be recognized yet.");
        } catch (BadCommandIDException x) {
            // Pass
        }
        packetFactory.registerVendorPacket(
                VENDOR_ID, VendorRequest.class, VendorResponse.class);
        SMPPPacket packet = packetFactory.newInstance(VENDOR_ID);
        assertTrue(packet instanceof VendorRequest);
        assertEquals(packet.getCommandId(), VENDOR_ID);
        
        packet = packetFactory.newInstance(VENDOR_ID | 0x80000000);
        assertTrue(packet instanceof VendorResponse);
        assertEquals(packet.getCommandId(), VENDOR_ID | 0x80000000);
        
        packet = packetFactory.newInstance(VENDOR_ID);
        packet.setSequenceNum(101);
        SMPPPacket response = packetFactory.newResponse(packet);
        assertTrue(response instanceof VendorResponse);
        assertEquals(response.getSequenceNum(), 101);
    }
}

class VendorRequest extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    VendorRequest() {
        super(PacketFactoryTest.VENDOR_ID);
    }
}
class VendorResponse extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    public VendorResponse() {
        super(PacketFactoryTest.VENDOR_ID | 0x80000000);
    }
    public VendorResponse(SMPPPacket request) {
        super(request);
    }
}
