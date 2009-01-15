package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class ParamRetrieveRespTest extends PacketTests<ParamRetrieveResp> {

    protected Class<ParamRetrieveResp> getPacketType() {
        return ParamRetrieveResp.class;
    }
    
    @Override
    protected ParamRetrieveResp getInitialisedPacket() {
        ParamRetrieveResp packet = new ParamRetrieveResp();
        packet.setParamValue("paramValue");
        return packet;
    }
}
