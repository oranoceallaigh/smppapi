package ie.omk.smpp.message;

/**
 * Abstract parent class of all SMPP request packets.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class SMPPRequest extends SMPPPacket {
    /** false if this packet has been ack'd, true if it has */
    protected boolean isAckd;

    /**
     * Construct a new SMPPRequest with specified id.
     */
    protected SMPPRequest(int id) {
        super(id);
    }

    /**
     * Construct a new SMPPRequest with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     */
    protected SMPPRequest(int id, int seqNum) {
        super(id, seqNum);
    }

    public final boolean isRequest() {
        return true;
    }
    
    /**
     * Check has this request been acknowledged or not.
     */
    public final boolean isAckd() {
        return isAckd;
    }

    /**
     * Set this request packet to acknowledged.
     */
    public final void ack() {
        isAckd = true;
    }
}

