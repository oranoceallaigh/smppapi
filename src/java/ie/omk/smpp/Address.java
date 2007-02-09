package ie.omk.smpp;

import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * Object representing a Short Message Entity's address. An address consists of
 * a Type Of Number, a Numbering Plan Indicator and an address.
 * 
 * @see ie.omk.smpp.util.GSMConstants
 */
public class Address implements java.io.Serializable {
    
    static final long serialVersionUID = -1899181032052084902L;
    
    /** Type of number. */
    private int ton = GSMConstants.GSM_TON_UNKNOWN;

    /** Numbering plan indicator. */
    private int npi = GSMConstants.GSM_NPI_UNKNOWN;

    /** The address. */
    private String address = "";

    /**
     * Create a new Address with all nul values. TON will be 0, NPI will be 0
     * and the address field will be blank.
     */
    public Address() {
    }

    /**
     * Create a new Address.
     * 
     * @param ton
     *            The Type Of Number.
     * @param npi
     *            The Numbering Plan Indicator.
     * @param address
     *            The address.
     */
    public Address(int ton, int npi, String address) {
        this.ton = ton;
        this.npi = npi;
        this.address = address;
    }

    /**
     * Get the Type Of Number.
     */
    public int getTON() {
        return ton;
    }

    /**
     * Set the Type of Number.
     */
    public void setTON(int ton) {
        this.ton = ton;
    }

    /**
     * Get the Numbering Plan Indicator.
     */
    public int getNPI() {
        return npi;
    }

    /**
     * Set the Numbering Plan Indicator.
     */
    public void setNPI(int npi) {
        this.npi = npi;
    }

    /**
     * Get the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address.
     */
    public void setAddress(String address) {
        this.address = (address != null) ? address : "";
    }

    /**
     * Get a unique hash code for this address.
     */
    public int hashCode() {
        StringBuffer buf = new StringBuffer();
        buf.append(Integer.toString(ton)).append(':');
        buf.append(Integer.toString(npi)).append(':');
        if (address != null) {
            buf.append(address);
        }

        return buf.hashCode();
    }

    /**
     * Test for equality. Two address objects are equal if their TON, NPI and
     * address fields are equal.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Address) {
            Address a = (Address) obj;
            return (a.ton == ton) && (a.npi == npi) && (a.address.equals(address));
        } else {
            return false;
        }
    }

    /**
     * Get the number of bytes this object would encode to.
     */
    public int getLength() {
        return 3 + address.length();
    }

    /**
     * Encode this object as bytes to the output stream. An address encodes as a
     * single byte for the TON, a single byte for the NPI and a nul-terminated
     * ASCII character string.
     * 
     * @param out
     *            The output stream to encode the address to.
     * @throws java.io.IOException
     *             If an I/O error occurs while writing to the output stream.
     */
    public void writeTo(OutputStream out) throws java.io.IOException {
        SMPPIO.writeByte(ton, out);
        SMPPIO.writeByte(npi, out);
        SMPPIO.writeCString(address, out);
    }

    /**
     * Decode this address from a byte array.
     * 
     * @param addr
     *            The byte array to read the address from.
     * @param position
     *            The position in the array to begin parsing the address from.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             If the byte array does not contain enough bytes to decode an
     *             address.
     */
    public void readFrom(byte[] addr, ParsePosition position) {
        int offset = position.getIndex();
        ton = SMPPIO.bytesToByte(addr, offset + 0);
        npi = SMPPIO.bytesToByte(addr, offset + 1);
        address = SMPPIO.readCString(addr, offset + 2);
        position.inc(address.length() + 3);
    }

    public String toString() {
        return new StringBuffer(25)
        .append(Integer.toString(ton)).append(':')
        .append(Integer.toString(npi)).append(':')
        .append(address).toString();
    }
}
