package com.adenki.smpp.encoding;


public abstract class AbstractMessageEncoding<T> implements MessageEncoding<T> {

    private int dataCoding = -1;

    protected AbstractMessageEncoding(int dataCoding) {
        this.dataCoding = dataCoding;
    }

    /**
     * Get the correct data_coding value for this message encoding type.
     */
    public final int getDataCoding() {
        return dataCoding;
    }
}
