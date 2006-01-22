package ie.omk.smpp.message;

/**
 * Check the link status. This message can originate from either an ESME or the
 * SMSC. It is used to check that the entity at the other end of the link is
 * still alive and responding to messages. Usually used by the SMSC after a
 * period of inactivity to decide whether to close the link.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class EnquireLink extends ie.omk.smpp.message.SMPPRequest {
    /**
     * Construct a new EnquireLink.
     */
    public EnquireLink() {
        super(ENQUIRE_LINK);
    }

    /**
     * Construct a new EnquireLink with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public EnquireLink(int seqNum) {
        super(ENQUIRE_LINK, seqNum);
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
        return new String("enquire_link");
    }
}

