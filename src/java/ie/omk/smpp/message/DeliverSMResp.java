package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * ESME response to a Deliver message request. Relevant inherited fields from
 * SMPPPacket: <br>
 * <ul>
 * messageId <br>
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class DeliverSMResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new DeliverSMResp.
     */
    public DeliverSMResp() {
        super(DELIVER_SM_RESP);
    }

    /**
     * Construct a new DeliverSMResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public DeliverSMResp(int seqNum) {
        super(DELIVER_SM_RESP, seqNum);
    }

    /**
     * Create a new DeliverSMResp packet in response to a DeliverSM. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public DeliverSMResp(DeliverSM r) {
        super(r);
    }

    public int getBodyLength() {
        return ((messageId != null) ? messageId.length() : 0) + 1;
    }

    /**
     * Write a byte representation of this packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to write to
     * @throws java.io.IOException
     *             if there's an error writing to the output stream.
     */
    protected void encodeBody(OutputStream out) throws java.io.IOException {
        SMPPIO.writeCString(getMessageId(), out);
    }

    public void readBodyFrom(byte[] b, int offset) throws SMPPProtocolException {
        messageId = SMPPIO.readCString(b, offset);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("deliver_sm_resp");
    }
}

