package ie.omk.smpp.message;

/**
 * ESME or SMSC response to an EnquireLink request. Used to positivly
 * acknowledge that this entity is still alive and capable of submitting, or
 * responding to, SMPP messages.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class EnquireLinkResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new EnquireLinkResp.
     */
    public EnquireLinkResp() {
        super(ENQUIRE_LINK_RESP);
    }

    /**
     * Construct a new EnquireLinkResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public EnquireLinkResp(int seqNum) {
        super(ENQUIRE_LINK_RESP, seqNum);
    }

    /**
     * Create a new BindReceiverResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public EnquireLinkResp(EnquireLink r) {
        super(r);
    }

    public int getBodyLength() {
        return 0;
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("enquire_link_resp");
    }
}

