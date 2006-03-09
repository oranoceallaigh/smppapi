package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Query the state of a message. Relevant inherited fields from SMPPPacket: <br>
 * <ul>
 * messageId <br>
 * source <br>
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class QuerySM extends ie.omk.smpp.message.SMPPRequest {
    /**
     * Construct a new QuerySM.
     */
    public QuerySM() {
        super(QUERY_SM);
    }

    /**
     * Construct a new QuerySM with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public QuerySM(int seqNum) {
        super(QUERY_SM, seqNum);
    }

    public int getBodyLength() {
        int len = ((messageId != null) ? messageId.length() : 0)
                + ((source != null) ? source.getLength() : 3);

        // 1 c-string
        return len + 1;
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
        if (source != null) {
            source.writeTo(out);
        } else {
            // Write ton=0(null), npi=0(null), address=\0(nul)
            new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
        }
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        messageId = SMPPIO.readCString(body, offset);
        offset += messageId.length() + 1;

        source = new Address();
        source.readFrom(body, offset);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("query_sm");
    }
}

