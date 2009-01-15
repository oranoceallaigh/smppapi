package com.adenki.smpp.encoding;

/**
 * Class representing a message encoded in binary format. This class uses a data
 * coding value of 4 (00000100b), in accordance with GSM 03.38.
 */
public class BinaryEncoding extends AbstractMessageEncoding<byte[]> {
    private static final int DCS = 4;

    public BinaryEncoding() {
        super(DCS);
    }
    
    public byte[] decode(byte[] bytes) {
        return bytes;
    }
    
    public byte[] encode(byte[] object) {
        return object;
    }
}
