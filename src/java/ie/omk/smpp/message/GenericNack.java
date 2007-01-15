package ie.omk.smpp.message;

/**
 * Generic negative acknowledgment. Used if the short message entity, either
 * ESME or SMSC, does not understand a message transmitted to it or if a
 * transmitted protocol message is badly formed.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class GenericNack extends SMPPPacket {
    /**
     * Construct a new GenericNack.
     */
    public GenericNack() {
        super(GENERIC_NACK);
    }

    public GenericNack(SMPPPacket request) {
        super(request);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("generic_nack");
    }
}

