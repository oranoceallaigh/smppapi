package ie.omk.smpp.message;

/**
 * Unbind from the SMSC. This operation does not close the network
 * connection...it is valid to issue a new bind command over the same network
 * connection to re-establish SMPP communication with the SMSC.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class Unbind extends ie.omk.smpp.message.SMPPRequest {
    /**
     * Construct a new Unbind.
     */
    public Unbind() {
        super(UNBIND);
    }

    /**
     * Construct a new Unbind with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public Unbind(int seqNum) {
        super(UNBIND, seqNum);
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
        return new String("unbind");
    }
}

