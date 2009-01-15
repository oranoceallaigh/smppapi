package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class DataSMRespTest extends PacketTests<DataSMResp> {

    protected Class<DataSMResp> getPacketType() {
        return DataSMResp.class;
    }
    
    @Override
    protected DataSMResp getInitialisedPacket() {
        DataSMResp packet = new DataSMResp();
        packet.setMessageId("messageId");
        return packet;
    }
}
