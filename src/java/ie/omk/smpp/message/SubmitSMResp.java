package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Submit short message response.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class SubmitSMResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new SubmitSMResp.
     */
    public SubmitSMResp() {
        super(SUBMIT_SM_RESP);
    }

    /**
     * Construct a new SubmitSMResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public SubmitSMResp(int seqNum) {
        super(SUBMIT_SM_RESP, seqNum);
    }

    /**
     * Create a new SubmitSMResp packet in response to a SubmitSM. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public SubmitSMResp(SubmitSM r) {
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
     *             If an error occurs writing to the output stream.
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
        return new String("submit_sm_resp");
    }
}

