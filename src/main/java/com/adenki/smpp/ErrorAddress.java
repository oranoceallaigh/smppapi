package com.adenki.smpp;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * An address that message submission was unsuccessfully submitted to. This
 * class is used in the SubmitMultiResp packet type to return a list of SME
 * addresses that message submission failed for along with an error code for
 * each address indicating the reason for the failure.
 * 
 */
public class ErrorAddress extends Address {
    static final long serialVersionUID = 2L;
    
    /**
     * The error code showing why this address failed.
     */
    private long error;

    /**
     * Create a new ErrorAddress object.
     */
    public ErrorAddress() {
    }

    /**
     * Create a new ErrorAddress object.
     * 
     * @param ton
     *            The Type Of Number.
     * @param npi
     *            The Numbering Plan Indicator.
     * @param addr
     *            The address.
     */
    public ErrorAddress(int ton, int npi, String addr) {
        super(ton, npi, addr);
    }

    /**
     * Create a new ErrorAddress object.
     * 
     * @param ton
     *            The Type Of Number.
     * @param npi
     *            The Numbering Plan Indicator.
     * @param addr
     *            The address.
     * @param error
     *            The error code indicating why message submission failed.
     */
    public ErrorAddress(int ton, int npi, String addr, long error) {
        super(ton, npi, addr);
        this.error = error;
    }

    /**
     * Get the error code associated with this ErrorAddress.
     */
    public long getError() {
        return error;
    }

    /**
     * Set the error code associated with this ErrorAddress.
     */
    public void setError(long error) {
        this.error = error;
    }

    public int getLength() {
        return super.getLength() + 4;
    }

    public void writeTo(PacketEncoder encoder) throws java.io.IOException {
        super.writeTo(encoder);
        encoder.writeUInt4(error);
    }

    public void readFrom(PacketDecoder decoder) {
        super.readFrom(decoder);
        error = decoder.readUInt4();
    }
    
    @Override
    public String toString() {
        return new StringBuffer(super.toString())
        .append("/Error=").append(error).toString();
    }
}
