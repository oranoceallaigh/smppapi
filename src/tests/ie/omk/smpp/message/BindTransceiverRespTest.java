package ie.omk.smpp.message;

import ie.omk.smpp.message.tlv.Tag;

import org.testng.annotations.Test;

@Test
public class BindTransceiverRespTest extends PacketTests<BindTransceiverResp> {

    protected Class<BindTransceiverResp> getPacketType() {
        return BindTransceiverResp.class;
    }

    @Override
    protected BindTransceiverResp getInitialisedPacket() {
        BindTransceiverResp packet = new BindTransceiverResp();
        packet.setSystemId("systemId");
        packet.setTLV(Tag.SC_INTERFACE_VERSION, new Integer(0x50));
        return packet;
    }
}
