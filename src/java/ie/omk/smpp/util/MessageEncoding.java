package ie.omk.smpp.util;


public abstract class MessageEncoding {

    private int dataCoding = -1;

    protected MessageEncoding(int dataCoding) {
        this.dataCoding = dataCoding;
    }

    /**
     * Get the correct data_coding value for this message encoding type.
     */
    public final int getDataCoding() {
        return dataCoding;
    }

    /**
     * Get the number of bits each encoded message byte represents. This method
     * will almost always return 8. The only known situation at this time where
     * the value should be different is in
     * {@link ie.omk.smpp.util.DefaultAlphabetEncoding}. The reason is that,
     * although the <code>DefaultAlphabetEncoding</code> will generate encoded
     * message byte arrays with one character in each byte location, in reality
     * each byte actually represents only 7-bits. When the message is re-encoded
     * at the SMSC, it will be compressed. This is how the GSM default alphabet
     * can fit 160 characters into a 140-byte payload. Most SMSCs, however,
     * accept the message in its uncompressed form.
     * <p>
     * It is not correct to return a value of 16 from an AlphabetEncoding which
     * encodes characters in 2 or more bytes. The reason that, although a
     * character is encoding as 2 bytes, in the <i>encoded form each byte
     * location still represents 8 bits </i>. This is an important distinction.
     * </p>
     */
    public int getEncodingLength() {
        return 8;
    }
}

