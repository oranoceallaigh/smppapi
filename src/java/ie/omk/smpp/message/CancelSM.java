package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Cancal message. This SMPP message is used to cancel a previously submitted
 * but yet undelivered short message at the SMSC. Relevant inherited fields from
 * SMPPPacket: <br>
 * <ul>
 * serviceType <br>
 * messageId <br>
 * source <br>
 * destination <br>
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class CancelSM extends ie.omk.smpp.message.SMPPRequest {
    /**
     * Construct a new CancelSM.
     */
    public CancelSM() {
        super(CANCEL_SM);
    }

    /**
     * Construct a new CancelSM with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public CancelSM(int seqNum) {
        super(CANCEL_SM, seqNum);
    }

    public int getBodyLength() {
        int len = ((serviceType != null) ? serviceType.length() : 0)
                + ((messageId != null) ? messageId.length() : 0)
                + ((source != null) ? source.getLength() : 3)
                + ((destination != null) ? destination.getLength() : 3);

        // 2 c-strings
        return len + 2;
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
        SMPPIO.writeCString(serviceType, out);
        SMPPIO.writeCString(getMessageId(), out);
        if (source != null) {
            source.writeTo(out);
        } else {
            // Write ton=0(null), npi=0(null), address=\0(nul)
            new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
        }

        if (destination != null) {
            destination.writeTo(out);
        } else {
            // Write ton=0(null), npi=0(null), address=\0(nul)
            new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
        }
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        serviceType = SMPPIO.readCString(body, offset);
        offset += serviceType.length() + 1;

        messageId = SMPPIO.readCString(body, offset);
        offset += messageId.length() + 1;

        source = new Address();
        source.readFrom(body, offset);
        offset += source.getLength();

        destination = new Address();
        destination.readFrom(body, offset);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("cancel_sm");
    }
}

