package ie.omk.smpp.message;

/**
 * Abstract parent class of all SMPP Response packets.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class SMPPResponse extends SMPPPacket {
    /**
     * Construct a new SMPPResponse with specified command id.
     */
    protected SMPPResponse(int id) {
        super(id);
    }

    /**
     * Construct a new SMPPResponse with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     */
    protected SMPPResponse(int id, int seqNum) {
        super(id, seqNum);
    }

    /**
     * Create a new SMPPResponse packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param q
     *            The Request packet the response is to
     */
    public SMPPResponse(SMPPRequest q) {
        // Response value is Command ID with Msb set, sequence no. must match
        super(q.getCommandId() | 0x80000000, q.getSequenceNum());
    }

    /**
     * Set the status of this command (header field)
     * 
     * @param s
     *            The value for the status
     */
    public void setCommandStatus(int s) {
        this.commandStatus = s;
    }
}

