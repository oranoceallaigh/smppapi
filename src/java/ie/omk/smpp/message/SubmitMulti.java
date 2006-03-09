package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.InvalidDateFormatException;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Submit a message to multiple destinations. Relevant inherited fields from
 * SMPPPacket: <br>
 * <ul>
 * serviceType <br>
 * source <br>
 * esmClass <br>
 * protocolID <br>
 * priority <br>
 * deliveryTime <br>
 * expiryTime <br>
 * registered <br>
 * replaceIfPresent <br>
 * dataCoding <br>
 * defaultMsg <br>
 * message <br>
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class SubmitMulti extends ie.omk.smpp.message.SMPPRequest {
    /** Table of destinations */
    private DestinationTable destinationTable = new DestinationTable();

    /**
     * Construct a new SubmitMulti.
     */
    public SubmitMulti() {
        super(SUBMIT_MULTI);
    }

    /**
     * Construct a new SubmitMulti with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public SubmitMulti(int seqNum) {
        super(SUBMIT_MULTI, seqNum);
    }

    /**
     * Get a handle to the error destination table. Applications may add
     * destination addresses or distribution list names to the destination
     * table.
     */
    public DestinationTable getDestinationTable() {
        return destinationTable;
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
                    "Distribution list name is invalid", d);
        }

        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Get the number of destinations in the destination table.
     * 
     * @deprecated Use getNumDests()
     */
    public int getNoOfDests() {
        return destinationTable.size();
    }

    /**
     * Get the number of destinations in the destination table.
     */
    public int getNumDests() {
        return destinationTable.size();
    }

    /**
     * Get an array of the Address(es) in the destination table.
     * 
     * @return Array of Addresses in the destination table (never null)
     */
    /*
     * public Address[] getDestAddresses() {Address sd[]; int loop = 0;
     * 
     * synchronized (destinationTable) {if(destinationTable.size() == 0) return
     * (new Address[0]);
     * 
     * sd = new Address[destinationTable.size()]; Iterator i =
     * destinationTable.iterator(); while (i.hasNext()) sd[loop++] =
     * (Address)i.next();}
     * 
     * return sd;}
     */

    /**
     * Return the number of bytes this packet would be encoded as to an
     * OutputStream.
     * 
     * @return the number of bytes this packet would encode as.
     */
    public int getBodyLength() {
        int size = ((serviceType != null) ? serviceType.length() : 0)
                + ((source != null) ? source.getLength() : 3)
                + ((deliveryTime != null) ? deliveryTime.toString().length() : 0)
                + ((expiryTime != null) ? expiryTime.toString().length() : 0)
                + ((message != null) ? message.length : 0);

        size += destinationTable.getLength();

        // 9 1-byte integers, 4 c-strings
        return size + 9 + 3;
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

        // Get a clone of the table that can't be changed while writing..
        DestinationTable table = (DestinationTable) this.destinationTable.clone();

        SMPPIO.writeCString(serviceType, out);
        if (source != null) {
            source.writeTo(out);
        } else {
            // Write ton=0(null), npi=0(null), address=\0(nul)
            new Address(GSMConstants.GSM_TON_UNKNOWN,
                    GSMConstants.GSM_NPI_UNKNOWN, "").writeTo(out);
        }

        int numDests = table.size();
        SMPPIO.writeInt(numDests, 1, out);
        table.writeTo(out);

        String dt = (deliveryTime == null) ? null : deliveryTime.toString();
        String et = (expiryTime == null) ? null : expiryTime.toString();

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
            int numDests = 0;
            int smLength = 0;
            String delivery;
            String valid;

            // First the service type
            serviceType = SMPPIO.readCString(body, offset);
            offset += serviceType.length() + 1;

            source = new Address();
            source.readFrom(body, offset);
            offset += source.getLength();

            // Read in the number of destination structures to follow:
            numDests = SMPPIO.bytesToInt(body, offset++, 1);

            // Now read in numDests number of destination structs
            DestinationTable dt = new DestinationTable();
            dt.readFrom(body, offset, numDests);
            offset += dt.getLength();
            this.destinationTable = dt;

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
        return new String("submit_multi");
    }
}

