package ie.omk.smpp.message;

/**
 * Unbind from the SMSC. This operation does not close the network
 * connection...it is valid to issue a new bind command over the same network
 * connection to re-establish SMPP communication with the SMSC.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class Unbind extends SMPPPacket {
    /**
     * Construct a new Unbind.
     */
    public Unbind() {
        super(UNBIND);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("unbind");
    }
}
