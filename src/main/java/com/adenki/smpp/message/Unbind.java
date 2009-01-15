package com.adenki.smpp.message;

/**
 * Unbind from the SMSC. This operation does not close the network
 * connection...it is valid to issue a new bind command over the same network
 * connection to re-establish SMPP communication with the SMSC.
 * 
 * @version $Id$
 */
public class Unbind extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    /**
     * Construct a new Unbind.
     */
    public Unbind() {
        super(CommandId.UNBIND);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 89;
    }
}
