package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * SMSC response to a Bind request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class BindResp extends ie.omk.smpp.message.SMPPResponse {
    /** System Id */
    private String sysId;

    /**
     * Construct a new BindResp.
     */
    protected BindResp(int id) {
        super(id);
    }

    /**
     * Create a new BindResp packet in response to a Bind request. This
     * constructor will set the sequence number to that if the packet it is in
     * response to.
     * 
     * @param req
     *            The Request packet the response is to
     */
    public BindResp(Bind req) {
        super(req);
    }

    /**
     * Set the system Id
     * 
     * @param sysId
     *            The new System Id string (Up to 15 characters)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the system id is too long.
     */
    public void setSystemId(String sysId) throws InvalidParameterValueException {
        if (sysId != null) {
            if (version.validateSystemId(sysId)) {
                this.sysId = sysId;
            } else {
                throw new InvalidParameterValueException("Invalid system Id",
                        sysId);
            }
        } else {
            this.sysId = null;
            return;
        }
    }

    /** Get the system Id */
    public String getSystemId() {
        return sysId;
    }

    /**
     * Return the number of bytes this packet would be encoded as to an
     * OutputStream.
     * 
     * @return the number of bytes this packet would encode as.
     */
    public int getBodyLength() {
        // Length of system ID plus a nul terminator.
        return ((sysId != null) ? sysId.length() : 0) + 1;
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
        SMPPIO.writeCString(sysId, out);
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        sysId = SMPPIO.readCString(body, offset);
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("bind_resp");
    }
}

