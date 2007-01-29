package ie.omk.smpp;

import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * An address that message submission was unsuccessfully submitted to. This
 * class is used in the SubmitMultiResp packet type to return a list of SME
 * addresses that message submission failed for along with an error code for
 * each address indicating the reason for the failure.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class ErrorAddress extends Address {
    static final long serialVersionUID = 2352811393926037102L;
    
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

    public void writeTo(OutputStream out) throws java.io.IOException {
        super.writeTo(out);
        SMPPIO.writeLongInt(error, out);
    }

    public void readFrom(byte[] ea, ParsePosition position) {
        super.readFrom(ea, position);
        error = SMPPIO.bytesToLongInt(ea, position.getIndex());
        position.inc(4);
    }
}
