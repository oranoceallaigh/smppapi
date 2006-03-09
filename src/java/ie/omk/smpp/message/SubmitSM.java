package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.InvalidDateFormatException;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Submit a message to the SMSC for delivery to a single destination. Relevant
 * inherited fields from SMPPPacket: <br>
 * <ul>
 * serviceType <br>
 * source <br>
 * destination <br>
 * esmClass <br>
 * protocolID <br>
 * priority <br>
 * deliveryTime <br>
 * expiryTime <br>
 * registered <br>
 * replaceIfPresent <br>
 * dataCoding <br>
 * defaultMsg <br>
 * smLength <br>
 * message
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class SubmitSM extends ie.omk.smpp.message.SMPPRequest {
    /**
     * Construct a new SubmitSM.
     */
    public SubmitSM() {
        super(SUBMIT_SM);
    }

    /**
     * Construct a new SubmitSM with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public SubmitSM(int seqNum) {
        super(SUBMIT_SM, seqNum);
    }

    /**
     * Return the number of bytes this packet would be encoded as to an
     * OutputStream.
     * 
     * @return the number of bytes this packet would encode as.
     */
    public int getBodyLength() {
        int len = ((serviceType != null) ? serviceType.length() : 0)
                + ((source != null) ? source.getLength() : 3)
                + ((destination != null) ? destination.getLength() : 3)
                + ((deliveryTime != null) ? deliveryTime.toString().length() : 0)
                + ((expiryTime != null) ? expiryTime.toString().length() : 0)
                + ((message != null) ? message.length : 0);

        // 8 1-byte integers, 3 c-strings
        return len + 8 + 3;
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
        int smLength = 0;
        if (message != null) {
            smLength = message.length;
        }

        SMPPIO.writeCString(serviceType, out);
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

        String dt = (deliveryTime == null) ? "" : deliveryTime.toString();
        String et = (expiryTime == null) ? "" : expiryTime.toString();

        SMPPIO.writeInt(esmClass, 1, out);
        SMPPIO.writeInt(protocolID, 1, out);
        SMPPIO.writeInt(priority, 1, out);
        SMPPIO.writeCString(dt, out);
        SMPPIO.writeCString(et, out);
        SMPPIO.writeInt(registered, 1, out);
        SMPPIO.writeInt(replaceIfPresent, 1, out);
        SMPPIO.writeInt(dataCoding, 1, out);
        SMPPIO.writeInt(defaultMsg, 1, out);
        SMPPIO.writeInt(smLength, 1, out);
        if (message != null) {
            out.write(message);
        }
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        try {
            int smLength = 0;
            String delivery;
            String valid;

            // First the service type
            serviceType = SMPPIO.readCString(body, offset);
            offset += serviceType.length() + 1;

            source = new Address();
            source.readFrom(body, offset);
            offset += source.getLength();

            destination = new Address();
            destination.readFrom(body, offset);
            offset += destination.getLength();

            // ESM class, protocol Id, priorityFlag...
            esmClass = SMPPIO.bytesToInt(body, offset++, 1);
            protocolID = SMPPIO.bytesToInt(body, offset++, 1);
            priority = SMPPIO.bytesToInt(body, offset++, 1);

            delivery = SMPPIO.readCString(body, offset);
            offset += delivery.length() + 1;
            if (delivery.length() > 0) {
                deliveryTime = SMPPDate.parseSMPPDate(delivery);
            }

            valid = SMPPIO.readCString(body, offset);
            offset += valid.length() + 1;
            if (valid.length() > 0) {
                expiryTime = SMPPDate.parseSMPPDate(valid);
            }

            // Registered delivery, replace if present, data coding, default msg
            // and message length
            registered = SMPPIO.bytesToInt(body, offset++, 1);
            replaceIfPresent = SMPPIO.bytesToInt(body, offset++, 1);
            dataCoding = SMPPIO.bytesToInt(body, offset++, 1);
            defaultMsg = SMPPIO.bytesToInt(body, offset++, 1);
            smLength = SMPPIO.bytesToInt(body, offset++, 1);

            if (smLength > 0) {
                message = new byte[smLength];
                System.arraycopy(body, offset, message, 0, smLength);
            }
        } catch (InvalidDateFormatException x) {
            throw new SMPPProtocolException("Unrecognized date format", x);
        }
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("submit_sm");
    }
}

