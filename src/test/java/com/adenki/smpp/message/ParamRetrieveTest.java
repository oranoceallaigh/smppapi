package com.adenki.smpp.message;

import org.testng.annotations.Test;

@Test
public class ParamRetrieveTest extends PacketTests<ParamRetrieve> {

    protected Class<ParamRetrieve> getPacketType() {
        return ParamRetrieve.class;
    }
    
    @Override
    protected ParamRetrieve getInitialisedPacket() {
        ParamRetrieve packet = new ParamRetrieve();
        packet.setParamName("paramName");
        return packet;
    }
}
