package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.InvalidDateFormatException;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Response to Query message details. Gives all details of a specified message
 * at the SMSC. Relevant inherited fields from SMPPPacket: <br>
 * <ul>
 * serviceType <br>
 * source <br>
 * protocolID <br>
 * priority <br>
 * deliveryTime <br>
 * expiryTime <br>
 * registered <br>
 * dataCoding <br>
 * message <br>
 * messageId <br>
 * finalDate <br>
 * messageStatus <br>
 * errorCode <br>
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class QueryMsgDetailsResp extends ie.omk.smpp.message.SMPPResponse {
    /** Table of destinations the message was routed to */
    private DestinationTable destinationTable = new DestinationTable();

    /**
     * Construct a new QueryMsgDetailsResp.
     */
    public QueryMsgDetailsResp() {
        super(QUERY_MSG_DETAILS_RESP);
    }

    /**
     * Construct a new QueryMsgDetailsResp with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public QueryMsgDetailsResp(int seqNum) {
        super(QUERY_MSG_DETAILS_RESP, seqNum);
    }

    /**
     * Create a new QueryMsgDetailsResp packet in response to a BindReceiver.
     * This constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public QueryMsgDetailsResp(QueryMsgDetails r) {
        super(r);

        // These are the only fields that can be got from the request packet
        messageId = r.getMessageId();
        source = r.getSource();
    }

    /**
     * Add an address to the destination table.
     * 
     * @param d
     *            The SME destination address
     * @return The current number of destination addresses (including the new
     *         one).
     * @see Address
     */
    public int addDestination(Address d) {
        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Add a distribution list to the destination table.
     * 
     * @param d
     *            the distribution list name.
     * @return The current number of destination addresses (including the new
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the distribution list name is too long.
     */
    public int addDestination(String d) throws InvalidParameterValueException {
        if (!version.validateDistListName(d)) {
            throw new InvalidParameterValueException(
                    "Distribution list is invalid", d);
        }

        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Get the current number of destination addresses.
     * 
     * @deprecated Use getNumDests.
     */
    public int getNoOfDests() {
        return destinationTable.size();
    }

    /**
     * Get the current number of destination addresses.
     */
    public int getNumDests() {
        return destinationTable.size();
    }

    /**
     * Get a handle to the destination table.
     */
    public DestinationTable getDestinationTable() {
        return destinationTable;
    }

    public int getBodyLength() {
        int size = ((serviceType != null) ? serviceType.length() : 0)
                + ((source != null) ? source.getLength() : 3)
                + ((deliveryTime != null) ? deliveryTime.toString().length() : 0)
                + ((expiryTime != null) ? expiryTime.toString().length() : 0)
                + ((message != null) ? message.length : 0)
                + ((messageId != null) ? messageId.length() : 0)
                + ((finalDate != null) ? finalDate.toString().length() : 0);

        size += destinationTable.getLength();

        // 8 1-byte integers, 5 c-strings
        return size + 8 + 5;
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

        DestinationTable table = (DestinationTable) destinationTable.clone();
        int numDests = table.size();
        SMPPIO.writeInt(numDests, 1, out);
        table.writeTo(out);

        String dt = (deliveryTime == null) ? null : deliveryTime.toString();
        String et = (expiryTime == null) ? null : expiryTime.toString();
        String fd = (finalDate == null) ? null : finalDate.toString();

        SMPPIO.writeInt(protocolID, 1, out);
        SMPPIO.writeInt(priority, 1, out);
        SMPPIO.writeCString(dt, out);
        SMPPIO.writeCString(et, out);
        SMPPIO.writeInt(registered, 1, out);
        SMPPIO.writeInt(dataCoding, 1, out);
        SMPPIO.writeInt(smLength, 1, out);
        if (message != null) {
            out.write(message);
        }
        SMPPIO.writeCString(getMessageId(), out);
        SMPPIO.writeCString(fd, out);
        SMPPIO.writeInt(messageStatus, 1, out);
        SMPPIO.writeInt(errorCode, 1, out);
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        try {
            int numDests = 0;
            int smLength = 0;
            String delivery;
            String valid;
            String finalD;

            serviceType = SMPPIO.readCString(body, offset);
            offset += serviceType.length() + 1;

            source = new Address();
            source.readFrom(body, offset);
            offset += source.getLength();

            numDests = SMPPIO.bytesToInt(body, offset++, 1);
            DestinationTable dt = new DestinationTable();
            dt.readFrom(body, offset, numDests);
            offset += dt.getLength();
            this.destinationTable = dt;

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

            registered = SMPPIO.bytesToInt(body, offset++, 1);
            dataCoding = SMPPIO.bytesToInt(body, offset++, 1);
            smLength = SMPPIO.bytesToInt(body, offset++, 1);

            if (smLength > 0) {
                message = new byte[smLength];
                System.arraycopy(body, offset, message, 0, smLength);
                offset += smLength;
            }

            messageId = SMPPIO.readCString(body, offset);
            offset += messageId.length() + 1;

            finalD = SMPPIO.readCString(body, offset);
            offset += finalD.length() + 1;
            if (finalD.length() > 0) {
                finalDate = SMPPDate.parseSMPPDate(finalD);
            }

            messageStatus = SMPPIO.bytesToInt(body, offset++, 1);
            errorCode = SMPPIO.bytesToInt(body, offset++, 1);
        } catch (InvalidDateFormatException x) {
            throw new SMPPProtocolException("Unrecognized date format", x);
        }
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("query_msg_details_resp");
    }
}

