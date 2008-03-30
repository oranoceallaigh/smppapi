package ie.omk.smpp.encoding;


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

    // TODO document
    public int getCharSize() {
        return 8;
    }
}

