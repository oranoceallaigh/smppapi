package ie.omk.smpp.util;

/**
 * Class representing a message encoded in binary format. This class uses a data
 * coding value of 4 (00000100b), in accordance with GSM 03.38.
 */
public class BinaryEncoding extends ie.omk.smpp.util.MessageEncoding {
    private static final int DCS = 4;

    private static final BinaryEncoding INSTANCE = new BinaryEncoding();

    public BinaryEncoding() {
        super(DCS);
    }

    /**
     * Get the singleton instance of BinaryEncoding.
     * @deprecated
     */
    public static BinaryEncoding getInstance() {
        return INSTANCE;
    }
}

