package com.adenki.smpp.net;

import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.util.PacketFactory;

/**
 * Base class for {@link SmscLink} implementations.
 * @version $Id$
 */
public abstract class AbstractSmscLink implements SmscLink {

    /**
     * Packet factory used to create new {@link SMPPPacket} instances.
     */
    protected PacketFactory packetFactory = new PacketFactory();

    protected AbstractSmscLink() {
    }

    public PacketFactory getPacketFactory() {
        return packetFactory;
    }

    public void setPacketFactory(PacketFactory packetFactory) {
        this.packetFactory = packetFactory;
    }
}
