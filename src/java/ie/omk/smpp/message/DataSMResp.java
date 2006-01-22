package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Response to a data_sm.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class DataSMResp extends ie.omk.smpp.message.SMPPResponse {
    /**
     * Construct a new DataSMResp.
     */
    public DataSMResp() {
        super(DATA_SM_RESP);
    }

    /**
     * Construct a new DataSMResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public DataSMResp(int seqNum) {
        super(DATA_SM_RESP, seqNum);
    }

    /**
     * Create a new DataSMResp packet in response to a DataSM. This constructor
     * will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public DataSMResp(DataSM r) {
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
        return new String("data_sm_resp");
    }
}

