package ie.omk.smpp.message;

/**
 * Generic negative acknowledgment. Used if the short message entity, either
 * ESME or SMSC, does not understand a message transmitted to it or if a
 * transmitted protocol message is badly formed.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class GenericNack extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new GenericNack.
     */
    public GenericNack() {
        super(GENERIC_NACK);
    }

    /**
     * Construct a new GenericNack with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public GenericNack(int seqNum) {
        super(GENERIC_NACK, seqNum);
    }

    public int getBodyLength() {
        return 0;
    }

    public void readBodyFrom(byte[] b, int offset) throws SMPPProtocolException {
        return;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("generic_nack");
    }
}

