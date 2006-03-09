package ie.omk.smpp;

import ie.omk.smpp.util.GSMConstants;
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
        SMPPIO.writeInt(ton, 1, out);
        SMPPIO.writeInt(npi, 1, out);
        SMPPIO.writeCString(address, out);
    }

    /**
     * Decode this address from a byte array.
     * 
     * @param addr
     *            The byte array to read the address from.
     * @param offset
     *            The offset within the byte array to begin decoding from.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             If the byte array does not contain enough bytes to decode an
     *             address.
     */
    public void readFrom(byte[] addr, int offset) {
        ton = SMPPIO.bytesToInt(addr, offset++, 1);
        npi = SMPPIO.bytesToInt(addr, offset++, 1);
        address = SMPPIO.readCString(addr, offset);
    }

    public String toString() {
        return new StringBuffer(25).append(Integer.toString(ton)).append(':')
            .append(Integer.toString(npi)).append(':').append(address).toString();
    }

    /**
     * Test driver function. This method checks that serialization and
     * deserialization of instances of this class result in byte arrays and new
     * packets of consistently the same size. It does the same as the NullTest
     * and FullTest packet test classes.
     */
    /*
     * public static final void main(String[] args) {try {
     * System.out.println("Null test:"); Address a = new Address();
     * java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
     * a.writeTo(os); byte[] b = os.toByteArray();
     * 
     * if (b.length == a.getLength()) System.out.println("\tpass 1."); else
     * System.out.println("\tfail 1.");
     * 
     * Address a1 = new Address(); a1.readFrom(b, 0);
     * 
     * if (b.length == a1.getLength()) System.out.println("\tpass 2."); else
     * System.out.println("\tfail 2.");} catch (Exception x) {
     * System.out.println("\texception:"); x.printStackTrace(System.out);}
     * 
     * try {System.out.println("\nFilled test:"); Address a = new Address(2, 2,
     * "4745879345"); java.io.ByteArrayOutputStream os = new
     * java.io.ByteArrayOutputStream(); a.writeTo(os); byte[] b =
     * os.toByteArray();
     * 
     * if (b.length == a.getLength()) System.out.println("\tpass 1."); else
     * System.out.println("\tfail 1.");
     * 
     * Address a1 = new Address(); a1.readFrom(b, 0);
     * 
     * if (b.length == a1.getLength()) System.out.println("\tpass 2."); else
     * System.out.println("\tfail 2.");} catch (Exception x) {
     * System.out.println("\texception:"); x.printStackTrace(System.out);} }
     */
}

